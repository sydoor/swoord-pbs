package com.lizikj.cache.service;

import com.lizikj.cache.model.User;
import com.lizikj.cache.notation.ExpireCache;
import com.lizikj.cache.notation.GetCache;

import java.util.List;

/**
 * @author Michael.Huang
 * @date 2017/7/22 17:30
 */
public interface UserService {



    /**
     * @param user
     * @return
     */
    User findUserByExample(User user);


    /**
     * @param userId
     * @param shopId
     * @return
     */
    User findUserByUserIdAndShopId(Long userId, Long shopId);

    /**
     *
     * @param userId
     * @return
     */
    User findUserByUserId(Long userId);

    /**
     *
     * @param user
     */
    void update(User user);


    void updateOnlyUserIdKey(User user);
}
