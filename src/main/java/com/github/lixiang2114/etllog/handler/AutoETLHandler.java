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
public class AutoETLHandler implements Runnable{
	/**
	 * 日志工具
	 */
	private static final Logger log=LoggerFactory.getLogger(AutoETLHandler.class);
	
	/**
	 * 切换下一个日志文件
	 * @return 是否是最后一个日志文件
	 * @throws IOException 
	 */
	private static final void nextFile(RandomAccessFile randomFile) throws IOException{
		ContextConfig.byteNumber=0;
		ContextConfig.lineNumber=0;
		if(null!=randomFile) randomFile.close();
		String curFilePath=ContextConfig.loggerFile.getAbsolutePath();
		
		ContextConfig.loggerFile.delete();
		int lastIndex=curFilePath.lastIndexOf(".");
		int newIndex=Integer.parseInt(curFilePath.substring(lastIndex+1))+1;
		ContextConfig.loggerFile=new File(curFilePath.substring(0,lastIndex+1)+newIndex);
		log.info("AutoETLHandler switch transferSave logFile to "+ContextConfig.loggerFile.getAbsolutePath());
	}
	
	/**
	 * 是否读到最后一个日志文件
	 * @return 是否是最后一个日志文件
	 */
	private static final boolean isLastFile(){
		String curFilePath=ContextConfig.loggerFile.getAbsolutePath();
		int curFileIndex=Integer.parseInt(curFilePath.substring(curFilePath.lastIndexOf(".")+1));
		for(String fileName:ContextConfig.loggerFile.getParentFile().list()) if(curFileIndex<Integer.parseInt(fileName.substring(fileName.lastIndexOf(".")+1))) return false;
		return true;
	}
	
	@Override
	public void run() {
		RandomAccessFile raf=null;
		try{
			out:while(ContextConfig.isStartETL){
				raf=new RandomAccessFile(ContextConfig.loggerFile, "r");
				if(0!=ContextConfig.byteNumber)raf.seek(ContextConfig.byteNumber);
				while(ContextConfig.isStartETL){
					ContextConfig.lineNumber=ContextConfig.lineNumber++;
					ContextConfig.byteNumber=raf.getFilePointer();
					String line=raf.readLine();
					if(null==line) {
						if(!isLastFile()){
							nextFile(raf);
							break;
						}
						Thread.sleep(3000L);
						continue;
					}
					
					String record=line.trim();
					if(0==record.length()) continue;
					
					String[] rows=null;
					if(null==ContextConfig.doFilter){
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
						
						if(loop){
							ContextConfig.isStartETL=false;
							break out;
						}
					}
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
