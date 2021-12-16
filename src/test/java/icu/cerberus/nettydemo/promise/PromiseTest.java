package icu.cerberus.nettydemo.promise;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.*;

@Slf4j
public class PromiseTest {

    @Test
    void testFuture() throws ExecutionException, InterruptedException, TimeoutException {
        ExecutorService service = Executors.newFixedThreadPool(2);
        Future<Object> future = service.submit(() -> {
            log.info("开始线程");
            Thread.sleep(1000);
            return "hello world";
        });
        log.info("等待结果");
        System.out.println(System.currentTimeMillis());
        Object o = future.get(1500, TimeUnit.MILLISECONDS);
        System.out.println(System.currentTimeMillis());
        log.info("{}", o);
    }

    @Test
    void testNettyFuture() {
        NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
        io.netty.util.concurrent.Future<Object> future = eventExecutors.next().submit(() -> {
            Thread.sleep(1000);
            log.debug("执行线程方法");
            return "Hello World!";
        });
        System.out.println("等待结果");
        // 不管Future是否执行完，仍然执行
        // System.out.println(future.getNow());
        /*try {
            System.out.println(future.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }*/
        future.addListener(future1 -> {
            log.info("异步使用回调函数执行");
            System.out.println(future1.getNow());
        });
    }

    @Test
    void testNettyPromise() {
        EventLoop eventLoop = new NioEventLoopGroup().next();
        DefaultPromise<String> promise = new DefaultPromise<>(eventLoop);

        Executors.newFixedThreadPool(2).execute(() -> {
            log.debug("线程开始");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread());
            promise.setSuccess("Hello World");
        });

        log.debug("等待执行");
        promise.addListener(future -> {
            log.debug("线程中执行完毕");
            log.debug("{}", future.getNow());
            log.debug("结束");
        });

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
