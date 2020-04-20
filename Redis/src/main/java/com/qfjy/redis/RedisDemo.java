package com.qfjy.redis;


import com.qfjy.redis.bean.User;
import com.qfjy.redis.utils.RedisPoolUtil;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashMap;
import java.util.Map;

public class RedisDemo {
    /**
     * java端 通过Jedis 操作Redis服务器
     * @param args
     */
    public static void main(String[] args) {
        //1.连接池Redis Pool基本配置信息
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(5);//最大连接数
        poolConfig.setMaxIdle(1);//空闲连接数

        String host = "192.168.76.132";
        int port = 6379;
        //2连接池
        JedisPool pool = new JedisPool(poolConfig,host,port);
        Jedis jedis = pool.getResource();

        jedis.auth("990305");
        System.out.println(jedis.ping());
    }

    /**
     * 测试字符串string
     * redis中有哪些命令，jedis就有哪些方法
     */
    @Test
    public void t1(){
        //获取连接
        Jedis jedis = new Jedis("192.168.76.132",6379);
        jedis.auth("990305");
        jedis.set("strName","字符串的名称");
        String strName = jedis.get("strName");
        System.out.println(strName);

        jedis.close();
    }
    /**
     * Redis作用：为了减轻（MYsql）的访问压力
     * 需求：判断某key是否存在，如果存在，就从Redis中查询
     *      如果不存在，就从数据库中查询，且要将查询出来的数据存入Redis
     *
     */
    @Test
    public void t2(){
        Jedis jedis = RedisPoolUtil.getJedis();

        String key = "applicationName";//key的名称
        if (jedis.exists(key)){
            String result = jedis.get(key);
            System.out.println("Redis数据库中查到的:"+result);
        }else {
            //在数据库中查询
            String result = "微信开发会议达人";
            jedis.set(key,result);
            System.out.println("mysql数据库中查询得到："+result);
        }

        RedisPoolUtil.close(jedis);
    }

    /**
     * Redis完成对hash类型的操作
     * 需求：hash存储一个对象
     *  判断Redis中是否存在该key，如果存在，直接返回对应值
     *  如果不存在，查询数据库（将查询的结果存入redis）并返回用户
     */

    @Test
    public void t3(){
        Jedis jedis = RedisPoolUtil.getJedis();
        String keys = "users";

        if (jedis.exists(keys)){
            Map<String, String> map = jedis.hgetAll(keys);
            System.out.println("--Redis中查询的结果是：");
            System.out.println(map.get("id")+"\t"+map.get("name")+"\t"+map.get("age")+"\t"+map.get("remark"));

        }else {
            String id = "1";
            String name = "松开";
            String age = "22";
            //查询数据库并返回结果
            jedis.hset(keys,"id",id);
            jedis.hset(keys,"name",name);
            jedis.hset(keys,"age",age);
            jedis.hset(keys,"remark","这是一位男同学");
            System.out.println("数据库中的查询结果："+id+"\t"+name);

        }

        RedisPoolUtil.close(jedis);
    }
    /**
     * 对上面的方法进行优化
     */
    @Test
    public void t4(){
        //Users selectById(String id);
        Jedis jedis = RedisPoolUtil.getJedis();
        int  id = 24;
        String key = User.getKeyName()+id;//user:1

        if (jedis.exists(key)){
            //redis中取出该对象
            Map<String, String> map = jedis.hgetAll(key);

            User u = new User();
            u.setId(Integer.parseInt(map.get("id")));
            u.setName(map.get("name"));
            u.setId(Integer.parseInt(map.get("age")));
            u.setUsername(map.get("username"));
            u.setPassword(map.get("password"));
            System.out.println("Redis数据库中查询的结果："+u);
        } else {
            //mysql数据库查询
            User user = new User();
            user.setId(id);
            user.setName("雪梦萌");
            user.setAge(23);
            user.setPassword("这是号");
            user.setUsername("同学");
            Map<String,String> hash = new HashMap<String,String>();
            hash.put("id",user.getId()+"");
            hash.put("name",user.getName());
            hash.put("age",user.getAge()+"");
            hash.put("username",user.getUsername());
            hash.put("password",user.getPassword());
            jedis.hmset(key,hash);

            System.out.println("mysql查询的User对象是："+user);
        }



        RedisPoolUtil.close(jedis);
    }

}
