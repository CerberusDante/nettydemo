package icu.cerberus.redis.redisson;

import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBucket;
import org.redisson.api.RKeys;
import org.redisson.api.RedissonClient;

@Slf4j
//@SpringBootTest
class RedissonConfigTest {
    @Test
    void RedissonClusterTest() {
        RedissonClient client = RedissonConfig.config();

        RBucket<Object> bucket = client.getBucket("city");
//        bucket.set("beijing");
        Object o = bucket.get();
        if (o != null) {
            System.out.println(o.getClass());
            System.out.println(o);
        }
        RKeys keys = client.getKeys(); //获取所有key值
        System.out.println("count: " + keys.count());
        log.info("random key {}", keys.randomKey());
        System.out.println();
        keys.getKeys().forEach(System.out::println);
        System.out.println("*******************");
        Consumer<String> getBucket = (String key) -> {
            Object v = client.getBucket(key).get();
//            log.info("key: {}, value: {}", key, v);
            System.out.println("key: " + key + ",  value: " + v);
        };
        keys.getKeys().forEach(getBucket);
        Object city = client.getBucket("hello").get();
        System.out.println(city);
        keys.flushall();
        log.error("##############");
        keys = client.getKeys();
        keys.getKeys().forEach(getBucket);
    }

    @Test
    void logbackTest() {
        System.out.println(System.getProperty("user.dir"));
        log.info("#############");
    }
}