package com.luyc.netty;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.luyc.netty.client.MyClient;
import com.luyc.netty.server.MyServer;
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
//        // make sure that server thread has started
//        Thread.sleep(3000L);
//        for(int i =0;i<50000;i++) {
//            client.writeAndFlush("hello world "+i+"*******************************************************************************************************************************************\n");
//        }
//        log.info("send completed");
    }

}
