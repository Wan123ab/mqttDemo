package com.wq.mqttdemo.log4j;

import com.alibaba.fastjson.JSONObject;
import com.wq.mqttdemo.config.Springconfig;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.ErrorCode;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Enumeration;

/**
 * 监听日志并发送到mqtt（暂未用到）
 */
@Component
public class MQTTAppenderSkeleton extends AppenderSkeleton {

    @Autowired
    private Springconfig.MqttGateway mqttGateway;


    @Override
    protected void append(LoggingEvent loggingEvent) {

        parseAndSendLogToMqtt(loggingEvent);

    }

    private void parseAndSendLogToMqtt(LoggingEvent event) {
        ThrowableInformation throwableInformation = event.getThrowableInformation();
        if (throwableInformation != null) {
            LocationInfo locationInfo = event.getLocationInformation();
            Throwable throwable = throwableInformation.getThrowable();
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject();

                jsonObject.put("className", locationInfo.getClassName());
                jsonObject.put("fileName", locationInfo.getFileName());
                jsonObject.put("lineNumber", locationInfo.getLineNumber());
                jsonObject.put("methodName", locationInfo.getMethodName());
                jsonObject.put("serverIp", getIp());
                jsonObject.put("logName", event.getLogger().getName());
                jsonObject.put("logLevel", event.getLevel());
                jsonObject.put("logThread", event.getThreadName());
                jsonObject.put("logMills", new Date(event.getTimeStamp()));
                jsonObject.put("logMessage", event.getMessage().toString());
                jsonObject.put("throwMessage", throwable.getMessage());
                jsonObject.put("throwDetailMessage", throwable.toString());
                jsonObject.put("throwStackTrace", throwable.getStackTrace());

                mqttGateway.sendToMqtt("sysLog",jsonObject.toJSONString());
            } catch (IOException e) {
                errorHandler.error("Error parseAndSendLogToMqtt", e, ErrorCode.GENERIC_FAILURE);
            }
        }
    }

    private String getIp() throws UnknownHostException {
        try {
            InetAddress candidateAddress = null;
            // 遍历所有的网络接口
            for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements(); ) {
                NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
                // 在所有的接口下再遍历IP
                for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
                    InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                    if (!inetAddr.isLoopbackAddress()) {// 排除loopback类型地址
                        if (inetAddr.isSiteLocalAddress()) {
                            // 如果是site-local地址，就是它了
                            return inetAddr.getHostAddress();
                        } else if (candidateAddress == null) {
                            // site-local类型的地址未被发现，先记录候选地址
                            candidateAddress = inetAddr;
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                return candidateAddress.getHostAddress();
            }
            // 如果没有发现 non-loopback地址.只能用最次选的方案
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            if (jdkSuppliedAddress == null) {
                throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
            }
            return jdkSuppliedAddress.getHostAddress();
        } catch (Exception e) {
            UnknownHostException unknownHostException = new UnknownHostException(
                    "Failed to determine LAN address: " + e);
            unknownHostException.initCause(e);
            throw unknownHostException;
        }
    }

    @Override
    public void close() {

        this.closed = true;

    }

    @Override
    public boolean requiresLayout() {
        return false;
    }
}
