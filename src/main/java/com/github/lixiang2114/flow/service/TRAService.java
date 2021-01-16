package com.github.lixiang2114.flow.service;

/**
 * @author Lixiang
 * @description 流程服务接口
 */
public interface TRAService {
	/**
	 * 停止所有ETL流程
	 * @return 对象
	 */
	public String stopAllTRAProcess() throws Exception;
	
	/**
	 * 启动所有ETL流程
	 * @return 对象
	 */
	public String startAllTRAProcess() throws Exception;
	
	/**
	 * 刷新所有ETL流程检查点
	 * @return 对象
	 */
	public String refleshAllTRACheckpoint() throws Exception;
	
	/**
	 * 停止ETL流程
	 * @param flowName 流程名称
	 * @return 对象
	 */
	public String stopTRAProcess(String flowName) throws Exception;
	
	/**
	 * 启动ETL流程
	 * @param flowName 流程名称
	 * @return 对象
	 */
	public String startTRAProcess(String flowName) throws Exception;
	
	/**
	 * 刷新指定ETL流程检查点
	 * @param flowName 流程名称
	 * @return 对象
	 */
	public Object refleshTRACheckpoint(String flowName) throws Exception;
}
