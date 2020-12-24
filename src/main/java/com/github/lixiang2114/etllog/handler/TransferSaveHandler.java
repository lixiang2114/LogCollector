package com.github.lixiang2114.etllog.handler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;

import com.github.lixiang2114.etllog.context.ContextConfig;

/**
 * @author Lixiang
 * @description 日志转存任务
 */
public class TransferSaveHandler implements Runnable{
	/**
	 * 命令字符串
	 */
	private String command;
	
	public TransferSaveHandler(String command){
		this.command=command;
	}

	@Override
	public void run() {
		BufferedWriter bw=null;
		Process subProcess=null;
		LineNumberReader lnr=null;
		Runtime runtime=Runtime.getRuntime();
		
		try {
			String line=null;
			subProcess=runtime.exec(command);
			lnr=new LineNumberReader(new InputStreamReader(subProcess.getInputStream()));
			
			while(ContextConfig.isStartTransSave){
				bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(ContextConfig.transferSaveFile,true)));
				while(ContextConfig.isStartTransSave){
					if(null==(line=lnr.readLine())){
						Thread.sleep(1000L);
						continue;
					}
					
					//写入转存日志文件
					bw.write(line);
					bw.newLine();
					bw.flush();
					
					//当前转存日志文件未达到最大值则继续写转存日志文件
					if(ContextConfig.transferSaveFile.length()<ContextConfig.transferSaveMaxSize) continue;
					
					//当前转存日志文件达到最大值则增加转存日志文件
					String curTransSaveFilePath=ContextConfig.transferSaveFile.getAbsolutePath();
					int lastIndex=curTransSaveFilePath.lastIndexOf(".");
					ContextConfig.transferSaveFile=new File(curTransSaveFilePath.substring(0,lastIndex+1)+(Integer.parseInt(curTransSaveFilePath.substring(lastIndex+1))+1));
					
					bw.close();
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				if(null!=bw) bw.close();
				if(null!=lnr) lnr.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
}
