package com.qfjy.redis.redisTemplate;

import com.qfjy.redis.bean.User;
import com.qfjy.redis.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class HashRedisTemplateTest {
    /**
     * 测试string RedisTemplate
     */
    @Test
    public void t1(){
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring_redis.xml");
        UserService userService = ctx.getBean(UserService.class);
       //Hash类型存储
        int id = 3;
        User user = userService.selectById(id);
        System.out.println(user);
    }
}
