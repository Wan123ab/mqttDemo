package com.wq.mqttdemo.constant;

/**
 * MQTT常量接口
 */
public interface MqttConstant {

    String brokerPrefixUrl = "http://127.0.0.1:18083";

    //获取集群内指定客户端的信息
    String CLIENT_INFO = "/api/v2/clients/";

    //断开集群内指定客户端连接
    String DISCONNECT_CLIENT = "/api/v2/clients/";

    //发布消息
    String PUBLISH = "/api/v2/mqtt/publish";

    //订阅主题
    String SUBSCRIBE = "/api/v2/mqtt/subscribe";

    //取消订阅
    String UNSUBSCRIBE = "/api/v2/mqtt/unsubscribe";


}
