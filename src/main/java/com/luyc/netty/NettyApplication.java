package com.luyc.netty;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

@SpringBootApplication
public class NettyApplication {
    private static final Logger log = LoggerFactory.getLogger(NettyApplication.class);
    private static ScheduledThreadPoolExecutor executor ;

    public static void main(String[] args) throws InterruptedException {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("manualTask-pool-%d").build();
        executor = new ScheduledThreadPoolExecutor(2,namedThreadFactory);
        executor.setRemoveOnCancelPolicy(true);
        ConfigurableApplicationContext context = SpringApplication.run(NettyApplication.class, args);
        //start server
        MyServer server = new MyServer(4567);
        executor.execute(server);

//        //start client
//        MyClient client = new MyClient("localhost",4567);
//        executor.execute(client);
//
//        Thread.currentThread().sleep(5000L);
//        client.writeAndFlush("hello world");
    }

}
