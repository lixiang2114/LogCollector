package com.github.lixiang2114.etllog.handler;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.lixiang2114.etllog.context.ContextConfig;

/**
 * @author Lixiang
 * @description ETL操作句柄
 * @param <T>
 */
public class ManualETLHandler {
	/**
	 * 日志工具
	 */
	private static final Logger log=LoggerFactory.getLogger(ManualETLHandler.class);
	
	/**
	 * 离线发送消息打Emqx
	 * @param manualLogFile 日志文件
	 */
	public static final void sendToEmqx(File manualLogFile) {
		if(null==manualLogFile) manualLogFile=ContextConfig.manualLoggerFile;
		RandomAccessFile raf=null;
		try{
			raf=new RandomAccessFile(manualLogFile, "r");
			if(0!=ContextConfig.manualByteNumber)raf.seek(ContextConfig.manualByteNumber);
			out:while(true){
				ContextConfig.manualLineNumber=ContextConfig.manualLineNumber++;
				ContextConfig.manualByteNumber=raf.getFilePointer();
				String line=raf.readLine();
				if(null==line) break;
				
				String record=line.trim();
				if(0==record.length()) continue;
				
				String[] rows=null;
				if(null==ContextConfig.doFilter) {
					rows=new String[]{record};
				}else{
					rows=(String[])ContextConfig.doFilter.invoke(ContextConfig.filterObject, record);
				}
				
				if(null==rows || 0==rows.length) continue;
				
				String msg=null;
				MqttMessage message = null;
				for(String row:rows){
					if(null==row) continue;
					if(0==(msg=row.trim()).length()) continue;
					message = new MqttMessage(msg.getBytes(Charset.forName("UTF-8")));
					message.setQos(ContextConfig.qos);
					message.setRetained(ContextConfig.retained);
					
					int times=0;
					boolean loop=false;
					do{
						try{
							ContextConfig.mqttClient.publish(ContextConfig.topic, message);
							loop=false;
						}catch(Exception e){
							times++;
							loop=true;
							Thread.sleep(2000L);
							log.error("publish occur excepton: "+e.getMessage());
						}
					}while(loop && times<3);
					if(loop) break out;
				}
			}
			ContextConfig.refreshCheckPoint();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(null!=raf) raf.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
}
