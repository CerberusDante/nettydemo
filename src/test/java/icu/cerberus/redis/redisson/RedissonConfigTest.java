package icu.cerberus.redis.redisson;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBucket;
import org.redisson.api.RKeys;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
//@SpringBootTest
class RedissonConfigTest {
    @Test
    void RedissonClusterTest() {
        RedissonClient client = RedissonConfig.config();

        RBucket<Object> bucket = client.getBucket("city");
        bucket.set("beijing");
        Object o = bucket.get();
        System.out.println(o.getClass());
        System.out.println(o);
        RKeys keys = client.getKeys(); //获取所有key值
        System.out.println("count: " + keys.count());
        log.info("random key {}", keys.randomKey());
        System.out.println();
        keys.getKeys().forEach(System.out::println);
//        for (String key :
//                keys.getKeys()) {
//            log.info("{}", client.getBucket("key").get());
//        }
        Object city = client.getBucket("hello").get();
        System.out.println(city);
//        Iterable<String> allKeys = keys.getKeys();
//        Iterable<String> foundedKeys = keys.getKeysByPattern("key");//获取所有模糊key值
//        System.out.println("allKeys" + allKeys);
//        System.out.println("allKeys" + foundedKeys);
    }
}