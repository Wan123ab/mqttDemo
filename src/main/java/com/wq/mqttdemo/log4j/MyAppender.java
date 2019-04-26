package com.wq.mqttdemo.log4j;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.AppenderBase;
import com.alibaba.fastjson.JSONObject;
import com.wq.mqttdemo.service.MqttRestService;
import com.wq.mqttdemo.service.WqMqttClient;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * 自定义logback的Appender，将错误日志按照指定格式发送到MQTT服务器
 */
public class MyAppender extends AppenderBase<LoggingEvent> {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private RestTemplate restTemplate;

    private WqMqttClient mqttClient ;

    @Override
    public void start() {
        super.start();
        restTemplate = new RestTemplate();
        //访问http://127.0.0.1:18083/** 有登录认证，此处添加1个登录认证拦截器
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor("admin", "public"));

        mqttClient = new WqMqttClient();
    }


    @Override
    protected void append(LoggingEvent event) {

        ThrowableProxy throwableProxy = (ThrowableProxy) event.getThrowableProxy();
        Throwable throwable = throwableProxy.getThrowable();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("logLevel", event.getLevel().levelStr);
        jsonObject.put("logThread", event.getThreadName());
        jsonObject.put("logMills", sdf.format(new Date(event.getTimeStamp())));
        jsonObject.put("logMessage", event.getFormattedMessage());
        jsonObject.put("logName", event.getLoggerName());
        jsonObject.put("throwClass", throwableProxy.getClassName());
        jsonObject.put("throwDetailMessage", throwable.toString());
        jsonObject.put("throwStackTrace", parseException(throwable.getStackTrace()));

//        System.out.println(jsonObject);
        try {
            //1、通过Mqtt客户端发送消息
            publish("sysLog",jsonObject.toJSONString());

            //2、通过Mqtt对外Rest Api推送消息
            MqttRestService.publish("topic1",jsonObject.toJSONString());


            //3、通过Mqtt对外Rest Api推送消息
            mqttClient.publish("topic2",jsonObject.toJSONString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String parseException(StackTraceElement[] stackTrace) {

        StringBuffer sb = new StringBuffer();
        Arrays.stream(stackTrace).forEach((e) -> {
            sb.append(e.getClassName() + "." + e.getMethodName() + "(" + e.getFileName() + ":" + e.getLineNumber() + ")").append("\r\n");
        });

        return sb.toString();

    }

    @Override
    public void stop() {
        super.stop();
    }

    public void publish(String topic, Object obj) throws Exception {
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("topic", topic);
        jsonParam.put("payload", JSONObject.toJSONString(obj));
        jsonParam.put("qos", 2);
        System.out.println(jsonParam);
        restTemplate.postForObject("http://127.0.0.1:18083" + "/api/v2/mqtt/publish", jsonParam, JSONObject.class);
    }

}
