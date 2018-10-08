package com.lizikj.login.util;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.TypeReference;
import com.lizikj.cache.Cache;
import com.lizikj.common.constants.UserCacheConstants;
import com.lizikj.common.enums.UserLoginSourceEnum;
import com.lizikj.common.enums.UserTypeEnum;
import com.lizikj.common.util.CookieUtils;
import com.lizikj.common.util.JsonUtils;
import com.lizikj.common.util.SpringContextUtil;
import com.lizikj.common.util.ThreadLocalContext;
import com.lizikj.login.dto.LoginUserInfoDTO;

/**
 * 登录信息工具类
 * @author lijundong 
 * @date 2017年7月20日 下午3:54:04
 */
@SuppressWarnings("rawtypes")
public class LoginInfoUtils {

	private static final Logger logger = LoggerFactory.getLogger(LoginInfoUtils.class);
	
	public static final String SYSTEM = "lz-system";
	
	private static final String LZ_TOKEN = "lz-token";
	
	public static final String IMG_CODE = "imgCode";
	
	public static final String MOBILE_CODE = "mobileCode";
	
	private static Cache cache;
	
	/**
	 * 清理登录信息
	 * @author lijundong
	 * @date 2017年7月20日 下午3:49:35
	 */
	public static void clearLoginInfo(String cookieName){
		HttpServletRequest request = ThreadLocalContext.getHttpRequest();
		HttpServletResponse response = ThreadLocalContext.getHttpResponse();
		
		Byte loginSource = ThreadLocalContext.getLoginSource();
		UserLoginSourceEnum loginSourceEnum = UserLoginSourceEnum.getEnum(loginSource);
		//pos/app清空header，pc/h5清空cookie
		if(UserLoginSourceEnum.APP == loginSourceEnum || UserLoginSourceEnum.POS == loginSourceEnum){
			//清空head
			response.setHeader(LZ_TOKEN, null);
		}else{
			//清空cookie
			CookieUtils.setCookieExpire(request, response, cookieName);
		}
		
		Long userId = ThreadLocalContext.getUserId();
		Byte userType = ThreadLocalContext.getUserType();
		
		//清空单个端口的登录信息
		clearSingleLoginInfo(userId, userType, loginSource);
	}
	
	/**
	 * 根据用户Id，用户类型，清空单个端口的登录信息
	 * @param cache
	 * @param userId
	 * @param userType void
	 * @author lijundong
	 * @date 2017年10月24日 下午6:27:29
	 */
	public static void clearSingleLoginInfo(Long userId, Byte userType, Byte loginSource){
		try {
			//清空redis里面的登录信息
			if(null != userId && null != userType && null != loginSource){
				getCache().hdel(UserCacheConstants.getUserCacheKey(userId, userType), loginSource.toString());
			}
		} catch (Exception e) {}
	}
	
	/**
	 * 根据用户Id，用户类型，清空redis里面所有的登录信息(所有端口)
	 * @param cache
	 * @param userId
	 * @param userType void
	 * @author lijundong
	 * @date 2017年10月24日 下午6:27:29
	 */
	@SuppressWarnings("unchecked")
	public static void clearAllLoginInfo(long userId, Byte userType){
		try {
			//清空redis里面的登录信息
			String cacheKey = UserCacheConstants.getUserCacheKey(userId, userType);
			Map<Object, Object> map = getCache().hgetAll(cacheKey);
			for(Map.Entry<Object, Object> entry: map.entrySet()){
				getCache().hdel(cacheKey, entry.getKey().toString());
			}
		} catch (Exception e) {}
	}
	
	/**
	 * 给head和cookie设置token
	 * @param response
	 * @param claims
	 * @param loginTime void
	 * @author lijundong
	 * @date 2017年7月13日 下午2:56:08
	 */
	public static void setToken(Map<String, Object> claims, Date loginTime, String code, Byte loginSource){
		
		HttpServletRequest request = ThreadLocalContext.getHttpRequest();
		HttpServletResponse response = ThreadLocalContext.getHttpResponse();
		
		//把token设置到请求头
		String token = JWTUtils.createJWT(claims, loginTime);
		
		//PC/h5用cookie
		if(UserLoginSourceEnum.getEnum(loginSource) == UserLoginSourceEnum.PC || UserLoginSourceEnum.getEnum(loginSource) == UserLoginSourceEnum.H5){
			//把token放到cookie中
			CookieUtils.addCookie(request, response, getTokenName(code), token);
		}else{
			response.setHeader(LZ_TOKEN, token);//设置token
			response.setHeader(SYSTEM, code);//设置sysType
		}
	}
	
	/**
	 * 从请求头/cookie中获取jwt
	 * @param request
	 * @param sysType 系统类型 1=agent、2=merchant、3=opt
	 * @return String
	 * @author lijundong
	 * @date 2017年7月13日 下午4:15:11
	 */
	public static String getJWT(HttpServletRequest request) {
		if(StringUtils.isNotBlank(request.getHeader(LZ_TOKEN)))
			return request.getHeader(LZ_TOKEN);
		
		String code = request.getHeader(SYSTEM);
		Cookie cookie = CookieUtils.getCookie(request, getTokenName(code));
		//先从cookie拿
		if(null != cookie)
			return cookie.getValue();
		
		return null;
	}
	
	public static String getTokenName(String code){
		UserTypeEnum enum1 = UserTypeEnum.getEnum(code);
		return enum1 == null ? UserTypeEnum.MERCHANT_USER.getCode() + "-token" : enum1.getCode() + "-token";
	}
	
	/**
	 * 根据用户id，用户类型，登录来源，获取登录信息
	 * @param userId
	 * @param userType
	 * @param loginSource
	 * @return LoginUserInfoDTO
	 * @author lijundong
	 * @date 2018年1月10日 下午2:32:42
	 */
	public static LoginUserInfoDTO getLoginUserInfo(Long userId, Byte userType, Byte loginSource){
		LoginUserInfoDTO loginUserInfoDTO = null;
		try {
			String cacheKey = UserCacheConstants.getUserCacheKey(userId, userType);
			Object object = getCache().hget(cacheKey, loginSource.toString());
			if(null != object){
				loginUserInfoDTO = JsonUtils.parseObject(object, new TypeReference<LoginUserInfoDTO>(){});
			}
		} catch (Exception e) {
			logger.error("从redis中获取用户信息 error", e);
		}
		return loginUserInfoDTO;
	}
	
	/**
	 * 把用户信息存放到redis里面
	 * @param userId
	 * @param userType
	 * @param loginSource
	 * @param loginUserInfoDTO void
	 * @author lijundong
	 * @date 2018年1月10日 下午2:38:53
	 */
	public static void setLoginUserInfo(Long userId, Byte userType, Byte loginSource, LoginUserInfoDTO loginUserInfoDTO){
		try {
			String cacheKey = UserCacheConstants.getUserCacheKey(userId, userType);
			String json = JsonUtils.toJSONString(loginUserInfoDTO);
			getCache().hset(cacheKey, loginSource.toString(), json);
		} catch (Exception e) {
			logger.error("设置用户信息到redis error", e);
		}
	}
	
	public static Cache getCache() {
		if(null == cache){
			cache = SpringContextUtil.getBean("cache");
		}
		return cache;
	}
}
