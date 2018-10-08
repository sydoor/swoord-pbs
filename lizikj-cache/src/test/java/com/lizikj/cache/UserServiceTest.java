package com.lizikj.cache;

import com.lizikj.cache.model.User;
import com.lizikj.cache.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

/**
 * @author Michael.Huang
 * @date 2017/7/25 17:38
 */
//@ActiveProfiles("dev")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private Cache cache;


    @Test
    public void testAll() {
        User user = new User();
        user.setUserId(55500l);
        user.setShopId(999900l);

        String key = "LIZIKJ:USER_" + user.getUserId() + "_" + user.getShopId();
        cache.delete(key);
        User result = userService.findUserByExample(user);

        Assert.notNull(result, "result not null");
        Object object = cache.get(key);
        Assert.isInstanceOf(User.class, object);
        Assert.notNull(object, "Redis缓存内容为空，key:" + key);

        //更新清除缓存
        userService.update(user);
        object = cache.get(key);

        Assert.isNull(object, "缓存获取内容为空");

        result = userService.findUserByUserIdAndShopId(user.getUserId(), user.getShopId());
        Assert.notNull(result, "result not null");


        userService.updateOnlyUserIdKey(user);

        object = cache.get(key);
        Assert.notNull(object, "缓存没有被清除");
    }


}
