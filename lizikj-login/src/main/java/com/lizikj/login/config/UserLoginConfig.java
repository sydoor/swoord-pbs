package com.lizikj.login.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.lizikj.login.interceptor.ApiAuthInterceptor;
import com.lizikj.login.interceptor.UserLoginInterceptor;

@Component
public class UserLoginConfig extends WebMvcConfigurerAdapter{

	@Autowired
	private UserLoginInterceptor loginInterceptor;
	
	@Autowired
	private ApiAuthInterceptor apiAuthInterceptor;
	
	/**
	 * 添加免登录校验的url
	 * @author lijundong
	 * @date 2017年8月28日 下午6:24:24
	 */
	public String[] getExcludePathPatterns(){
		List<String> list = new ArrayList<String>();
		list.add("/error");//全局错误页面
		list.add("/swagger-resources/**");//过滤swagger相关url
		list.add("/retry/**");//重跑渠道账单
//		list.add("/agents/user/login");//过滤代理商登录接口
//		list.add("/agents/code/**");//过滤代理商获取验证码接口
//		list.add("/opt/user/login");//过滤opt用户登录接口
//		list.add("/opt/code/**");//过滤opt用户登录获取验证码接口
//		list.add("/user/login");//过滤商户登录接口
//		list.add("/user/code/**");//过滤商户登录获取验证码接口
//		list.add("/user/activate/mobile/code");//获取激活帐号手机验证码接口
//		list.add("/user/activate/user");//激活商户帐号
//		list.add("/user/set/password");//设置商户账户密码
//		list.add("/user/find/mobile/code");//获取找回密码手机验证码接口
//		list.add("/user/validate/mobile/code");//校验手机验证码
//		list.add("/wechat/**");//微信相关接口
//		list.add("/alipay/**");//支付宝相关接口
		return list.toArray(new String[]{});
	}
	/**
	 * 拦截器配置 
	 * @param registry
	 * @author lijundong
	 * @date 2017年7月14日 下午5:23:39
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) { 
		//注册API验签拦截器 
		registry.addInterceptor(apiAuthInterceptor)
		.addPathPatterns("/**");
		
		//注册登录拦截器 
		registry.addInterceptor(loginInterceptor)
		.addPathPatterns("/**")
		//过滤swagger资源和登陆接口
		.excludePathPatterns(getExcludePathPatterns());
	}
	  
	@Override
	public void addCorsMappings(CorsRegistry registry) { 
		registry.addMapping("/**") 
			.allowedOrigins("*") 
			.allowedHeaders("*/*")
			.allowedMethods("*")
			.maxAge(120); 
	}
	
	/**
	 * 视图处理器
	 * 
	 * @return ViewResolver
	 * @author lijundong
	 * @date 2017年7月14日 下午5:10:31
	 */
	@Bean
	public ViewResolver viewResolver() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/WEB-INF/jsp/");
		viewResolver.setSuffix(".jsp");
		return viewResolver;
	}

	/**
	 * 资源处理器
	 * @param registry
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
	}
}