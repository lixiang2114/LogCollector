package com.github.lixiang2114.etllog.thread;

import java.util.concurrent.Future;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.github.lixiang2114.etllog.context.ContextConfig;
import com.github.lixiang2114.etllog.handler.TokenExpireHandler;

/**
 * @author Lixiang
 * @description 平滑关闭ETL服务
 */
public class GracefulShutdownJVM extends Thread{
	/**
	 * 日志线程操作句柄
	 */
	public static Future<?> logFuture;
	
	/**
	 * Spring线程池
	 */
	public static ThreadPoolTaskExecutor taskExecutor;
	
	@Override
	public void run() {
		ContextConfig.isStartETL=false;
		ContextConfig.isStartTransSave=false;
		try{
			TokenExpireHandler.stopTokenScheduler();
			ContextConfig.mqttClient.disconnectForcibly();
			ContextConfig.mqttClient.close(true);
		}catch(MqttException e){
			e.printStackTrace();
		}
		logFuture.cancel(false);
		taskExecutor.destroy();
	}
}
