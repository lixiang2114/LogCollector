package com.github.lixiang2114.etllog.filter;

import java.util.Properties;

/**
 * @author Lixiang
 * @description 默认日志过滤器实现
 */
public class DefaultLogFilterImpl implements LogFilter{
	/**
	 * 通信质量指标
	 */
	private static Integer qos;
	
	/**
	 * 连接主题名称
	 */
	private static String topic;
	
	/**
	 * 是否设置为保留消息
	 */
	private static Boolean retained;
	
	/**
	 * 登录Emqx密码
	 */
	private static String passWord;
	
	/**
	 * 登录Emqx用户名
	 */
	private static String userName;
	
	/**
	 * 登录验证Token的秘钥
	 */
	private static String jwtSecret;
	
	/**
	 * 携带Token的字段名
	 */
	private static String tokenFrom;
	
	/**
	 * Token过期时间
	 */
	private static Integer tokenExpire;
	
	/**
	 * Token过期时间因数
	 */
	private static Integer expireFactor;
	
	@Override
	public Integer getQos() {
		return qos;
	}
	
	@Override
	public String getTopic() {
		return topic;
	}
	
	@Override
	public Boolean getRetained() {
		return retained;
	}

	@Override
	public String getPassword() {
		return passWord;
	}

	@Override
	public String getUsername() {
		return userName;
	}
	
	@Override
	public String getJwtsecret() {
		return jwtSecret;
	}
	
	@Override
	public String getTokenfrom() {
		return tokenFrom;
	}

	@Override
	public Integer getTokenexpire() {
		return tokenExpire;
	}
	
	@Override
	public Integer getExpirefactor() {
		return expireFactor;
	}

	@Override
	public String[] doFilter(String record) {
		return new String[]{record};
	}
	
	@Override
	public void filterConfig(Properties config) {
		String qosStr=config.getProperty("qos");
		String topicStr=config.getProperty("topic");
		String retainedStr=config.getProperty("retained");
		String jwtSecretStr=config.getProperty("jwtSecret");
		String passWordStr=config.getProperty("passWord");
		String userNameStr=config.getProperty("userName");
		String tokenFromStr=config.getProperty("tokenFrom");
		String tokenExpireStr=config.getProperty("tokenExpire");
		String expireFactorStr=config.getProperty("expireFactor");
		
		if(null!=qosStr) {
			String qoss=qosStr.trim();
			if(0!=qoss.length()) qos=new Integer(qoss);
		}
		
		if(null!=topicStr) {
			String topicss=topicStr.trim();
			if(0!=topicss.length()) topic=topicss;
		}
		
		if(null!=retainedStr) {
			String retainedss=retainedStr.trim();
			if(0!=retainedss.length()) retained=new Boolean(retainedss);
		}
		
		if(null!=jwtSecretStr) {
			String secret=jwtSecretStr.trim();
			if(0!=secret.length()) jwtSecret=secret;
		}
		
		if(null!=passWordStr) {
			String pass=passWordStr.trim();
			if(0!=pass.length()) passWord=pass;
		}
		
		if(null!=userNameStr) {
			String user=userNameStr.trim();
			if(0!=user.length()) userName=user;
		}
		
		if(null!=tokenFromStr) {
			String from=tokenFromStr.trim();
			if(0!=from.length()) tokenFrom=from;
		}
		
		if(null!=tokenExpireStr) {
			String expire=tokenExpireStr.trim();
			if(0!=expire.length()) tokenExpire=Integer.parseInt(expire);
		}
		
		if(null!=expireFactorStr) {
			String factor=expireFactorStr.trim();
			if(0!=factor.length()) expireFactor=Integer.parseInt(factor);
		}
	}
}
