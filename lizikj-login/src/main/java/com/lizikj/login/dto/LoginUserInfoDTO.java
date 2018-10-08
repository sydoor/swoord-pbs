package com.lizikj.login.dto;

import java.util.Date;

import com.lizikj.version.dto.BaseDTO;

/**
 * 登录信息封装类
 * 
 * @author lijundong
 * @date 2017年7月25日 下午8:54:09
 */
public class LoginUserInfoDTO extends BaseDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7582215853938929095L;

	/**
	 * 用户Id
	 */
	private Long userId;

	/**
	 * 用户名
	 */
	private String userName;

	/**
	 * 员工id
	 */
	private Long staffId;

	/**
	 * 登录端口来源
	 */
	private Byte loginSource;

	/**
	 * 用户类型
	 */
	private Byte userType;

	/**
	 * 商户id
	 */
	private Long merchantId;

	/**
	 * 店铺id
	 */
	private Long shopId;

	/**
	 * 代理商id
	 */
	private Long agentId;

	/**
	 * 李子会员id
	 */
	private Long memberId;

	/**
	 * 商户会员Id
	 */
	private Long merchantMemberId;

	/**
	 * 登录时间
	 */
	private Date loginTime;

	/**
	 * 登录设备编号
	 */
	private String did;

	/**
	 * 用户来源(微信/支付宝)
	 */
	private Byte userSource;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Long getStaffId() {
		return staffId;
	}

	public void setStaffId(Long staffId) {
		this.staffId = staffId;
	}

	public Byte getLoginSource() {
		return loginSource;
	}

	public void setLoginSource(Byte loginSource) {
		this.loginSource = loginSource;
	}

	public Byte getUserType() {
		return userType;
	}

	public void setUserType(Byte userType) {
		this.userType = userType;
	}

	public Long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}

	public Long getShopId() {
		return shopId;
	}

	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}

	public Long getAgentId() {
		return agentId;
	}

	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public Long getMerchantMemberId() {
		return merchantMemberId;
	}

	public void setMerchantMemberId(Long merchantMemberId) {
		this.merchantMemberId = merchantMemberId;
	}

	public Date getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

	public String getDid() {
		return did;
	}

	public void setDid(String did) {
		this.did = did;
	}

	public Byte getUserSource() {
		return userSource;
	}

	public void setUserSource(Byte userSource) {
		this.userSource = userSource;
	}
}
