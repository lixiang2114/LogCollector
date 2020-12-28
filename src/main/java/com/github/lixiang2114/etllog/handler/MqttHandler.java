package com.github.lixiang2114.etllog.handler;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Lixiang
 * @description Mqtt协议操作句柄
 */
public class MqttHandler implements MqttCallbackExtended{
	/**
	 * 日志工具
	 */
	private static final Logger log=LoggerFactory.getLogger(MqttHandler.class);
	
	@Override
	public void connectionLost(Throwable cause) {
		log.error("push-MQTT客户端断开连接...");
		cause.printStackTrace();
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {}

	@Override
	public void connectComplete(boolean reconnect, String serverURI) {}
}
