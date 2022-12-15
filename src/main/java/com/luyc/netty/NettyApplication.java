package com.luyc.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

@SpringBootApplication
public class NettyApplication {
    private static final Logger log = LoggerFactory.getLogger(NettyApplication.class);
    private static ScheduledThreadPoolExecutor executor ;

    public static void main(String[] args) {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("manualTask-pool-%d").build();
        executor = new ScheduledThreadPoolExecutor(2,namedThreadFactory);
        executor.setRemoveOnCancelPolicy(true);
        ConfigurableApplicationContext context = SpringApplication.run(NettyApplication.class, args);
        MyServer server = context.getBean(MyServer.class);
        server.setPort(4567);
        executor.execute(server);
        log.info("server start");

    }

}
