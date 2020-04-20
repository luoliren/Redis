package com.qfjy.redis.redisTemplate;

import com.qfjy.redis.bean.User;
import com.qfjy.redis.impl.UserServiceImpl;
import com.qfjy.redis.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

public class ListRedisTemplateTest {
    /**
     * 测试string RedisTemplate
     */
    @Test
    public void t1(){
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring_redis.xml");
        UserService userService = ctx.getBean(UserService.class);
       //list类型存储
        userService.listAdd();
        int pageNum = 1;//当前页
        int pageSize = 3;//每页显示3条
        List<String> list = (List<String>) userService.listRange(pageNum,pageSize);

        for (String list1: list) {
            System.out.println(list1);
        }
    }

    /**
     * 下单物流流程 队列示例
     */
    @Test
    public void t2(){
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring_redis.xml");
        UserService userService = ctx.getBean(UserService.class);
        /**
         * 模拟物流的下单流程
         */
        String cardId = "1001";
        //1.用户进行值赋完后曾后，生成对应的物流队列
        userService.listQueueInit(cardId);
        System.out.println("--------------------大大物流--------------------");
        System.out.println("--------当前任务队列：");
        List<String> list1 = userService.listQueueWait(cardId);
        for (String s : list1) {
            System.out.println(s);
        }
        System.out.println("--------------物流小哥开始运物流信息----------------");
        userService.listQueueTouch(cardId);

        System.out.println("--------物流小哥操作后当前任务队列：");
        List<String> list3 = userService.listQueueWait(cardId);
        for (String s : list3) {
            System.out.println(s);
        }

        System.out.println("-------已完成任务列");
        List<String> list = userService.listQueueSucc(cardId);
        for (String s : list) {
            System.out.println(s);
        }

    }
}
