package com.lizikj.login.controller;

import java.io.ByteArrayOutputStream;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lizikj.api.vo.Result;
import com.lizikj.common.util.CodeUtils;
import com.lizikj.login.interceptor.LoginExclude;
import com.lizikj.login.util.LoginInfoUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 图形验证码
 * @author lijundong 
 * @date 2017年8月1日 上午10:26:03
 */
@Controller
@RequestMapping("/{system}/code")
@SuppressWarnings("unchecked")
@Api(value = "图片验证码API", tags = "图片验证码API")
public class ImgCodeController {

	private Logger logger = Logger.getLogger(ImgCodeController.class);
	
	/**
	 * 获取登录图形验证码
	 * @param req
	 * @param resp
	 * @param session void
	 * @author lijundong
	 * @date 2017年8月1日 上午10:25:48
	 */
	@RequestMapping("/getCode")
	@ApiOperation(value = "获取验证码", notes = "获取验证码", httpMethod = "GET")
	@ResponseBody
	@LoginExclude
	public Result<String> getLoginCode(HttpSession session, HttpServletResponse response, @ApiParam(name = "system", value = "登录系统来源,opt=运营中心，user=商户系统,agent=代理商系统, tender=撩美味系统", required = true) @PathVariable(name = "system") String system) {
		String base64Code = "data:image/png;base64,";
		try {
			// 设置响应的类型格式为图片格式
//			response.setContentType("image/jpeg");
//			// 禁止图像缓存。
//			response.setHeader("Pragma", "no-cache");
//			response.setHeader("Cache-Control", "no-cache");
//			response.setDateHeader("Expires", 0);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
			String code = CodeUtils.createLoginCode(160, 50, 4, 100, out);
			
	        base64Code += Base64.encodeBase64String(out.toByteArray());
	        
			session.setAttribute(LoginInfoUtils.IMG_CODE, code);
		} catch (Exception e) {
			logger.error("getImgCode is error", e);
		}
		return Result.SUCESS(base64Code);
	}

	@RequestMapping(value = "/validate/{code}")
	@ResponseBody
	@ApiOperation(value = "校验验证码", notes = "校验验证码", httpMethod = "GET")
	public Result<Object> validate(@PathVariable(name = "code", required = true) String code, HttpSession session){
		String imgCode = (String) session.getAttribute(LoginInfoUtils.IMG_CODE);
		//验证码为空/验证码错误
		if(StringUtils.isBlank(code) || !code.equalsIgnoreCase(imgCode))
			return Result.FAILURE("验证码错误");
		session.removeAttribute(LoginInfoUtils.IMG_CODE);
		return Result.SUCESS();
	}
}
