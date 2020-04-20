package com.qfjy.redis.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisPoolUtil {
    private static JedisPool pool;
    static {
        //1.连接池Redis Pool 基本配置信息
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(1);//最大空闲数
        poolConfig.setMaxTotal(5);//最大连接数
        //2.连接池
        String host = "192.168.76.132";
        int port = 6379;
        pool = new JedisPool(poolConfig,host,port);
    }
    public static Jedis getJedis(){
        Jedis jedis = pool.getResource();
        jedis.auth("990305");
        return jedis;
    }
    public static void close(Jedis jedis) {
        jedis.close();
    }
}
