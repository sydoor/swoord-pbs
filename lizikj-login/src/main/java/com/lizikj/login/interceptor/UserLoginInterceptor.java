package com.lizikj.login.interceptor;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.lizikj.api.vo.Result;
import com.lizikj.common.enums.UserLoginSourceEnum;
import com.lizikj.common.enums.UserTypeEnum;
import com.lizikj.common.util.DateUtils;
import com.lizikj.common.util.ThreadLocalContext;
import com.lizikj.login.dto.LoginUserInfoDTO;
import com.lizikj.login.util.JWTUtils;
import com.lizikj.login.util.LoginInfoUtils;
import com.lizikj.merchant.dto.UserProfileDTO;
import com.lizikj.merchant.facade.IMerchantAuthApiReadFacade;

import io.jsonwebtoken.Claims;

/**
 * 登录拦截器
 * @author lijundong 
 * @date 2017年7月21日 下午2:39:32
 */
@Component
public class UserLoginInterceptor implements HandlerInterceptor {

	private static Logger logger = LoggerFactory.getLogger(UserLoginInterceptor.class);
	
	@Autowired
	private IMerchantAuthApiReadFacade merchantAuthApiReadFacade;
	
//	@Autowired
//	private IOptAuthApiReadFacade optAuthApiReadFacade;
	
	@Autowired
	private Environment environment;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		//使用前清理下，tomcat使用的是线程池，线程会重复
		//放到ApiAuthInterceptor清理，因为ApiAuthInterceptor先执行
//		ThreadLocalContext.remove();
				
		//动态过滤一些不需要登录拦截的请求
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		if(null != handlerMethod.getBeanType().getAnnotation(LoginExclude.class) || null != handlerMethod.getMethod().getAnnotation(LoginExclude.class)){
		  return true;
		}
    
		ThreadLocalContext.putThreadValue(ThreadLocalContext.HTTP_REQUEST, request);
		ThreadLocalContext.putThreadValue(ThreadLocalContext.HTTP_RESPONSE, response);
    
		String requestURI = request.getRequestURI();
    
		// 获取jwt
		String jwt = LoginInfoUtils.getJWT(request);

		// 校验jwt的有效性
		if (StringUtils.isNotBlank(jwt)) {
			Claims claims = null;
			try {
				claims = JWTUtils.parseJWT(jwt);
			} catch (Exception e) {
				logger.error("解析jwt时发生异常, requestURI={}, jwt={}", requestURI, jwt);
			}

			if (null == claims) {
				return toResponse(response, true, "非法操作，请登录");
			}
			
			// 判断当前时间是否大于jwt的最后有效时间，如果不是，则登陆过期
			if (System.currentTimeMillis() >= claims.getExpiration().getTime()) {
				return toResponse(response, true, "登录已过期，请重新登录");
			}
			
			//判断当前登录环境(dev/test/prod)
			String property = environment.getProperty("spring.profiles.active");
			String env = claims.get(JWTUtils.ENV, String.class);
			if(property != null && !property.equals(env)){
				return toResponse(response, false, env + "不能访问" + property + "的接口");
			}
			
			//获取登录用户id
			long userId = new Long(claims.get(JWTUtils.USER_ID).toString());
			String userName = claims.get(JWTUtils.USER_NAME, String.class);
			byte userType = new Byte(claims.get(JWTUtils.USER_TYPE).toString());
			//登录来源
			byte loginSource = new Byte(claims.get(JWTUtils.LOGIN_SOURCE).toString());
			Object userSourceObj = claims.get(JWTUtils.USER_SOURCE);
			Byte userSource = new Byte(null == userSourceObj ? "0" : userSourceObj.toString());
						
			Object shopObj = claims.get(JWTUtils.SHOP_ID);
			Object merchantObj = claims.get(JWTUtils.MERCHANT_ID);
			Object agentObj = claims.get(JWTUtils.AGENT_ID);
			
			Long shopId = new Long(null == shopObj ? "0" : shopObj.toString());
			Long merchantId = new Long(null == merchantObj ? "0" : merchantObj.toString());
			Long agentId = new Long(null == agentObj ? "0" : agentObj.toString());
			
			//把用户id放到共享线程中
			ThreadLocalContext.putThreadValue(ThreadLocalContext.LOGIN_USER_ID, userId);
			ThreadLocalContext.putThreadValue(ThreadLocalContext.LOGIN_USER_NAME, userName);
			//把登陆来源放到共享线程中
			ThreadLocalContext.putThreadValue(ThreadLocalContext.LOGIN_SOURCE, loginSource);
			//把用户类型放到共享线程中
			ThreadLocalContext.putThreadValue(ThreadLocalContext.LOGIN_USER_TYPE, userType);
			ThreadLocalContext.putThreadValue(ThreadLocalContext.LOGIN_USER_SOURCE, userSource);
			
			//从redis中获取登录信息
			LoginUserInfoDTO loginUserInfoDTO = LoginInfoUtils.getLoginUserInfo(userId, userType, loginSource);
			if (null == loginUserInfoDTO) {
				toResponse(response, true, "登录信息不存在，请重新登录");
				return false;
			}
			
			UserTypeEnum userTypeEnum = UserTypeEnum.getEnum(userType);
			//如果是商户用户类型，则要判断是否有员工id
			if(userTypeEnum == UserTypeEnum.MERCHANT_USER){
				//获取登录员工id
				long staffId = new Long(claims.get(JWTUtils.STAFF_ID).toString());
				
				//如果一个用户有多个员工身份的时候，需要选择其中一个员工进行二次登录，这时候不拦截，其他都拦截校验是否有staffId
				if( !"/user/merchant/staff/login".equals(requestURI) && 0L == staffId){
					return toResponse(response, true, "员工id不存在，请重新登录");
				}
				
				//针对商户APP/pos登录的，做单端登陆操作
				UserLoginSourceEnum loginSourceEnum = UserLoginSourceEnum.getEnum(loginSource);
				if (UserLoginSourceEnum.APP.equals(loginSourceEnum) || UserLoginSourceEnum.POS.equals(loginSourceEnum)){
					Date loginTime = loginUserInfoDTO.getLoginTime();
					//登录时间
					Date issuedAt = claims.getIssuedAt();
					// 登录时间对不上，则重新登陆
					//ps: 从redis中拿出时间后，时间毫秒多了一点点，所以只能判断2个时间相减的绝对值在1秒内，则视为同一个时间
					if (Math.abs(loginTime.getTime() - issuedAt.getTime()) > 1000) {
						return toResponse(response, false, "您的账号于"+ DateUtils.format(loginTime, DateUtils.FULL_BAR_PATTERN) +"在其它设备上登录，如非本人操作，请修改密码或联系我们");
					}
					
					//校验店铺是否属于当前商户
					if (null == shopId || shopId <= 0) {
						shopId = ThreadLocalContext.getShopId();
					}
					
					if (null == merchantId || merchantId <=0) {
						merchantId = ThreadLocalContext.getMerchantId();
					}
				}
				
				//封装商户相关信息
				//把员工id放到共享线程中
				ThreadLocalContext.putThreadValue(ThreadLocalContext.LOGIN_STAFF_ID, staffId);
				//把商户id放到共享线程中
				ThreadLocalContext.putThreadValue(ThreadLocalContext.LOGIN_MERCHANT_ID, merchantId);
				//把店铺id放到共享线程中
				ThreadLocalContext.putThreadValue(ThreadLocalContext.LOGIN_SHOP_ID, shopId);
				//把代理商id放到共享线程中
				ThreadLocalContext.putThreadValue(ThreadLocalContext.LOGIN_AGENT_ID, agentId);
			}
			//如果是代理商用户类型
			else if(userTypeEnum == UserTypeEnum.AGENT_USER){
				//封装代理商相关信息
				//把代理商id放到共享线程中
				ThreadLocalContext.putThreadValue(ThreadLocalContext.LOGIN_AGENT_ID, agentId);
			}
			//如果是客户端用户
			else if(userTypeEnum == UserTypeEnum.CLIENT_USER){
				//封装客户端用户相关信息
				long memberId = new Long(claims.get(JWTUtils.MEMBER_ID).toString());
				Object merchantMemberIdObj = claims.get(JWTUtils.MERCHANT_MEMBER_ID);
				long merchantMemberId = new Long(null == merchantMemberIdObj ? "0" : merchantMemberIdObj.toString());
				
				//校验店铺是否属于当前商户
				if (null == shopId || shopId <= 0) {
					shopId = ThreadLocalContext.getShopId();
				}
				if (null == merchantId || merchantId <=0) {
					merchantId = ThreadLocalContext.getMerchantId();
				}
				//把会员id放到共享线程中
				ThreadLocalContext.putThreadValue(ThreadLocalContext.LOGIN_MEMBER_ID, memberId);
				//把会员id放到共享线程中
				ThreadLocalContext.putThreadValue(ThreadLocalContext.LOGIN_MERCHANT_MEMBER_ID, merchantMemberId);
				//把店铺id放到共享线程中
				ThreadLocalContext.putThreadValue(ThreadLocalContext.LOGIN_SHOP_ID, shopId);
				//把商户id放到共享线程中
				ThreadLocalContext.putThreadValue(ThreadLocalContext.LOGIN_MERCHANT_ID, merchantId);
			}
			//如果是撩美味用户
			else if(userTypeEnum == UserTypeEnum.TENDER_USER){
				
			//如果是小程序用户
			} else if (userTypeEnum == UserTypeEnum.SMALL_CLIENT_USER) {
				//封装客户端用户相关信息
//				long memberId = new Long(claims.get(JWTUtils.MEMBER_ID).toString());
				Object merchantMemberIdObj = claims.get(JWTUtils.MERCHANT_MEMBER_ID);
				long merchantMemberId = new Long(null == merchantMemberIdObj ? "0" : merchantMemberIdObj.toString());
				
				//校验店铺是否属于当前商户
				if (null == shopId || shopId <= 0) {
					shopId = ThreadLocalContext.getShopId();
				}
				if (null == merchantId || merchantId <=0) {
					merchantId = ThreadLocalContext.getMerchantId();
				}
				//把会员id放到共享线程中
//				ThreadLocalContext.putThreadValue(ThreadLocalContext.LOGIN_MEMBER_ID, memberId);
				//把会员id放到共享线程中
				ThreadLocalContext.putThreadValue(ThreadLocalContext.LOGIN_MERCHANT_MEMBER_ID, merchantMemberId);
				//把店铺id放到共享线程中
				ThreadLocalContext.putThreadValue(ThreadLocalContext.LOGIN_SHOP_ID, shopId);
				//把商户id放到共享线程中
				ThreadLocalContext.putThreadValue(ThreadLocalContext.LOGIN_MERCHANT_ID, merchantId);
			//校验访问的url权限
			}if( !isPermitted(userId, userType, loginSource, requestURI)){
				return toResponse(response, false, "您没有权限访问该URL");
			}
		} else {
			return toResponse(response, true, "登录已过期，请重新登录");
		}
		
		return true;
	}

	/**
	 * 统一处理返回值
	 * 
	 * @param response
	 * @param result
	 * @throws IOException
	 * @author lijundong
	 * @date 2017年7月13日 下午3:00:07
	 */
	private boolean toResponse(HttpServletResponse response, boolean isClear, String result) throws IOException {
		HttpServletRequest request = ThreadLocalContext.getHttpRequest();
		if(isClear){
			//清空登录信息
			String cookieName = LoginInfoUtils.getTokenName(request.getHeader(LoginInfoUtils.SYSTEM));
			LoginInfoUtils.clearLoginInfo(cookieName);
		}
		
		Result<Object> resultJson = new Result<Object>(0, "9999", result);
		String message = JSONObject.toJSONString(resultJson);
		
		//设置跨域设置
		response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Content-Type", "application/json");
		response.setCharacterEncoding("utf-8");
		response.getWriter().write(message);
		return false;
	}

	/**
	 * 校验访问url是否有权限
	 * @param userId
	 * @param userType
	 * @param loginSource
	 * @param requestURI
	 * @return boolean
	 * @author lijundong
	 * @date 2017年7月25日 上午11:48:36
	 */
	private boolean isPermitted(long userId, byte userType, byte loginSource, String requestURI){
		switch (UserTypeEnum.getEnum(userType)) {
			case MERCHANT_USER:
				UserProfileDTO userProfileDTO = new UserProfileDTO();
				userProfileDTO.setUserId(userId);
				userProfileDTO.setUserType(userType);
				userProfileDTO.setLoginSource(loginSource);
				return merchantAuthApiReadFacade.isPermitted(userProfileDTO, requestURI);
			case OPT_USER:
//				OptUserProfileDTO optUserProfileDTO = new OptUserProfileDTO();
//				optUserProfileDTO.setUserId(userId);
//				return optAuthApiReadFacade.isPermitted(optUserProfileDTO, requestURI);
				return true;
			default:
				return true;
		}
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {}
	
}
