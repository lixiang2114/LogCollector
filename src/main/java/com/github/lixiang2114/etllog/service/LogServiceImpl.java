package com.github.lixiang2114.etllog.service;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.github.lixiang2114.etllog.context.ContextConfig;
import com.github.lixiang2114.etllog.handler.AutoETLHandler;
import com.github.lixiang2114.etllog.handler.ManualETLHandler;
import com.github.lixiang2114.etllog.handler.TokenExpireHandler;
import com.github.lixiang2114.etllog.handler.TransferSaveHandler;
import com.github.lixiang2114.etllog.thread.GracefulShutdownJVM;
import com.github.lixiang2114.etllog.thread.SpringThreadPool;
import com.github.lixiang2114.etllog.util.CommonUtil;

/**
 * @author Lixiang
 * @description 日志ETL服务实现
 */
@Service("logService")
@SuppressWarnings("unchecked")
public class LogServiceImpl implements LogService{
	/**
	 * 日志工具
	 */
	private static final Logger log=LoggerFactory.getLogger(LogServiceImpl.class);
	
	@Override
	public String startETL() {
		if(null==ContextConfig.transferSaveFile) {
			log.error("start ETL process failture,logger file not be specified!");
			return "start ETL process failture,logger file not be specified!";
		}
		
		ContextConfig.isStartETL=true;
		GracefulShutdownJVM.taskExecutor=SpringThreadPool.getSpringThreadPool();
		GracefulShutdownJVM.logFuture=GracefulShutdownJVM.taskExecutor.submit(new AutoETLHandler());
		log.info("ETL process has already started...");
		
		return "start ETL process complete!";
	}

	@Override
	public String stopETL() {
		ContextConfig.isStartETL=false;
		log.info("stop ETL process...");
		return "stop ETL process complete!";
	}

	public String isETLStart() {
		return ContextConfig.isStartETL?"ETL process is running!":"ETL process is stopped!";
	}
	
	@Override
	public String isStartTransSave() {
		return ContextConfig.isStartTransSave?"TransSave process is running!":"TransSave process is stopped!";
	}

	@Override
	public String reloadEmqxConfig() {
		ContextConfig.reloadEmqxConfig();
		log.info("reload emqx config complete!");
		return "reload emqx config complete!";
	}

	@Override
	public String reloadLoggerConfig() {
		ContextConfig.reloadLoggerConfig();
		log.info("reload logger config complete!");
		return "reload logger config complete!";
	}

	@Override
	public String startTokenScheduler() {
		if(null==ContextConfig.startTokenScheduler || !ContextConfig.startTokenScheduler){
			log.warn("token expire is -1,no need to start the scheduler!");
			return "token expire is -1,no need to start the scheduler!";
		}
		
		TokenExpireHandler.startTokenScheduler(ContextConfig.mqttConnectOptions, ContextConfig.tokenFromPass);
		log.info("start scheduler complete!");
		return "start scheduler complete!";
	}

	@Override
	public String stopTokenScheduler() {
		try{
			TokenExpireHandler.stopTokenScheduler();
			ContextConfig.startTokenScheduler=false;
			ContextConfig.mqttClient.disconnectForcibly();
			ContextConfig.mqttClient.close(true);
			log.info("stop scheduler complete!");
			return "stop scheduler complete!";
		}catch(MqttException e){
			log.error("stop scheduler failure,cause:",e);
			return "stop scheduler failure,cause:"+e.getMessage();
		}
	}

	@Override
	public String startLogTransferSave() {
		String command=null;
		if(ContextConfig.isWin){
			command=ContextConfig.winTailfCmd+" "+ContextConfig.appLogFile;
		}else{
			command="tail -F "+ContextConfig.appLogFile;
		}
		
		log.info("tailf cmmand is: "+command);
		
		ContextConfig.isStartTransSave=true;
		GracefulShutdownJVM.taskExecutor=SpringThreadPool.getSpringThreadPool();
		GracefulShutdownJVM.taskExecutor.submit(new TransferSaveHandler(command));
		log.info("start logger transfer save process complete!");
		
		return "start logger transfer save process complete!";
	}

	@Override
	public String stopLogTransferSave() {
		ContextConfig.isStartTransSave=false;
		log.info("stop logger transfer save process complete!");
		return "stop logger transfer save process complete!";
	}

	@Override
	public String startOfflineLog(String logFileName) {
		if(null==logFileName || 0==logFileName.trim().length()){
			ManualETLHandler.sendToEmqx(null);
		}else{
			ManualETLHandler.sendToEmqx(new File(logFileName.trim()));
		}
		
		log.info("send offline log complete!");
		return "send offline log complete!";
	}

	@Override
	public String initEmqxConfig() {
		ContextConfig.initEmqxConfig();
		log.info("init emqx config complete!");
		return "init emqx config complete!";
	}

	@Override
	public String initLoggerConfig() {
		ContextConfig.initLoggerConfig();
		log.info("init logger config complete!");
		return "init logger config complete!";
	}

	@Override
	public String refreshCheckPoint() {
		try {
			ContextConfig.refreshCheckPoint();
		} catch (IOException e) {
			log.error("refresh transfer logfile checkPoint failure,cause is:",e);
			return "Error: refresh transfer logfile checkPoint failure!";
		}
		log.info("refresh transfer logfile checkPoint success!");
		return "refresh transfer logfile checkPoint success!";
	}
	
	@Override
	public Object getParamValue(String key) {
		try {
			Field field = ContextConfig.class.getDeclaredField(key);
			field.setAccessible(true);
			return field.get(ContextConfig.class);
		} catch (NoSuchFieldException e) {
			log.error("not find field for "+key+" in ContextConfig,cause is: ",e);
			return "Error: not find field for "+key+" in ContextConfig";
		} catch (Exception e) {
			log.error("get parameter value occur error,cause is: ",e);
			return "Error: "+e.getMessage();
		}
	}

	@Override
	public String setParamValue(String key, String value) {
		try {
			Field field = ContextConfig.class.getDeclaredField(key);
			field.setAccessible(true);
			Class<?> fieldType=field.getType();
			if(fieldType==File.class){
				field.set(ContextConfig.class, new File(value));
			}else{
				field.set(ContextConfig.class, CommonUtil.transferType(value, field.getType()));
			}
			return "set param: "+key+"="+value+" success!";
		} catch (NoSuchFieldException e) {
			log.error("not find field for "+key+" in ContextConfig ",e);
			return "Error: not find field for "+key+" in ContextConfig";
		} catch (Exception e) {
			log.error("set parameter value occur error,cause is: ",e);
			return "Error: "+e.getMessage();
		}
	}

	@Override
	public String dumpParamConfig() {
		log.info("dump all config parameter...");
		return ContextConfig.collectRealtimeParams();
	}
}
