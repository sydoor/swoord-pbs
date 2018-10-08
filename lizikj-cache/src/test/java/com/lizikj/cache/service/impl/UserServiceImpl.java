package com.lizikj.cache.service.impl;

import com.lizikj.cache.model.User;
import com.lizikj.cache.notation.ExpireCache;
import com.lizikj.cache.notation.GetCache;
import com.lizikj.cache.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael.Huang
 * @date 2017/7/25 17:15
 */
@Service
public class UserServiceImpl implements UserService {


    @Override
    @GetCache(prefix = "LIZIKJ:USER_", paramKeys = {"#user.userId", "#user.shopId"})
    public User findUserByExample(User user) {
        User u = new User();
        u.setUserId(888l);
        u.setShopId(999l);
        return u;
    }

    @Override
    @GetCache(prefix = "LIZIKJ:USER_", paramKeys = {"#userId", "#shopId"})
    public User findUserByUserIdAndShopId(Long userId, Long shopId) {
        User u = new User();
        u.setUserId(888l);
        u.setShopId(999l);
        return u;
    }

//    @Override
    @GetCache(prefix = "LIZIKJ:USER_")
    public User findUserByUserId(Long userId) {
        User u = new User();
        u.setUserId(888l);
        u.setShopId(999l);
        return u;
    }

    @Override
    @ExpireCache(prefix = "LIZIKJ:USER_", paramKeys = {"#user.userId", "#user.shopId"})
    public void update(User user) {
        System.out.println("do update");
    }

    @Override
    @ExpireCache(prefix = "LIZIKJ:USER_", paramKeys = {"#user.userId"})
    public void updateOnlyUserIdKey(User user) {

        System.out.println("do updateOnlyUserIdKey");
    }
}
