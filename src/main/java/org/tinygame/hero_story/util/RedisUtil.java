package org.tinygame.hero_story.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Redis 实用工具类
 */
public final class RedisUtil {

    /**
     * 日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisUtil.class);

    /**
     * Redis 连接池
     */
    private static JedisPool _jedisPool = null;

    /**
     * 私有化类默认构造器
     */
    private RedisUtil() {
    }

    public static void init() {
        try {
            _jedisPool = new JedisPool("192.168.76.100", 6379);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }
    /**
     * 获取 Redis 实例
     *
     * @return Redis 实例
     * */
    public static Jedis getRedis() {
        if(_jedisPool == null){
            throw new RuntimeException("_jedisPool 尚未初始化");
        }
        Jedis redis = _jedisPool.getResource();
        //redis的密码
        //redis.auth("root");
        return redis;
    }

}
