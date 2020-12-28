package com.github.lixiang2114.etllog.context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.lixiang2114.etllog.util.ApplicationUtil;
import com.github.lixiang2114.etllog.util.ClassLoaderUtil;
import com.github.lixiang2114.etllog.util.CommonUtil;
import com.github.lixiang2114.etllog.util.PropertiesReader;
import com.github.lixiang2114.etllog.util.TokenUtil;

/**
 * @author Lixiang
 * @description 日志上下文配置
 */
@SuppressWarnings("unchecked")
public class ContextConfig {
	/**
	 * Qos质量指标
	 */
	public static Integer qos;
	
	/**
	 * 主题名称
	 */
	public static String topic;
	
	/**
	 * 是否为Windows系统
	 */
	public static Boolean isWin;
	
	/**
	 * 是否为实时操作模式
	 */
	public static Boolean realTime;
	
	/**
	 * Win32下的Tailf命令字串
	 */
	public static String winTailfCmd;
	
	/**
	 * 当服务启动时初始化配置上下文
	 */
	public static Boolean initOnStart;
	
	/**
	 * 应用端日志文件
	 */
	public static String appLogFile;
	
	/**
	 * 实时读取推送的日志文件
	 */
	public static File loggerFile;
	
	/**
	 * 实时文件已经读取的行数
	 */
	public static int lineNumber;
	
	/**
	 * 实时文件已经读取的字节数量
	 */
	public static long byteNumber;
	
	/**
	 * 实时转存的日志文件
	 */
	public static File transferSaveFile;
	
	/**
	 * 离线推送的日志文件
	 */
	public static File manualLoggerFile;
	
	/**
	 * 离线文件已经读取的行数
	 */
	public static int manualLineNumber;
	
	/**
	 * 离线文件已经读取的字节数量
	 */
	public static long manualByteNumber;
	
	/**
	 * ETL流程是否启动
	 */
	public static Boolean isStartETL;
	
	/**
	 * 是否设置为保留消息
	 */
	public static Boolean retained;
	
	/**
	 * 主机列表
	 */
	public static String[] hostList;
	
	/**
	 * 批处理尺寸
	 */
	public static Integer batchSize;
	
	/**
	 * 过滤器名称
	 */
	public static String filterName;
	
	/**
	 * 过滤方法
	 */
	public static Method doFilter;
	
	/**
	 * 过滤器对象
	 */
	public static Object filterObject;
	
	/**
	 * Mqtt客户端
	 */
	public static MqttClient mqttClient;
	
	/**
	 * 日志过滤器配置
	 */
	public static Properties filterConfig;
	
	/**
	 * 日志文件配置
	 */
	public static Properties loggerConfig;
	
	/**
	 * 工程上下文配置
	 */
	public static Properties contextConfig;
	
	/**
	 * 日志转存流程是否启动
	 */
	public static Boolean isStartTransSave;
	
	/**
	 * 是否需要启动Token过期调度器
	 */
	public static Boolean startTokenScheduler;
	
	/**
	 * 是否使用密码字段携带Token
	 */
	public static boolean tokenFromPass=true;
	
	/**
	 * 转存日志文件最大尺寸
	 */
	public static Long transferSaveMaxSize;
	
	/**
	 * Mqtt客户端持久化模式
	 */
	public static MqttClientPersistence persistence;
	
	/**
	 * Mqtt客户端连接参数
	 */
	public static MqttConnectOptions mqttConnectOptions;
	
	/**
	 * 英文冒号正则式
	 */
	private static final Pattern COLON_REGEX=Pattern.compile(":");
	
	/**
	 * 英文逗号正则式
	 */
	private static final Pattern COMMA_REGEX=Pattern.compile(",");
	
	/**
     * 数字正则式
     */
	public static final Pattern NUMBER_REGEX=Pattern.compile("^[0-9]+$");
	
	/**
	 * 日志工具
	 */
	private static final Logger log=LoggerFactory.getLogger(ContextConfig.class);
	
	/**
     * IP地址正则式
     */
	public static final Pattern IP_REGEX=Pattern.compile("^\\d+\\.\\d+\\.\\d+\\.\\d+$");
	
	/**
	 * 容量正则式
	 */
	private static final Pattern CAP_REGEX = Pattern.compile("([1-9]{1}\\d+)([a-zA-Z]{1,5})");
	
	/**
	 * Source默认过滤器
	 */
	private static final String DEFAULT_FILTER="com.github.lixiang2114.etllog.filter.DefaultLogFilterImpl";
	
	/**
	 * 初始化上下文配置
	 */
	public static final void loadContextConfig() {
		contextConfig=PropertiesReader.getProperties("context.properties");
		realTime=Boolean .valueOf(contextConfig.getProperty("context.realTime", "true").trim());
		initOnStart=Boolean .valueOf(contextConfig.getProperty("context.initOnStart", "true").trim());
	}
	
	/**
	 * 重载日志配置
	 */
	public static final void reloadLoggerConfig() {
		//初始化上下文配置
		loadContextConfig();
		initLoggerConfig();
	}
	
	/**
	 * 重载Emqx配置
	 */
	public static final void reloadEmqxConfig() {
		//初始化上下文配置
		loadContextConfig();
		initEmqxConfig();
	}
	
	/**
	 * 初始化日志配置
	 */
	public static final void initLoggerConfig() {
		//初始化logger配置
		loggerConfig=PropertiesReader.getProperties("logger.properties");
		
		//应用方日志文件
		appLogFile=loggerConfig.getProperty("logger.appLogFile");
		
		//默认实时缓冲日志文件
		File bufferLogFile=new File(ApplicationUtil.getValue("projectFile",File.class),"tmp/transfer.log.0");
		
		//转存应用方的日志文件
		String transferSaveFileName=loggerConfig.getProperty("logger.transferSaveFile");
		if(null==transferSaveFileName || 0==transferSaveFileName.trim().length()) {
			transferSaveFile=bufferLogFile;
			log.warn("not found parameter: 'logger.transferSaveFile',will be use default...");
		}else{
			File file=new File(transferSaveFileName.trim());
			if(file.exists() && file.isFile()) transferSaveFile=file;
		}
		
		log.info("transfer save logger file is: "+transferSaveFile.getAbsolutePath());
		
		//转存日志文件最大尺寸
		transferSaveMaxSize=getTransferSaveMaxSize(loggerConfig);
		log.info("transfer save logger file max size is: "+transferSaveMaxSize);
		
		//实时自动推送的日志文件
		String loggerFileName=loggerConfig.getProperty("logger.loggerFile");
		if(null==loggerFileName || 0==loggerFileName.trim().length()) {
			loggerFile=bufferLogFile;
			log.warn("not found parameter: 'logger.loggerFile',will be use default...");
		}else{
			File file=new File(loggerFileName.trim());
			if(file.exists() && file.isFile()) loggerFile=file;
		}
		
		log.info("realTime logger file is: "+loggerFile.getAbsolutePath());
		
		//实时自动推送的日志文件检查点
		lineNumber=Integer.parseInt(loggerConfig.getProperty("logger.lineNumber","0"));
		log.info("lineNumber is: "+lineNumber);
		byteNumber=Integer.parseInt(loggerConfig.getProperty("logger.byteNumber","0"));
		log.info("byteNumber is: "+byteNumber);
		
		//离线手动推送的日志文件
		String manualFileName=loggerConfig.getProperty("logger.manualLoggerFile");
		if(null==manualFileName || 0==manualFileName.trim().length()) {
			log.warn("not found parameter: 'logger.manualLoggerFile',can not use offline send with manual...");
		}else{
			File file=new File(manualFileName.trim());
			if(file.exists() && file.isFile()) manualLoggerFile=file;
		}
		
		//离线手动推送的日志文件检查点
		manualLineNumber=Integer.parseInt(loggerConfig.getProperty("logger.manualLineNumber","0"));
		log.info("manualLineNumber is: "+manualLineNumber);
		manualByteNumber=Integer.parseInt(loggerConfig.getProperty("logger.manualByteNumber","0"));
		log.info("manualByteNumber is: "+manualByteNumber);
	}
	
	/**
	 * 初始化Emqx配置
	 */
	public static final void initEmqxConfig() {
		//获取上下文参数1:过滤器名称
		filterName=getParamValue("emqx.filterName", "filter");
		
		//获取上下文参数2:批处理尺寸
		batchSize=Integer.parseInt(getParamValue("emqx.batchSize", "100"));
		
		//装载过滤器配置
		filterConfig=PropertiesReader.getProperties("conf/"+filterName+".properties");
		if(null==filterConfig) filterConfig=new Properties();
		
		//获取绑定的过滤器类
		Class<?> filterType=null;
		try {
			String filterClass=(String)filterConfig.remove("type");
			if(null==filterClass || 0==filterClass.trim().length()){
				filterClass=DEFAULT_FILTER;
				log.warn("filterName=="+filterName+" the filter is empty or not found, the default filter will be used...");
			}
			log.info("load filter class file:"+filterClass);
			filterType=Class.forName(filterClass);
		} catch (ClassNotFoundException e) {
			log.error("load filter class failure,cause is:",e);
			throw new RuntimeException(e);
		}
		
		//创建过滤器对象
		try {
			filterObject=filterType.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			log.error("filter object instance failure,cause is:",e);
			throw new RuntimeException(e);
		}
		
		//自动初始化过滤器参数
		try {
			initFilter(filterType);
			System.out.println("INFO: auto initialized filter parameter complete!");
		} catch (ClassNotFoundException | IOException e) {
			log.warn(filterType.getName()+" may not be auto initialized:filterConfig");
		}
		
		//回调初始化过滤器参数
		try {
			Method filterConfig = filterType.getDeclaredMethod("filterConfig",Properties.class);
			if(null!=filterConfig) filterConfig.invoke(filterObject, filterConfig);
			log.info("callback initialized filter parameter complete!");
		} catch (Exception e) {
			log.warn(filterType.getName()+" may not be manual initialized:filterConfig");
		}
		
		//获取上下文参数3:Emqx主机地址
		initHostAddress();
		if(null==hostList) throw new RuntimeException("Error:host address can not be NULL or EMPTY!!!");
		log.info("emqx host address initialized complete:"+hostList);
		
		//获取上下文参数4:Mqtt主机连接参数
		initMqttClientOptions(filterType);
		log.info("emqx host connection initialized complete: URLS: "+Arrays.toString(mqttConnectOptions.getServerURIs())+" tokenFromPass: "
		+tokenFromPass+" useName:"+mqttConnectOptions.getUserName()+" passWord: "+new String(mqttConnectOptions.getPassword()));
		
		//初始化过滤器对象与接口表
		initFilterFace(filterType);
	}
	
	/**
	 * @param context
	 */
	private static final void initHostAddress(){
		//获取连接协议类型
		String protocolType=getParamValue("emqx.protocolType", "tcp")+"://";
		
		//计算默认端口号
		String defaultPort="ssl://".equals(protocolType)?"8883":"1883";
		
		//获取主机列表字串
		String tmpHostStr=contextConfig.getProperty("emqx.hostList","").trim();
		String hostStr=0!=tmpHostStr.length()?tmpHostStr:"127.0.0.1:"+defaultPort;
		
		//初始化主机列表
		ArrayList<String> tmpList=new ArrayList<String>();
		String[] hosts=COMMA_REGEX.split(hostStr);
		for(int i=0;i<hosts.length;i++){
			String host=hosts[i].trim();
			if(0==host.length()) continue;
			String[] ipAndPort=COLON_REGEX.split(host);
			if(ipAndPort.length>=2){
				String ip=ipAndPort[0].trim();
				String port=ipAndPort[1].trim();
				if(!IP_REGEX.matcher(ip).matches()) continue;
				if(!NUMBER_REGEX.matcher(port).matches()) continue;
				tmpList.add(new StringBuilder(protocolType).append(ip).append(":").append(port).toString());
				continue;
			}
			
			if(ipAndPort.length<=0) continue;
			
			String unknow=ipAndPort[0].trim();
			if(NUMBER_REGEX.matcher(unknow).matches()){
				tmpList.add(new StringBuilder(protocolType).append("127.0.0.1:").append(unknow).toString());
			}else if(IP_REGEX.matcher(unknow).matches()){
				tmpList.add(new StringBuilder(protocolType).append(unknow).append(":").append(defaultPort).toString());
			}
		}
		
		int hostCount=tmpList.size();
		if(0!=hostCount) hostList=tmpList.toArray(new String[hostCount]);
	}
	
	/**
	 * @param context
	 */
	private static final void initMqttClientOptions(Class<?> filterType){
		mqttConnectOptions = new MqttConnectOptions();
		
		mqttConnectOptions.setServerURIs(hostList);
		mqttConnectOptions.setMaxInflight(Integer.parseInt(getParamValue("emqx.automaticReconnect", "10")));
		mqttConnectOptions.setCleanSession(Boolean.parseBoolean(getParamValue("emqx.cleanSession", "true")));
		mqttConnectOptions.setKeepAliveInterval(Integer.parseInt(getParamValue("emqx.keepAliveInterval", "60")));
		mqttConnectOptions.setConnectionTimeout(Integer.parseInt(getParamValue("emqx.connectionTimeout", "30")));
		mqttConnectOptions.setAutomaticReconnect(Boolean.parseBoolean(getParamValue("emqx.automaticReconnect", "true")));
		
		String jwtSecret=null;
		try{
			jwtSecret=(String)filterType.getDeclaredMethod("getJwtsecret").invoke(filterObject);
		}catch(Exception e){
			log.warn("jwt secret information not found...");
		}
		
		String userName=null;
		String passWord=null;
		try{
			userName=(String)filterType.getDeclaredMethod("getUsername").invoke(filterObject);
			passWord=(String)filterType.getDeclaredMethod("getPassword").invoke(filterObject);
		}catch(Exception e){
			log.warn("username or password information not found...");
		}
		
		passWord=null==passWord||0==passWord.trim().length()?"public":passWord.trim();
		userName=null==userName||0==userName.trim().length()?"admin":userName.trim();
			
		if(null!=jwtSecret){
			Integer tokenExpire=null;
			try{
				tokenExpire=(Integer)filterType.getDeclaredMethod("getTokenexpire").invoke(filterObject);
			}catch(Exception e){
				log.warn("token expire information not found...");
			}
			
			Integer expireFactor=null;
			try{
				expireFactor=(Integer)filterType.getDeclaredMethod("getExpirefactor").invoke(filterObject);
			}catch(Exception e){
				log.warn("expire factor information not found...");
			}
			
			if(null==tokenExpire) tokenExpire=3600;
			if(null==expireFactor) expireFactor=750;
			
			if(-1==tokenExpire.intValue()) {
				expireFactor=1000;
				tokenExpire=1000000000;
				startTokenScheduler=false;
			}
			
			String token=null;
			try {
				token=TokenUtil.initToken(jwtSecret, tokenExpire, userName, expireFactor);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			
			if(null==token) throw new RuntimeException("token is NULL or EMPTY!!!");
			
			String tokenFromField=null;
			try{
				tokenFromField=(String)filterType.getDeclaredMethod("getTokenfrom").invoke(filterObject);
			}catch(Exception e){
				log.warn("token field name is unknow,default use password...");
			}
			
			if(null==tokenFromField) tokenFromField="password";
			
			if("username".equalsIgnoreCase(tokenFromField)) {
				userName=token;
				tokenFromPass=false;
			}else {
				passWord=token;
				tokenFromPass=true;
			}
			
			if(null==startTokenScheduler) startTokenScheduler=true;
		}
		
		mqttConnectOptions.setUserName(userName);
		mqttConnectOptions.setPassword(passWord.toCharArray());
		
		String persistenceType=getParamValue("emqx.persistenceType", "org.eclipse.paho.client.mqttv3.persist.MemoryPersistence");
		try {
			persistence=(MqttClientPersistence)Class.forName(persistenceType).newInstance();
		} catch (Exception e) {
			persistence=new MemoryPersistence();
			log.warn(persistenceType+" is not be found,default use MemoryPersistence...");
		}
	}
	
	/**
	 * @param filterType
	 */
	private static final void initFilterFace(Class<?> filterType) {
		try{
			String tmpTopic=(String)filterType.getDeclaredMethod("getTopic").invoke(filterObject);
			if(null==tmpTopic) throw new RuntimeException("topic can not be NULL!!!");
			topic=tmpTopic.trim();
			if(0==topic.length()) throw new RuntimeException("topic can not be EMPTY!!!");
		}catch(Exception e){
			log.error("get topic occur error,cause is:",e);
			throw new RuntimeException(e);
		}
		
		try{
			qos=(Integer)filterType.getDeclaredMethod("getQos").invoke(filterObject);
			if(null==qos) qos=1;
		}catch(Exception e){
			qos=1;
			log.warn("getQoses method can not be found,use default Qos=1...");
		}
		
		try{
			retained=(Boolean)filterType.getDeclaredMethod("getRetained").invoke(filterObject);
			if(null==retained) retained=false;
		}catch(Exception e){
			retained=false;
			log.warn("getRetained method can not be found,use default retained=false...");
		}
		
		 try {
			doFilter=filterType.getDeclaredMethod("doFilter",String.class);
		} catch (NoSuchMethodException | SecurityException e) {
			log.warn("doFilter method can not be found,will not be invoke filter method...");
		}
		 
		try {
			String clientId=MqttClient.generateClientId();
			mqttClient=new MqttClient(mqttConnectOptions.getServerURIs()[0],clientId,persistence);
			IMqttToken mqttToken=mqttClient.connectWithResult(mqttConnectOptions);
			mqttToken.waitForCompletion();
		} catch (MqttException e) {
			log.error("connection Mqtt server occur error,cause is:",e);
			 throw new RuntimeException(e);
		}
	}
	
	/**
	 * @param filterType
	 * @param filterProperties
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private static final void initFilter(Class<?> filterType) throws IOException, ClassNotFoundException {
		if(null==filterType || 0==filterConfig.size()) return;
		for(Map.Entry<Object, Object> entry:filterConfig.entrySet()){
			String key=((String)entry.getKey()).trim();
			if(0==key.length()) continue;
			Field field=null;
			try {
				field=filterType.getDeclaredField(key);
				field.setAccessible(true);
			} catch (NoSuchFieldException | SecurityException e) {
				e.printStackTrace();
			}
			
			if(null==field) continue;
			
			Object value=null;
			try{
				value=CommonUtil.transferType(entry.getValue(),field.getType());
			}catch(RuntimeException e){
				e.printStackTrace();
			}
			
			if(null==value) continue;
			
			try {
				if((field.getModifiers() & 0x00000008) == 0){
					field.set(filterObject, value);
				}else{
					field.set(filterType, value);
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * @param context
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	private static final String getParamValue(String key,String defaultValue){
		String value=contextConfig.getProperty(key, defaultValue).trim();
		return value.length()==0?defaultValue:value;
	}
	
	/**
	 * 刷新日志文件检查点
	 * @throws IOException
	 */
	public static final void refreshCheckPoint() throws IOException{
		loggerConfig.setProperty("logger.transferSaveFile", transferSaveFile.getAbsolutePath());
		
		loggerConfig.setProperty("logger.lineNumber",""+lineNumber);
		loggerConfig.setProperty("logger.byteNumber",""+byteNumber);
		loggerConfig.setProperty("logger.loggerFile", loggerFile.getAbsolutePath());
		
		loggerConfig.setProperty("logger.manualLineNumber",""+manualLineNumber);
		loggerConfig.setProperty("logger.manualByteNumber",""+manualByteNumber);
		loggerConfig.setProperty("logger.manualLoggerFile", manualLoggerFile.getAbsolutePath());
		
		OutputStream fos=null;
		try{
			fos=new FileOutputStream(ClassLoaderUtil.getRealFile("logger.properties"));
			log.info("reflesh checkpoint...");
			loggerConfig.store(fos, "reflesh checkpoint");
		}finally{
			if(null!=fos) fos.close();
		}
	}
	
	/**
	 * 获取转存日志文件最大尺寸(默认为2GB)
	 */
	private static final Long getTransferSaveMaxSize(Properties loggerConfig){
		String configMaxVal=loggerConfig.getProperty("logger.transferSaveMaxSize");
		if(null==configMaxVal || 0==configMaxVal.trim().length()) return 2*1024*1024*1024L;
		Matcher matcher=CAP_REGEX.matcher(configMaxVal.trim());
		if(!matcher.find()) return 2*1024*1024*1024L;
		return SizeUnit.getBytes(Long.parseLong(matcher.group(1)), matcher.group(2).substring(0,1));
	}

	public static String collectRealtimeParams() {
		HashMap<String,Object> map=new HashMap<String,Object>();
		map.put("qos", qos);
		map.put("topic", topic);
		map.put("isWin", isWin);
		map.put("retained", retained);
		map.put("realTime", realTime);
		map.put("realTime", realTime);
		map.put("isStartETL", isStartETL);
		map.put("batchSize", batchSize);
		map.put("loggerFile", loggerFile);
		map.put("filterName", filterName);
		map.put("appLogFile", appLogFile);
		map.put("initOnStart", initOnStart);
		map.put("lineNumber", lineNumber);
		map.put("winTailfCmd", winTailfCmd);
		map.put("byteNumber", byteNumber);
		map.put("tokenFromPass", tokenFromPass);
		map.put("clientId", mqttClient.getClientId());
		map.put("hostList", Arrays.toString(hostList));
		map.put("transferSaveFile", transferSaveFile);
		map.put("isStartTransSave", isStartTransSave);
		map.put("manualLoggerFile", manualLoggerFile);
		map.put("jwtSecret", filterConfig.get("jwtSecret"));
		map.put("manualLineNumber", manualLineNumber);
		map.put("manualByteNumber", manualByteNumber);
		map.put("transferSaveMaxSize", transferSaveMaxSize);
		map.put("startTokenScheduler", startTokenScheduler);
		map.put("tokenExpire", filterConfig.get("tokenExpire"));
		map.put("filterType", filterObject.getClass().getName());
		map.put("expireFactor", filterConfig.get("expireFactor"));
		map.put("persistence", persistence.getClass().getName());
		map.put("userName", mqttConnectOptions.getUserName());
		map.put("maxInflight", mqttConnectOptions.getMaxInflight());
		map.put("isCleanSession", mqttConnectOptions.isCleanSession());
		map.put("passWord", new String(mqttConnectOptions.getPassword()));
		map.put("keepAliveInterval", mqttConnectOptions.getKeepAliveInterval());
		map.put("connectionTimeout", mqttConnectOptions.getConnectionTimeout());
		map.put("automaticReconnect", mqttConnectOptions.isAutomaticReconnect());
		return map.toString();
	}
}
