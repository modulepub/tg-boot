package pub.module.cache.api.service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 缓存服务接口，模仿Redis的增删改查方法
 */
public interface BizCacheService {

    // ===============================  String 类型操作  ===============================
    
    /**
     * 设置指定 key 的值
     * @param key 键
     * @param value 值
     */
    void set(String key, String value);

    Set<String> keys();

    Set<String> keys(String prefix);

    /**
     * 设置指定 key 的值，并将 key 的过期时间设为 timeout
     * @param key 键
     * @param value 值
     * @param timeout 过期时间
     * @param timeUnit 时间单位
     */
    void set(String key, String value, long timeout, TimeUnit timeUnit);

    /**
     * 获取指定 key 的值
     * @param key 键
     * @return 值
     */
    String get(String key);

    /**
     * 删除指定 key
     * @param key 键
     */
    void delete(String key);

    /**
     * 批量删除指定 key
     * @param keys 键列表
     */
    void delete(String... keys);

    // ===============================  Hash 类型操作  ===============================
    
    /**
     * 将哈希表 key 中的字段 field 的值设为 value
     * @param key 键
     * @param field 字段
     * @param value 值
     */
    void hSet(String key, String field, String value);

    /**
     * 获取存储在哈希表中指定字段的值
     * @param key 键
     * @param field 字段
     * @return 值
     */
    String hGet(String key, String field);


    /**
     * 删除哈希表 key 中的一个或多个指定字段
     * @param key 键
     * @param fields 字段列表
     */
    void hDelete(String key, String... fields);

    // ===============================  List 类型操作  ===============================
    
    /**
     * 将一个或多个值插入到列表头部
     * @param key 键
     * @param values 值列表
     */
    void lPush(String key, String... values);

    /**
     * 移除并返回列表的第一个元素
     * @param key 键
     * @return 第一个元素
     */
    String lPop(String key);

    /**
     * 获取列表指定范围内的元素
     * @param key 键
     * @param start 开始索引
     * @param end 结束索引
     * @return 元素列表
     */
    List<String> lRange(String key, long start, long end);

    // ===============================  Set 类型操作  ===============================
    
    /**
     * 向集合添加一个或多个成员
     * @param key 键
     * @param members 成员列表
     */
    void sAdd(String key, String... members);

    /**
     * 获取集合中的所有成员
     * @param key 键
     * @return 成员集合
     */
    Set<String> sMembers(String key);

    /**
     * 判断 member 元素是否是集合 key 的成员
     * @param key 键
     * @param member 成员
     * @return 是否是成员
     */
    boolean sIsMember(String key, String member);

    // ===============================  通用操作  ===============================
    
    /**
     * 设置 key 的过期时间
     * @param key 键
     * @param timeout 过期时间
     * @param timeUnit 时间单位
     */
    void expire(String key, long timeout, TimeUnit timeUnit);

    /**
     * 检查给定 key 是否存在
     * @param key 键
     * @return 是否存在
     */
    boolean exists(String key);



}