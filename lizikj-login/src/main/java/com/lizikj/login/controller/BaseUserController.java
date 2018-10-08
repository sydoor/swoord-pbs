package com.lizikj.login.controller;

import com.lizikj.cache.Cache;
import com.lizikj.common.enums.MessageBizTypeEnum;
import com.lizikj.common.enums.MessageSysModuleEnum;
import com.lizikj.common.enums.UserLoginSourceEnum;
import com.lizikj.common.enums.UserTypeEnum;
import com.lizikj.common.template.MessageContentTemplate;
import com.lizikj.common.util.DateUtils;
import com.lizikj.common.util.ThreadLocalContext;
import com.lizikj.login.dto.LoginUserInfoDTO;
import com.lizikj.login.util.JWTUtils;
import com.lizikj.login.util.LoginInfoUtils;
import com.lizikj.merchant.dto.ShopLoginEquipmentRecordsDTO;
import com.lizikj.merchant.facade.IShopLoginEquipmentRecordsWriteFacade;
import com.lizikj.message.api.dto.PushMsgDTO;
import com.lizikj.message.api.dto.UserDeviceRecordDTO;
import com.lizikj.message.api.enums.MessageTypeEnum;
import com.lizikj.message.api.enums.SendTypeEnum;
import com.lizikj.message.api.enums.StatusEnum;
import com.lizikj.message.api.enums.TerminalTypeEnum;
import com.lizikj.message.api.facade.IMsgPushFacade;
import com.lizikj.message.api.facade.IMsgPushRegisterFacade;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * B端登录controller层公共类
 *
 * @author lijundong
 * @date 2017年7月24日 下午3:51:40
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@Controller
public class BaseUserController {

    private static final Logger logger = LoggerFactory.getLogger(BaseUserController.class);

    @Autowired
    protected Cache cache;

    @Autowired
    private IMsgPushRegisterFacade msgPushRegisterFacade;

    @Autowired
    private IMsgPushFacade msgPushFacade;

    @Autowired
    private Environment environment;

    @Autowired
    private IShopLoginEquipmentRecordsWriteFacade shopLoginEquipmentRecordsWriteFacade;

    /**
     * 统一登录处理，设置token，cookie，redis等
     *
     * @param request
     * @param response
     * @param userId
     * @param staffId
     * @param loginSource void
     * @author lijundong
     * @date 2017年7月24日 下午3:30:14
     */
    protected void toLogin(LoginUserInfoDTO loginUserInfoDTO) {
        //封装claims
        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put(JWTUtils.USER_ID, loginUserInfoDTO.getUserId());//用户id
        claims.put(JWTUtils.LOGIN_SOURCE, loginUserInfoDTO.getLoginSource());//面向的用户， 哪个登陆端的
        claims.put(JWTUtils.USER_TYPE, loginUserInfoDTO.getUserType());//用户类型

        UserTypeEnum userType = UserTypeEnum.getEnum(loginUserInfoDTO.getUserType());
        switch (userType) {
            case MERCHANT_USER:
                claims.put(JWTUtils.STAFF_ID, loginUserInfoDTO.getStaffId());
                claims.put(JWTUtils.MERCHANT_ID, loginUserInfoDTO.getMerchantId());
                claims.put(JWTUtils.SHOP_ID, loginUserInfoDTO.getShopId());
                claims.put(JWTUtils.AGENT_ID, loginUserInfoDTO.getAgentId());
                break;
            case AGENT_USER:
                claims.put(JWTUtils.AGENT_ID, loginUserInfoDTO.getAgentId());
                break;
            case OPT_USER:

                break;
            case CLIENT_USER:
            case SMALL_CLIENT_USER:
                claims.put(JWTUtils.SHOP_ID, loginUserInfoDTO.getShopId());//设置店铺Id
                claims.put(JWTUtils.MERCHANT_ID, loginUserInfoDTO.getMerchantId());
                claims.put(JWTUtils.MEMBER_ID, loginUserInfoDTO.getMemberId());//李子会员id
                claims.put(JWTUtils.MERCHANT_MEMBER_ID, loginUserInfoDTO.getMerchantMemberId());//商户会员id
                claims.put(JWTUtils.USER_SOURCE, loginUserInfoDTO.getUserSource());//用户来源
                claims.put(JWTUtils.USER_ID, loginUserInfoDTO.getUserId());//第三方用户的userid
                break;
            case TENDER_USER:
                claims.put(JWTUtils.USER_NAME, loginUserInfoDTO.getUserName());//用户名
                break;
            default:
                break;
        }
        //存入当前登录环境(dev/test/prod)
        String property = environment.getProperty("spring.profiles.active");
        claims.put(JWTUtils.ENV, property);

        //生成登录时间
        Date loginTime = new Date();

        //给head和cookie设置jwtToken
        LoginInfoUtils.setToken(claims, loginTime, userType.getCode(), loginUserInfoDTO.getLoginSource());

        loginUserInfoDTO.setLoginTime(loginTime);
        loginUserInfoDTO.setDid(ThreadLocalContext.getDid());//设置设备号

        //注册消息中心设备id
        UserLoginSourceEnum loginSourceEnum = UserLoginSourceEnum.getEnum(loginUserInfoDTO.getLoginSource());
        //只记录pos和app的
        if (UserLoginSourceEnum.POS == loginSourceEnum || UserLoginSourceEnum.APP == loginSourceEnum) {
            TerminalTypeEnum terminalTypeEnum = TerminalTypeEnum.getEnum(ThreadLocalContext.getSname());
            if (null != terminalTypeEnum) {
                try {
                    UserDeviceRecordDTO recordDTO = new UserDeviceRecordDTO();
                    recordDTO.setShopId(loginUserInfoDTO.getShopId());
                    recordDTO.setMerchantId(loginUserInfoDTO.getMerchantId());
                    recordDTO.setSn(ThreadLocalContext.getDid());
                    recordDTO.setTerminalTypeEnum(terminalTypeEnum);
                    recordDTO.setUserId(loginUserInfoDTO.getUserId());
                    recordDTO.setLoginTime(new Date());
                    recordDTO.setUserTypeEnum(userType);
                    msgPushRegisterFacade.refreshDeviceUseRecord(recordDTO);
                } catch (Exception e) {
                    if (logger.isErrorEnabled()) {
                        logger.error("刷新用户终端（app/pos）使用记录error, did={}, shopId={}", ThreadLocalContext.getDid(), loginUserInfoDTO.getUserId());
                    }
                }

                try {
                    if (UserLoginSourceEnum.POS == loginSourceEnum || UserLoginSourceEnum.APP == loginSourceEnum) {
                        ShopLoginEquipmentRecordsDTO equipmentRecordsDTO = new ShopLoginEquipmentRecordsDTO();
                        equipmentRecordsDTO.setMerchantId(loginUserInfoDTO.getMerchantId());
                        equipmentRecordsDTO.setShopId(loginUserInfoDTO.getShopId());
                        equipmentRecordsDTO.setSn(ThreadLocalContext.getDid());
                        equipmentRecordsDTO.setEquipmentTypeName(ThreadLocalContext.getDeviceName());
                        equipmentRecordsDTO.setEquipmentType((byte) terminalTypeEnum.getCode());
                        shopLoginEquipmentRecordsWriteFacade.addEquipmentRecord(equipmentRecordsDTO);
                    }
                } catch (Exception e) {
                	logger.error("用户登录设备信息保存失败，loginSource:{},merchantId:{},shopId:{},sn:{}",loginSourceEnum, loginUserInfoDTO.getMerchantId(), loginUserInfoDTO.getShopId(), ThreadLocalContext.getDid(), e);
                }

            }
        }

        //把登陆信息放到缓存中
        loginInfoSetCache(loginUserInfoDTO);
    }

    /**
     * <p>把登录信息放到缓存中
     * <p>1、判断登录来源是否是app/pos
     * <p>2、判断是否存在旧的登录信息，如果存在，判断新老设备号是否不一致，如果不一致，则发送“踢下线通知消息”
     *
     * @param userId
     * @param loginSource
     * @param loginTime   void
     * @author lijundong
     * @date 2017年7月13日 下午2:58:20
     */
    private void loginInfoSetCache(LoginUserInfoDTO loginUserInfoDTO) {
        //app/pos才有互踢机制
        UserLoginSourceEnum loginSourceEnum = UserLoginSourceEnum.getEnum(loginUserInfoDTO.getLoginSource());
        if (UserLoginSourceEnum.POS == loginSourceEnum || UserLoginSourceEnum.APP == loginSourceEnum) {

            //如果存在旧的登录信息，判断当前登录用户的设备号是否跟旧的设备号一致，不一致则发送“踢下线通知消息”
            LoginUserInfoDTO oldLoginUserInfo = LoginInfoUtils.getLoginUserInfo(loginUserInfoDTO.getUserId(), loginUserInfoDTO.getUserType(), loginUserInfoDTO.getLoginSource());
            if (null != oldLoginUserInfo) {
                String oldDid = oldLoginUserInfo.getDid();
                //判断新老设备号是否不一致
                if (StringUtils.isNotBlank(oldDid) && !oldDid.equals(loginUserInfoDTO.getDid())) {
                    try {
                        if (logger.isInfoEnabled()) {
                            logger.info("发送单一登录踢下线通知, userId={}, shopId={}, newDid={}, oldDid={}", loginUserInfoDTO.getUserId(), loginUserInfoDTO.getShopId(), loginUserInfoDTO.getDid(), oldDid);
                        }

                        String msgContent = "您的账号于" + DateUtils.format(loginUserInfoDTO.getLoginTime(), DateUtils.FULL_BAR_PATTERN) + "在其它设备上登录，如非本人操作，请修改密码或联系我们";
                        MessageContentTemplate template = new MessageContentTemplate(MessageSysModuleEnum.LOGIN_MODULE, MessageBizTypeEnum.LOGIN_ABNORMAL, msgContent);

                        //发送踢下线通知
                        PushMsgDTO pushMsgDTO = new PushMsgDTO();
                        pushMsgDTO.setMsgTitle("账号登录异常(李子科技）");
                        pushMsgDTO.setMessageTypeEnum(MessageTypeEnum.MESSAGE_TYPE_ENUM);
                        //通知栏显示内容
                        pushMsgDTO.setMsgContent(template.getContent());
                        pushMsgDTO.setExtras(template.buildMsgExtra());
                        pushMsgDTO.setSendTypeEnum(SendTypeEnum.AUDIENCE_SINGLE);
                        pushMsgDTO.setMustArrive(StatusEnum.NO);
                        msgPushFacade.pushMsgToDevice(oldDid, pushMsgDTO);
                    } catch (Exception e) {
                        if (logger.isErrorEnabled()) {
                            logger.error("发送单一登录踢下线通知error, did={}", oldDid);
                        }
                    }
                }
            }
        }

        //重新生成新的登录信息
        LoginInfoUtils.setLoginUserInfo(loginUserInfoDTO.getUserId(), loginUserInfoDTO.getUserType(), loginUserInfoDTO.getLoginSource(), loginUserInfoDTO);
    }
}
