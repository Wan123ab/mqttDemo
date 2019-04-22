package com.wq.mqttdemo.controller;

import com.wq.mqttdemo.config.Springconfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MqttController {

    @Autowired
    private Springconfig.MqttGateway mqttGateway;

    @RequestMapping("/send/{topic}/{message}")
    public ResponseEntity<String> send(@PathVariable String topic, @PathVariable String message) {
        mqttGateway.sendToMqtt(topic, message);

        return new ResponseEntity<>(message, HttpStatus.OK);
    }




}
