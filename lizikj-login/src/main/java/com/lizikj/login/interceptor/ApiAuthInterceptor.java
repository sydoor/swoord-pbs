package com.lizikj.login.interceptor;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.lizikj.common.util.ThreadLocalContext;

/**
 * API验签拦截器
 * 
 * @author lijundong
 * @date 2017年7月21日 下午2:39:40
 */
@Component
public class ApiAuthInterceptor implements HandlerInterceptor {

	private static Logger logger = LoggerFactory.getLogger(ApiAuthInterceptor.class);
	
	private Map<String, String> appKeyMap = new HashMap<String, String>();
	
	private static final String SNAME = "sname";
	
	@PostConstruct
	public void init(){
		//pos=5e0bdcbddccca4d66d74ba8c1cee1a68
		appKeyMap.put("pos", "Jt0M1ls6HXDV54zyKvq7OFmEbicNjIpWwUPnQgGLe9kC3oSBrdTaR2AuZhxf8Y");
		//android=c31b32364ce19ca8fcd150a417ecce58
		appKeyMap.put("android", "Jt0M1ls6HXDV54zyKvq7OFmEbicNjIpWwUPnQgGLe9kC3oSBrdTaR2AuZhxf8Y");
		//ios=9e304d4e8df1b74cfa009913198428ab
		appKeyMap.put("ios", "Jt0M1ls6HXDV54zyKvq7OFmEbicNjIpWwUPnQgGLe9kC3oSBrdTaR2AuZhxf8Y");
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		//使用前清理下，tomcat使用的是线程池，线程会重复
		ThreadLocalContext.remove();
				
		String startStr = "lz-";
		Map<String, String> params = new HashMap<String, String>();
		
		// 获取所有的头部参数
		Enumeration<String> headerNames = request.getHeaderNames();
		for (Enumeration<String> e = headerNames; e.hasMoreElements();) {
			String thisName = e.nextElement().toString();
			if(thisName.startsWith(startStr)){
				String thisValue = request.getHeader(thisName);
//				System.out.println("header的key:" + thisName + "--------------header的value:" + thisValue);
				//去掉lz-
				thisName = thisName.substring(startStr.length());
				params.put(thisName.toLowerCase(), thisValue);
			}
		}

		/*//获取加密key
		String appKey = params.get("appkey");
		//获取签名
		String sign = params.remove("sign");
		
		//计算签名
		String signature = HMacSHA1Utils.getSignature(SignUtils.signSource(params), appKeyMap.get(appKey));
		
		if(!sign.equals(signature)){
			logger.error("ApiAuthInterceptor | 验签不通过");
			return toResponse(response, "验签不通过");
		}*/
		Long shopId = 0L;
		String shopIdValue  = params.get("shopid");
		if (StringUtils.isNotBlank(shopIdValue) && StringUtils.isNumeric(shopIdValue)) {
			shopId = Long.valueOf(shopIdValue);
			ThreadLocalContext.putThreadValue(ThreadLocalContext.LOGIN_SHOP_ID, shopId);
		}
		
		Long merchantId = 0L;
		String merchantIdValue = params.get("merchantid");
		if (StringUtils.isNotBlank(merchantIdValue)  && StringUtils.isNumeric(merchantIdValue) ) {
			merchantId = Long.valueOf(merchantIdValue);
			ThreadLocalContext.putThreadValue(ThreadLocalContext.LOGIN_MERCHANT_ID, merchantId);
		}
//		if(StringUtils.isBlank(did))
//			return toResponse(response, "设备编号不能为空");
		
		String sname = params.get("sname");
		ThreadLocalContext.putThreadValue(ThreadLocalContext.LZ_SNAME, sname);
		
		String sversion = params.get("sversion");
		ThreadLocalContext.putThreadValue(ThreadLocalContext.LZ_SVERSION, sversion);
		
		String aversion = params.get("aversion");
		ThreadLocalContext.putThreadValue(ThreadLocalContext.LZ_AVERSION, aversion);
		
		String did = params.get("did");
		ThreadLocalContext.putThreadValue(ThreadLocalContext.LZ_DID, did);
		
		String lng = params.get("lng");
		ThreadLocalContext.putThreadValue(ThreadLocalContext.LZ_LNG, lng);
		
		String lat = params.get("lat");
		ThreadLocalContext.putThreadValue(ThreadLocalContext.LZ_LAT, lat);
		
		String deviceName = params.get("device-name");
		ThreadLocalContext.putThreadValue(ThreadLocalContext.LZ_DEVICE_NAME, deviceName);
		return true;
	}

	/**
	 * 将回调请求的参数装载到parameters
	 * 
	 * @param reqParams
	 * @return Map<String,String>
	 * @author lijundong
	 * @date 2017年7月21日 下午3:04:22
	 */
	public Map<String, String> initRequestParams(Map<String, String[]> reqParams) {
		Map<String, String> parameters = new HashMap<String, String>();
		Iterator<String> it = reqParams.keySet().iterator();
		while (it.hasNext()) {
			String k = (String) it.next();
			String v = ((String[]) reqParams.get(k))[0];
			if (StringUtils.isNotBlank(v)) {
				parameters.put(k, v.trim());
			}
		}
		return parameters;
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
	private boolean toResponse(HttpServletResponse response, String result) throws IOException {
		String message = "{\"code\": 0, \"bizCode\": \"10001\", \"message\": \""+ result +"\", \"data\": null}";
		response.setHeader("Content-type", "application/json;charset=UTF-8");
		response.setCharacterEncoding("utf-8");
		response.getWriter().write(message);
		return false;
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}
}
