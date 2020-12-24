package com.github.lixiang2114.etllog.filter;

import java.util.Map;
import java.util.Properties;

/**
 * @author Lixiang
 * @description 日志过滤器
 */
public interface LogFilter {
	/**
	 * 获取主题名称(必须重写)
	 * @return 主题名称
	 */
	public String getTopic();
	
	/**
	 * 日志数据如何通过过滤转换成记录(必须重写)
	 * @param record
	 * @return 文档记录
	 */
	public String[] doFilter(String record);
	
	/**
	 * 日志数据的通信质量
	 * @return 通信指标
	 */
	default public Integer getQos(){return null;}
	
	/**
	 * 登录Emqx服务的密码(如果需要则重写)
	 * @return 登录密码
	 */
	default public String getPassword(){return null;}
	
	/**
	 * 登录Emqx服务的用户名(如果需要则重写)
	 * @return 登录用户
	 */
	default public String getUsername(){return null;}
	
	/**
	 * 生成登录Token的秘钥(如果需要则重写)
	 * @return Token秘钥
	 */
	default public String getJwtsecret(){return null;}
	
	/**
	 * 发送消息是否为保留消息
	 * @return 是否为保留消息
	 */
	default public Boolean getRetained(){return null;}
	
	/**
	 * Token的有效期系数/因子(如果需要则重写)
	 * @return 有效期系数
	 */
	default public Integer getExpirefactor(){return 750;}
	
	/**
	 * Token的过期时间(如果需要则重写)
	 * @return 过期时间
	 */
	default public Integer getTokenexpire(){return 3600;}
	
	/**
	 * 使用过滤器配置初始化本过滤器实例成员变量(如果需要则重写)
	 * @param properties 配置
	 */
	default public void filterConfig(Properties properties){}
	
	/**
	 * Token通过哪个字段携带到Emqx服务端
	 * @return 携带字段
	 */
	default public String getTokenfrom(){return "password";}
	
	/**
	 * 使用Flume插件配置初始化本过滤器实例成员变量(如果需要则重写)
	 * @param config 配置
	 */
	default public void pluginConfig(Map<String,Object> config){}
}
