package com.github.lixiang2114.etllog.handler;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * @author Lixiang
 * @description Mqtt协议操作句柄
 */
public class MqttHandler implements MqttCallbackExtended{
	@Override
	public void connectionLost(Throwable cause) {
		System.out.println("push-MQTT客户端断开连接...");
		cause.printStackTrace();
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {}

	@Override
	public void connectComplete(boolean reconnect, String serverURI) {}
}
