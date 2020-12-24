package com.github.lixiang2114.etllog.service;

/**
 * @author Lixiang
 * @description 日志ETL服务接口
 */
public interface LogService {
	/**
	 * 启动ETL流程
	 */
	public String startETL();
	
	/**
	 * 停止ETL流程
	 */
	public String stopETL();
	
	/**
	 * ETL流程是否启动
	 * @return
	 */
	public String isETLStart();
	
	/**
	 * dump所有参数配置
	 * @return
	 */
	public String dumpParamConfig();
	
	/**
	 * 刷新转存日志检查点
	 * @return
	 */
	public String refreshCheckPoint();
	
	/**
	 * 转存流程是否启动
	 * @return
	 */
	public String isStartTransSave();
	
	/**
	 * 初始化Emqx配置
	 */
	public String initEmqxConfig();
	
	/**
	 * 初始化Logger配置
	 */
	public String initLoggerConfig();
	
	/**
	 * 平滑装载Emqx配置
	 */
	public String reloadEmqxConfig();
	
	/**
	 * 平滑装载Logger配置
	 */
	public String reloadLoggerConfig();
	
	/**
	 * 启动日志调度器
	 */
	public String startTokenScheduler();
	
	/**
	 * 停止日志调度器
	 */
	public String stopTokenScheduler();
	
	/**
	 * 启动日志转存进程
	 */
	public String startLogTransferSave();
	
	/**
	 * 停止日志转存进程
	 * @return
	 */
	public String stopLogTransferSave();
	
	/**
	 * 手动发送离线日志
	 * @return
	 */
	public String startOfflineLog(String logFileName);
	
	/**
	 * 获取参数值
	 * @param key 键名
	 * @param value 键值
	 * @return
	 */
	public Object getParamValue(String key);
	
	/**
	 * 设置参数值
	 * @param key 键名
	 * @param value 键值
	 * @return
	 */
	public String setParamValue(String key,String value);
}
