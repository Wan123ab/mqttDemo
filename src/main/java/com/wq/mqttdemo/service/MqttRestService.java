package com.wq.mqttdemo.service;

import com.alibaba.fastjson.JSONObject;
import com.wq.mqttdemo.constant.MqttConstant;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RestTemplate;

/**
 * 调用MQTT对外暴露Rest Api服务类
 */
public class MqttRestService {

    private static RestTemplate restTemplate;

    static {

        restTemplate = new RestTemplate();
        //访问http://127.0.0.1:18083/** 有登录认证，此处添加1个登录认证拦截器
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor("admin", "public"));
    }

    public static JSONObject subscribe(String clientId, String topic) throws Exception {
        return subscribe(clientId, 2, topic);
    }

    public static JSONObject subscribe(String clientId, int qos, String topic) throws Exception {
        String reqUrl = MqttConstant.brokerPrefixUrl + MqttConstant.SUBSCRIBE;
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("client_id", clientId);
        jsonParam.put("qos", qos);
        jsonParam.put("topic", topic);
        return restTemplate.postForObject(reqUrl, jsonParam, JSONObject.class);
    }

    /**
     * 取消订阅
     */
    public static JSONObject unSubscribe(String clientId, String topic) throws Exception {
        String reqUrl = MqttConstant.brokerPrefixUrl + MqttConstant.SUBSCRIBE;
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("topic", topic);
        jsonParam.put("client_id", clientId);
        return restTemplate.postForObject(reqUrl, jsonParam, JSONObject.class);
    }

    /**
     * 获取集群内指定客户端的会话信息
     */
    public static JSONObject getClientInfo(String clientId) throws Exception {
        String reqUrl = MqttConstant.brokerPrefixUrl + MqttConstant.CLIENT_INFO  + clientId;
        return restTemplate.getForObject(reqUrl, JSONObject.class);
    }

    /**
     * 断开集群内指定客户端连接
     */
    public static JSONObject disConnectClient(String clientId) throws Exception {
        String reqUrl = MqttConstant.brokerPrefixUrl + MqttConstant.DISCONNECT_CLIENT  + clientId;
        return restTemplate.execute(reqUrl, HttpMethod.DELETE, null, new HttpMessageConverterExtractor<JSONObject>(JSONObject.class, restTemplate.getMessageConverters()), (Object) null);
    }

    /**
     * 发布消息
     */
    public static void publish(String topic, Object obj) throws Exception {
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("topic", topic);
        jsonParam.put("payload", JSONObject.toJSONString(obj));
        jsonParam.put("qos", 2);
        restTemplate.postForObject(MqttConstant.brokerPrefixUrl + MqttConstant.PUBLISH, jsonParam, JSONObject.class);
    }


}
