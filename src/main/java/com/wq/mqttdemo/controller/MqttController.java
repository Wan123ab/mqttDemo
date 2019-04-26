package com.wq.mqttdemo.controller;

import com.wq.mqttdemo.config.Springconfig;
import com.wq.mqttdemo.service.MqttService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class MqttController {

    @Autowired
    private Springconfig.MqttGateway mqttGateway;

    @Autowired
    private MqttService mqttService;

    @RequestMapping("/send/{topic}/{message}")
    public ResponseEntity<String> send(@PathVariable String topic, @PathVariable String message) {
        mqttGateway.sendToMqtt(topic, message);

        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @GetMapping("/log")
    public  void testLog() throws Exception{

        log.info("测试日志输出");

        try {
            mqttService.log();
        } catch (Exception e) {
            log.error("出异常啦,{}","哈哈",e);
//            throw e;
        }

    }




}
