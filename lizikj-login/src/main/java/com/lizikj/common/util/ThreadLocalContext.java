package com.lizikj.common.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 当前线程中，对象共享工具类
 * @author lijundong 
 * @date 2017年7月13日 下午8:27:09
 */
public class ThreadLocalContext implements AutoCloseable {
 
    private static final ThreadLocal<Map<String, Object>> privateLocal = new ThreadLocal<Map<String, Object>>(){
		@Override
		protected Map<String, Object> initialValue() {
			return new HashMap<String, Object>();
		}
    };
 
    /**
     * httpServletRequest
     */
    public static final String HTTP_REQUEST = "httpRequst";
    
    /**
     * httpServletResponse
     */
    public static final String HTTP_RESPONSE = "httpResponse";
    
    /**
     * 当前用户id：key
     */
    public static final String LOGIN_USER_ID = "login:userId";
    
    /**
     * 当前用户名称：key
     */
    public static final String LOGIN_USER_NAME = "login:userName";
    
    /**
     * 当前员工id：key
     */
    public static final String LOGIN_STAFF_ID = "login:staffId";
    
    /**
     * 当前登录来源
     */
    public static final String LOGIN_SOURCE = "login:loginSource";
    
    /**
     * 用户类型
     */
    public static final String LOGIN_USER_TYPE = "login:userType";
    
    /**
     * 当前登录用户的商户id
     */
    public static final String LOGIN_MERCHANT_ID = "login:merchantId";
    
    /**
     * 当前登录用户的店铺id
     */
    public static final String LOGIN_SHOP_ID = "login:shopId";
    
    /**
     * 当前登录用户的代理商id
     */
    public static final String LOGIN_AGENT_ID = "login:agentId";
    
    /**
     * 当前登录用户的李子会员id
     */
    public static final String LOGIN_MEMBER_ID = "login:memberId";
    
    /**
     * 当前登录用户的商户会员id
     */
    public static final String LOGIN_MERCHANT_MEMBER_ID = "login:merchantMemberId";
    
    /**
     * 当前登录用户的登录来源(微信支付宝)
     */
    public static final String LOGIN_USER_SOURCE = "login:userSource";
    
    /**
     * 当前登录的小程序appid
     */
    public static final String LZ_SC_APPID = "login:smallClient:appid";
    
    /**
     * API版本
     */
    public static final String LZ_API_VERSION = "lz-api-version";
    
    /**
     * 操作系统名称ios/android/pos
     */
    public static final String LZ_SNAME = "lz-sname";
    
    /**
     * 操作系统版本
     */
    public static final String LZ_SVERSION = "lz-sversion";
    
    /**
     * app/pos版本
     */
    public static final String LZ_AVERSION = "lz-aversion";
    
    /**
     * 设备id
     */
    public static final String LZ_DID = "lz-did";
    
    /**
     * 经度
     */
    public static final String LZ_LNG = "lz-lng";
    
    /**
     * 纬度
     */
    public static final String LZ_LAT = "lz-lat";
    
    /**
     * 设备名称(小米1/小米2/iphone6/iphone7)
     */
    public static final String LZ_DEVICE_NAME = "lz-device-name";
    
    
    /**
     * 获取httpRequest
     * @return HttpServletRequest
     * @author lijundong
     * @date 2017年7月20日 下午3:47:47
     */
    public static HttpServletRequest getHttpRequest() {
    	Object v = getThreadValue(HTTP_REQUEST, null);
		return (null == v ? null : (HttpServletRequest) v);
    }
    
    /**
     * 获取httpResponse
     * @return HttpServletRequest
     * @author lijundong
     * @date 2017年7月20日 下午3:47:47
     */
    public static HttpServletResponse getHttpResponse() {
    	Object v = getThreadValue(HTTP_RESPONSE, null);
		return (null == v ? null : (HttpServletResponse) v);
    }
    
    /**
     * 获取登录用户id
     * @return Long
     * @author lijundong
     * @date 2017年7月20日 下午3:48:13
     */
    public static Long getUserId() {
    	Object v = getThreadValue(LOGIN_USER_ID, null);
		return (null == v ? null : (Long) v);
    }
    
    /**
     * 获取登录用户名称
     * @return String
     * @author lijundong
     * @date 2017年7月20日 下午3:48:13
     */
    public static String getUserName() {
    	Object v = getThreadValue(LOGIN_USER_NAME, null);
    	return (null == v ? null : (String) v);
    }
 
    /**
     * 获取登录员工id
     * @return Long
     * @author lijundong
     * @date 2017年7月20日 下午3:48:26
     */
    public static Long getStaffId() {
    	Object v = getThreadValue(LOGIN_STAFF_ID, null);
		return (null == v ? null : (Long) v);
    }
    
    /**
     * 获取登录来源
     * @return String
     * @author lijundong
     * @date 2017年7月20日 下午3:48:38
     */
    public static Byte getLoginSource() {
    	Object v = getThreadValue(LOGIN_SOURCE, null);
		return (null == v ? null : (Byte) v);
    }
    
    /**
     * 获取登录用户类型
     * @return Byte
     * @author lijundong
     * @date 2017年7月21日 下午2:50:04
     */
    public static Byte getUserType() {
    	Object v = getThreadValue(LOGIN_USER_TYPE, null);
		return (null == v ? null : (Byte) v);
    }
    
    /**
     * 获取商户id
     * @return Long
     * @author lijundong
     * @date 2017年7月25日 下午8:50:14
     */
    public static Long getMerchantId(){
    	Object v = getThreadValue(LOGIN_MERCHANT_ID, null);
		return null == v || 0 == (Long)v ? null : (Long) v;
    }
    
    /**
     * 获取店铺id
     * @return Long
     * @author lijundong
     * @date 2017年7月25日 下午8:50:36
     */
    public static Long getShopId(){
    	Object v = getThreadValue(LOGIN_SHOP_ID, null);
		return null == v || 0 == (Long)v ? null : (Long) v;
    }
    
    /**
     * 获取代理商id
     * @return Long
     * @author lijundong
     * @date 2017年7月28日 上午10:44:52
     */
    public static Long getAgentId(){
    	Object v = getThreadValue(LOGIN_AGENT_ID, null);
		return null == v || 0 == (Long)v ? null : (Long) v;
    }
    
    /**
     * 获取API版本
     * @return String
     * @author lijundong
     * @date 2017年8月1日 上午10:33:27
     */
    public static String getLZVersion(){
        Object v = getThreadValue(LZ_API_VERSION, null);
        return (null == v ? null : (String) v);
    }

    /**
     * 获取李子会员id
     * @return String
     * @author lijundong
     * @date 2017年8月1日 上午10:33:27
     */
    public static Long getMemberId(){
        Object v = getThreadValue(LOGIN_MEMBER_ID, null);
        return (null == v ? null : (Long) v);
    }
    
    /**
     * 获取商户会员id
     * @return String
     * @author lijundong
     * @date 2017年8月1日 上午10:33:27
     */
    public static Long getMerchantMemberId(){
        Object v = getThreadValue(LOGIN_MERCHANT_MEMBER_ID, null);
        return (null == v ? null : (Long) v);
    }
    
    /**
     * 获取用户的登录来源
     * @return Byte
     * @author lijundong
     * @date 2018年1月19日 下午12:00:18
     */
    public static Byte getUserSource(){
        Object v = getThreadValue(LOGIN_USER_SOURCE, null);
        return (null == v ? null : (Byte) v);
    }
    
    /**
     * 获取操作系统名称
     * @return Long
     * @author lijundong
     * @date 2017年9月7日 上午10:49:13
     */
    public static String getSname(){
    	Object v = getThreadValue(LZ_SNAME, null);
    	return (null == v ? null : (String) v);
    }
    
    /**
     * 获取操作系统版本
     * @return Long
     * @author lijundong
     * @date 2017年9月7日 上午10:49:53
     */
    public static String getSversion(){
    	Object v = getThreadValue(LZ_SVERSION, null);
    	return (null == v ? null : (String) v);
    }
    
    /**
     * 获取app/pos版本
     * @return String
     * @author lijundong
     * @date 2017年9月7日 上午10:51:18
     */
    public static String getAversion(){
    	Object v = getThreadValue(LZ_AVERSION, null);
    	return (null == v ? null : (String) v);
    }
    
    /**
     * 获取设备id
     * @return String
     * @author lijundong
     * @date 2017年8月1日 上午10:33:27
     */
    public static String getDid(){
    	Object v = getThreadValue(LZ_DID, null);
		return (null == v ? null : (String) v);
    }
    
    /**
     * 获取经度
     * @return String
     * @author lijundong
     * @date 2017年9月7日 上午10:52:07
     */
    public static String getLng(){
    	Object v = getThreadValue(LZ_LNG, null);
    	return (null == v ? null : (String) v);
    }
    
    /**
     * 获取纬度
     * @return String
     * @author lijundong
     * @date 2017年9月7日 上午10:52:10
     */
    public static String getLat(){
    	Object v = getThreadValue(LZ_LAT, null);
    	return (null == v ? null : (String) v);
    }
    
    /**
     * 获取设备名称
     * @return String
     * @author lijundong
     * @date 2018年1月25日 下午4:11:44
     */
    public static String getDeviceName(){
    	Object v = getThreadValue(LZ_DEVICE_NAME, null);
    	return (null == v ? null : (String) v);
    }
    
    /**
     * 获取小程序的appid
     * @author zone
     * @return
     */
    public static String getSmallClientAppid(){
    	Object v = getThreadValue(LZ_SC_APPID, null);
    	return (null == v ? null : (String) v);
    }
    
    @SuppressWarnings("unchecked")
	public static <T> T getThreadValue(String key, T t) {
		Object v = privateLocal.get().get(key);
		return (null == v ? t : (T) v);
	}
    
    public static void putThreadValue(String key, Object value) {
		if (null != value)
			privateLocal.get().put(key, value);
		else
			privateLocal.get().remove(key);
	}
    
    @Override
    public void close() {
    	System.out.println("回收");
    	privateLocal.remove();
    }
    
    public static void remove(){
    	privateLocal.remove();
    }
}