package com.github.lixiang2114.etllog.controller;

import java.io.File;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.lixiang2114.etllog.context.ContextConfig;
import com.github.lixiang2114.etllog.service.LogService;
import com.github.lixiang2114.etllog.thread.GracefulShutdownJVM;
import com.github.lixiang2114.etllog.util.ApplicationUtil;
import com.github.lixiang2114.etllog.util.ClassLoaderUtil;
import com.github.lixiang2114.etllog.util.CommonUtil;

/**
 * @author Lixiang
 * @description 管理控制台
 */
@RestController
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private LogService logService;
	
	@PostConstruct
	public void init(){
		File projectFile=ApplicationUtil.getProjectPath();
		ApplicationUtil.setValue("projectFile", projectFile);
		ClassLoaderUtil.addFileToCurrentClassPath(new File(projectFile,"extra"));
		ClassLoaderUtil.addFileToCurrentClassPath(new File(projectFile,"conf"));
		ClassLoaderUtil.addFileToCurrentClassPath(new File(projectFile,"filter"));
		ClassLoaderUtil.addCycleFileToCurrentClassPath(new File(projectFile,"filter/lib"));
		Runtime.getRuntime().addShutdownHook(new GracefulShutdownJVM());
		ContextConfig.isWin=CommonUtil.getOSType().toLowerCase().startsWith("win");
		
		System.out.println("INFO: load context config....");
		ContextConfig.loadContextConfig();
		if(!ContextConfig.initOnStart) {
			System.out.println("INFO: manual init other config,because initOnStart:"+ContextConfig.initOnStart);
			return;
		}
		
		System.out.println("INFO: initing logger config....");
		ContextConfig.initLoggerConfig();
		
		System.out.println("INFO: initing emqx config....");
		ContextConfig.initEmqxConfig();
		
		System.out.println("INFO: start token Scheduler process....");
		logService.startTokenScheduler();
		
		if(ContextConfig.realTime) {
			if(ContextConfig.isWin){
				File tailfCmd=new File(projectFile,"bin/tailf.exe");
				if(tailfCmd.exists()) {
					ContextConfig.winTailfCmd=tailfCmd.getAbsolutePath();
				}else{
					File systemTailfCmd=new File(System.getenv("SystemDrive")+"/windows/system32/tailf.exe");
					if(systemTailfCmd.exists()){
						ContextConfig.winTailfCmd=systemTailfCmd.getAbsolutePath();
					}else{
						throw new RuntimeException("current system is windows,tailf.exe is not found...");
					}
				}
				System.out.println("INFO: current system is windows,tailf path is: "+ContextConfig.winTailfCmd);
			}
			
			System.out.println("INFO: start realtime ETL process....");
			logService.startETL();
			
			System.out.println("INFO: start transfer Save process....");
			logService.startLogTransferSave();
			return;
		}
		
		System.out.println("INFO: start offline ETL process!");
		logService.startOfflineLog(null);
		logService.refreshCheckPoint();
		Runtime.getRuntime().exit(0);
	}
	
	@RequestMapping(path="/shutdown")
    public void stopServer() {
		logService.stopLogTransferSave();
		logService.stopETL();
		logService.refreshCheckPoint();
		logService.stopTokenScheduler();
		 Runtime.getRuntime().exit(0);
	}

	@RequestMapping(path="/startETL")
    public String start(Long interval) {
		 return logService.startETL();
	}
	
	@RequestMapping(path="/stopETL")
    public String stop() {
        return logService.stopETL();
    }
	
	@RequestMapping(path="/restartETL")
    public String restart() {
		logService.stopETL();
		return logService.startETL();
    }
	
	@RequestMapping(path="/isETLStart")
    public String isETLStart() {
		return logService.isETLStart();
	}
	
	@RequestMapping(path="/isStartTransSave")
    public String isStartTransSave() {
		return logService.isStartTransSave();
	}
	
	@RequestMapping(path="/initEmqx")
    public String initEmqx() {
		return logService.initEmqxConfig();
	}
	
	@RequestMapping(path="/initLogger")
    public String initLogger() {
		return logService.initLoggerConfig();
	}
	
	@RequestMapping(path="/reloadEmqx")
    public String reloadEmqx() {
		return logService.reloadEmqxConfig();
	}
	
	@RequestMapping(path="/reloadLogger")
    public String reloadLogger() {
		return logService.reloadLoggerConfig();
	}
	
	@RequestMapping(path="/startTokenScheduler")
    public String startTokenScheduler() {
		return logService.startTokenScheduler();
	}
	
	@RequestMapping(path="/stopTokenScheduler")
    public String stopTokenScheduler() {
		return logService.stopTokenScheduler();
	}
	
	@RequestMapping(path="/startLogTransferSave")
    public String startLogTransferSave() {
		return logService.startLogTransferSave();
	}
	
	@RequestMapping(path="/stopLogTransferSave")
    public String stopLogTransferSave() {
		return logService.stopLogTransferSave();
	}
	
	@RequestMapping(path="/restartLogTransferSave")
    public String restartLogTransferSave() {
		logService.stopLogTransferSave();
		return logService.startLogTransferSave();
	}
	
	@RequestMapping(path="/checkpoint")
    public String checkpoint(String fileName) {
		return logService.refreshCheckPoint();
	}
	
	@RequestMapping(path="/setcp")
    public String setCheckpoint(Long bn) {
		if(null==bn) {
			ContextConfig.byteNumber=0L;
		}else{
			ContextConfig.byteNumber=bn;
		}
		return logService.refreshCheckPoint();
	}
	
	@RequestMapping(path="/setmcp")
    public String setManualCheckpoint(Long mbn) {
		if(null==mbn) {
			ContextConfig.manualByteNumber=0L;
		}else{
			ContextConfig.manualByteNumber=mbn;
		}
		return logService.refreshCheckPoint();
	}
	
	@RequestMapping(path="/setcps")
    public String setCheckpoints(Long bn,Long mbn) {
		if(null==bn) {
			ContextConfig.byteNumber=0L;
		}else{
			ContextConfig.byteNumber=bn;
		}
		
		if(null==mbn) {
			ContextConfig.manualByteNumber=0L;
		}else{
			ContextConfig.manualByteNumber=mbn;
		}
		
		return logService.refreshCheckPoint();
	}
	
	@RequestMapping(path="/startOfflineLog")
    public String startOfflineLog(String fileName) {
		return logService.startOfflineLog(fileName);
	}
	
	@RequestMapping(path="/getparam")
    public Object getParamValue(String key) {
		return logService.getParamValue(key);
	}
	
	@RequestMapping(path="/setparam")
    public String setParamValue(String key,String value) {
		return logService.setParamValue(key, value);
	}
	
	@RequestMapping(path="/dumpconfig")
    public String dumpParamConfig() {
		return logService.dumpParamConfig();
	}
}
