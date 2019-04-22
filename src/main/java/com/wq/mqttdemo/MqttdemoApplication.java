package com.wq.mqttdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource(encoding = "UTF-8", value = {"classpath:mqtt.properties"})
public class MqttdemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MqttdemoApplication.class, args);
    }

}
