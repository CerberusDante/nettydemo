package icu.cerberus.redis.redisson;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.config.TransportMode;

public class RedissonConfig {
    public static RedissonClient config() {
        Config config = new Config();
        config.setTransportMode(TransportMode.NIO);
        config.setCodec(new JsonJacksonCodec());
        config.useClusterServers()
                .setScanInterval(2000) // 集群状态扫描间隔时间，单位是毫秒
                //可以用"rediss://"来启用SSL连接
                .addNodeAddress("redis://192.168.187.132:6379", "redis://192.168.187.132:6380")
                .addNodeAddress("redis://192.168.187.132:6381")
                .addNodeAddress("redis://192.168.187.133:6379", "redis://192.168.187.133:6380")
                .addNodeAddress("redis://192.168.187.133:6381");

        return Redisson.create(config);
    }
}
