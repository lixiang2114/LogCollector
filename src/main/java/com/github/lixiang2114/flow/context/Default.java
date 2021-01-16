package com.github.lixiang2114.flow.context;

/**
 * @author Lixiang
 * @description 提供默认常量值
 */
public class Default {
	/**
	 *  ETL流程模式(true:实时,false:离线)
	 */
	public static final String ETL_MODE="true";
	
	/**
	 *  是否清除流程实例中缓存的插件实例配置信息
	 */
	public static final String CLEAR_CACHE="false";
	
	/**
	 * 上下文配置装载模式(true:启动时装载,false:运行时装载)
	 */
	public static final String LOAD_MODE="true";
	
	/**
	 * 默认Filter过滤器
	 */
	public static final String Filter="defaultFilter";
	
	/**
	 * 默认Sink插件
	 */
	public static final String SINK="emqxSink";
	
	/**
	 * 默认Source插件
	 */
	public static final String SOURCE="fileSource";
	
	/**
	 * 默认的ETL流程列表(多个流程名称间使用英文逗号分隔)
	 */
	public static final String FLOWS="fileToEmqx";

	/**
	 * 默认的过滤器列表(多个过滤器名称间使用英文逗号分隔)
	 */
	public static final String FILTERS="defaultFilter";
	
	/**
	 *  启动流程调度器(true:启动,false:停止)
	 */
	public static final String FLOW_SCHEDULER="true";
	
	/**
	 *  是否需要检查插件实现接口
	 */
	public static final String CHECK_PLUGIN_FACE="false";
	
	/**
	 * 流程通道最大尺寸
	 */
	public static final String CHANNEL_MAX_SIZE="20000";
	
	/**
	 *  ETL流程调度间隔时间(单位:毫秒)
	 */
	public static final String ETL_SCHEDULER_INTERVAL="2000";
	
	/**
	 *  转存流程调度间隔时间(单位:毫秒)
	 */
	public static final String TRA_SCHEDULER_INTERVAL="1000";
	
	/**
	 * 默认的插件列表(多个插件名称间使用英文逗号分隔)
	 */
	public static final String PLUGINS="fileSource,defaultFilter,emqxSink";
}
