package com.lizikj.cache.model;

/**
 * @author Michael.Huang
 * @date 2017/7/22 17:31
 */
public class User {

    private Long userId;

    private Long shopId;

    private String userName;


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

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }
}
