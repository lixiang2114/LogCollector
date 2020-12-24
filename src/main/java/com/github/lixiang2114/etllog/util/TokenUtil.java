package com.github.lixiang2114.etllog.util;

import java.util.Date;
import java.util.HashMap;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;

/**
 * @author Louis(Lixiang)
 */
public class TokenUtil {
	/**
	 * 当前Token值
	 */
	private static String token;
	
	/**
	 * Token创建时间
	 */
	private static long createTime;
	
	/**
	 * 验证Token的秘钥文本
	 */
	private static String jwtSecret;
	
	/**
	 * 用户名
	 */
	private static String userName;
	
	/**
	 * Token过期时长(单位:秒,>=1200s)
	 */
	private static int tokenExpire;
	
	/**
	 * Token过期时间因子(默认值0.75)
	 */
	private static Integer expireFactor=750;
	
	/**
	 * @param jwtSecret
	 * @param tokenExpire
	 * @param userName
	 * @return Token
	 * @throws Exception
	 */
	public static String initToken(String jwtSecret,int tokenExpire) throws Exception{
		return initToken(jwtSecret,tokenExpire,null,null);
	}
	
	/**
	 * @param jwtSecret
	 * @param tokenExpire
	 * @param userName
	 * @return Token
	 * @throws Exception
	 */
	public static String initToken(String jwtSecret,int tokenExpire,Integer factor) throws Exception{
		return initToken(jwtSecret,tokenExpire,null,factor);
	}
	
	/**
	 * @param userName
	 * @return Token
	 * @throws Exception
	 */
	public static final String getToken() throws Exception {
		if(null==token) throw new RuntimeException("ERROR:===token has not been initialized...");
		if(System.currentTimeMillis()-createTime>(tokenExpire*expireFactor)) return createToken();
		return token;
	}
	
	/**
	 * @param jwtSecret
	 * @param tokenExpire
	 * @param userName
	 * @return Token
	 * @throws Exception
	 */
	public static String initToken(String jwtSecret,int tokenExpire,String userName,Integer factor) throws Exception{
		if(tokenExpire<1200) throw new RuntimeException("ERROR:===token expire can not smaller than 20 minute...");
		if(null!=factor) TokenUtil.expireFactor=factor;
		TokenUtil.tokenExpire=tokenExpire;
		TokenUtil.userName=userName;
		TokenUtil.jwtSecret=jwtSecret;
		return createToken();
	}
	
	/**
	 * @param userName
	 * @return Token
	 * @throws Exception
	 */
	private static final String createToken() throws Exception {
		HashMap<String,Object> headerClaims=new HashMap<String,Object>();
		headerClaims.put("typ", "JWT");
		headerClaims.put("alg", "HS256");
		JWTCreator.Builder builder=JWT.create();
		if(null!=userName) builder.withSubject(userName);
		return token=builder.withExpiresAt(new Date((createTime=System.currentTimeMillis())+tokenExpire*1000L))
				.withHeader(headerClaims).withIssuedAt(new Date()).sign(Algorithm.HMAC256(jwtSecret));
    }
}
