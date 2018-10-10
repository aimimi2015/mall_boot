package com.mmall.common;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by ${aimimi2015} on 2017/6/9.
 * 二期已废弃
 */
@Slf4j
public class TokenCache {

//    private static Logger logger = LoggerFactory.getLogger(TokenCache.class);
    public static final String TOKEN_PREFIX="token_";

    //LRU算法 最少使用算法
    //本地缓存,最大10000,初始化1000,最多存放12小时
    private static LoadingCache<String, String> localCache = CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000).expireAfterAccess(12,TimeUnit.HOURS).build(
            new CacheLoader<String, String>() {

        //默认的数据加载实现,当调用get取值是,如果key没有对应的值,就调用这个方法加载
        @Override
        public String load(String s) throws Exception {
            return "null";
        }
    });

    public static void setKey(String key, String value) {
        localCache.put(key, value);
    }

    public static String getKey(String key) {
        String value = "null";
        try {
            value = localCache.get(key);
            if ("null".equals(value)) {
                return null;
            }
            return value;

        } catch (Exception e) {
            log.error("localCache get error", e);
        }

        return null;

    }
}
