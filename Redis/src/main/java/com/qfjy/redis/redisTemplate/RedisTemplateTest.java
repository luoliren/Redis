package com.qfjy.redis.redisTemplate;

import com.qfjy.redis.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RedisTemplateTest {
    /**
     * 测试string RedisTemplate
     */
    @Test
    public void t1(){
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring_redis.xml");
        UserService userService = ctx.getBean(UserService.class);
        String key = "applicationName";
        String result = userService.getString(key);
        System.out.println(result);
    }
}
