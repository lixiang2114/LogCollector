package com.github.lixiang2114.etllog.handler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import com.github.lixiang2114.etllog.util.TokenUtil;

/**
 * @author Louis(Lixiang)
 */
public class TokenExpireHandler {
	/**
	 * Token过期事件调度池句柄
	 */
	private static ScheduledFuture<?> future;
	
	/**
	 * Token过期事件调度池
	 */
	private static ScheduledExecutorService tokenExpireService;
	
	public static void startTokenScheduler(MqttConnectOptions mqttConnectOptions,boolean tokenFromPass){
		tokenExpireService = Executors.newSingleThreadScheduledExecutor();
		future = tokenExpireService.scheduleWithFixedDelay(new Runnable() {
			public void run() {
            	String token=null;
				try {
					token = TokenUtil.getToken();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				
            	if(!tokenFromPass){
            		mqttConnectOptions.setUserName(token);
            		return;
            	}
            	
            	mqttConnectOptions.setPassword(token.toCharArray());
            }
        },10, 10, TimeUnit.MINUTES);
	}
	
	/**
	 * 停止处理器
	 */
	public static void stopTokenScheduler(){
		 if (future != null) future.cancel(true);
		 if (null == tokenExpireService) return;
		 tokenExpireService.shutdownNow();
	}
}
