package com.haoran.Brainstorming.plugin;

import com.haoran.Brainstorming.config.service.BaseService;
import com.haoran.Brainstorming.model.SystemConfig;
import com.haoran.Brainstorming.service.ISystemConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.params.SetParams;

import javax.annotation.Resource;


@Component
@DependsOn("mybatisPlusConfig")
public class RedisService implements BaseService<JedisPool> {

    @Resource
    private ISystemConfigService systemConfigService;
    private JedisPool jedisPool;
    private final Logger log = LoggerFactory.getLogger(RedisService.class);

    public void setJedis(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public JedisPool instance() {
        try {
            if (this.jedisPool != null) return this.jedisPool;

            SystemConfig systemConfigHost = systemConfigService.selectByKey("redis_host");
            String host = systemConfigHost.getValue();

            SystemConfig systemConfigPort = systemConfigService.selectByKey("redis_port");
            String port = systemConfigPort.getValue();
            SystemConfig systemConfigPassword = systemConfigService.selectByKey("redis_password");
            String password = systemConfigPassword.getValue();
            password = StringUtils.isEmpty(password) ? null : password;
            SystemConfig systemConfigDatabase = systemConfigService.selectByKey("redis_database");
            String database = systemConfigDatabase.getValue();
            SystemConfig systemConfigTimeout = systemConfigService.selectByKey("redis_timeout");
            String timeout = systemConfigTimeout.getValue();

            if (!this.isRedisConfig()) {
                log.info("error");
                return null;
            }
            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            jedisPoolConfig.setMaxIdle(7);
            jedisPoolConfig.setMaxTotal(20);
            jedisPoolConfig.setTestOnBorrow(true);
            jedisPoolConfig.setTestOnReturn(true);
            jedisPool = new JedisPool(jedisPoolConfig, host, Integer.parseInt(port), Integer.parseInt(timeout), password,
                    Integer.parseInt(database));
            log.info("Success");
            return this.jedisPool;
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return null;
        }
    }


    public boolean isRedisConfig() {
        SystemConfig systemConfigHost = systemConfigService.selectByKey("redis_host");
        String host = systemConfigHost.getValue();
        // port
        SystemConfig systemConfigPort = systemConfigService.selectByKey("redis_port");
        String port = systemConfigPort.getValue();
        // database
        SystemConfig systemConfigDatabase = systemConfigService.selectByKey("redis_database");
        String database = systemConfigDatabase.getValue();
        // timeout
        SystemConfig systemConfigTimeout = systemConfigService.selectByKey("redis_timeout");
        String timeout = systemConfigTimeout.getValue();

        return !StringUtils.isEmpty(host) && !StringUtils.isEmpty(port) && !StringUtils.isEmpty(database) && !StringUtils
                .isEmpty(timeout);
    }

    // 获取String值
    public String getString(String key) {
        JedisPool instance = this.instance();
        if (StringUtils.isEmpty(key) || instance == null) return null;
        Jedis jedis = instance.getResource();
        String value = jedis.get(key);
        jedis.close();
        return value;
    }

    public void setString(String key, String value) {
        this.setString(key, value, 300); // 如果不指定过时时间，默认为5分钟
    }

    /**
     *
     *
     * @param key
     * @param value
     * @param expireTime
     */
    public void setString(String key, String value, int expireTime) {
        JedisPool instance = this.instance();
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value) || instance == null) return;
        Jedis jedis = instance.getResource();
        SetParams params = new SetParams();
        params.px(expireTime * 1000);
        jedis.set(key, value, params);
        jedis.close();
    }

    public void delString(String key) {
        JedisPool instance = this.instance();
        if (StringUtils.isEmpty(key) || instance == null) return;
        Jedis jedis = instance.getResource();
        jedis.del(key);
        jedis.close();
    }



}
