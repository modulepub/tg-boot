package pub.module.cache.biz.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import pub.module.cache.api.service.BizCacheService;

import jakarta.annotation.Resource;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis 缓存服务实现类
 */
@Slf4j
@Service("redisCacheService")
public class RedisCacheServiceImpl implements BizCacheService {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    // ===============================  String 类型操作  ===============================

    @Override
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void set(String key, String value, long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    public Set<String> keys() {
        return redisTemplate.keys("*");
    }

    public Set<String> keys(String prefix) {
        return redisTemplate.keys(prefix+"*");
    }

    @Override
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public void delete(String... keys) {
        for (String key : keys) {
            redisTemplate.delete(key);
        }
    }


    @Override
    public void hSet(String key, String field, String value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    @Override
    public String hGet(String key, String field) {
        return (String) redisTemplate.opsForHash().get(key, field);
    }



    @Override
    public void hDelete(String key, String... fields) {
        redisTemplate.opsForHash().delete(key, (Object[]) fields);
    }

    // ===============================  List 类型操作  ===============================

    @Override
    public void lPush(String key, String... values) {
        redisTemplate.opsForList().leftPushAll(key, values);
    }

    @Override
    public String lPop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    @Override
    public List<String> lRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    // ===============================  Set 类型操作  ===============================

    @Override
    public void sAdd(String key, String... members) {
        redisTemplate.opsForSet().add(key, members);
    }

    @Override
    public Set<String> sMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    @Override
    public boolean sIsMember(String key, String member) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, member));
    }

    // ===============================  通用操作  ===============================

    @Override
    public void expire(String key, long timeout, TimeUnit timeUnit) {
        redisTemplate.expire(key, timeout, timeUnit);
    }

    @Override
    public boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }



}