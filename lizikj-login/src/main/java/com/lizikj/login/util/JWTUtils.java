package com.lizikj.login.util;

import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.lizikj.common.util.DateUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * jwt工具类
 * @author lijundong 
 * @date 2017年7月13日 下午5:22:16
 */
public class JWTUtils {
	
	public static final int TIME = 7 * 24 * 3600;
	
	public static final String USER_ID = "ui";
	
	public static final String USER_NAME = "un";
	
	public static final String USER_TYPE = "ut";
	
	public static final String LOGIN_SOURCE = "ls";
	
	public static final String STAFF_ID = "si";
	
	public static final String MERCHANT_ID = "mi";
	
	public static final String SHOP_ID = "spi";

	public static final String AGENT_ID = "agi";
	
	public static final String MEMBER_ID = "mbi";
	
	public static final String MERCHANT_MEMBER_ID = "mmbi";
	
	public static final String USER_SOURCE = "us";
	
	public static final String ENV = "env";
	
	/**
	 * 创建jwt
	 * @param claims
	 * @param loginTime 登录时间
	 * @param time 有效时间，单位为毫秒
	 * @return String
	 * @author lijundong
	 * @date 2017年7月12日 下午6:59:17
	 */
	public static String createJWT(Map<String, Object> claims, Date loginTime){
		SecretKey key = generalKey();
		//isssuedAt=jwt的签发时间
		JwtBuilder builder = Jwts.builder().signWith(SignatureAlgorithm.HS256, key);
		//设置过期时间
		long expMillis = loginTime.getTime() + JWTUtils.TIME * 1000;
		Date exp = new Date(expMillis);
		builder.setClaims(claims);
		builder.setIssuedAt(loginTime);
		builder.setExpiration(exp);
		
		return builder.compact();
	}

	/**
	 * 解密 jwt
	 * @param jwt
	 * @return
	 * @throws Exception
	 */
	public static Claims parseJWT(String jwt){
		SecretKey key = generalKey();
		Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(jwt).getBody();
		return claims;
	}
	
	/**
	 * 由字符串生成加密key
	 * @return
	 */
	private static SecretKey generalKey(){
		String stringKey = "87454654564";
		byte[] encodedKey = Base64.decodeBase64(stringKey);
	    SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
	    return key;
	}
	
	public static void main(String[] args) {
		Claims claims = parseJWT("eyJhbGciOiJIUzI1NiJ9.eyJ1aSI6MiwibW1iaSI6MCwibWJpIjowLCJscyI6MywibWkiOjEsImV4cCI6MTUwODQ5MjQwMCwic3BpIjoxLCJpYXQiOjE1MDc4ODc2MDAsInV0Ijo0fQ.3-Jvgj34S-x4c9iYXqxvX56Ly1krw1HajB76TBL8sEk");
		
		System.out.println(claims.get("userId"));
		System.out.println(DateUtils.format(claims.getIssuedAt(), DateUtils.FULL_BAR_PATTERN));
	}
}
