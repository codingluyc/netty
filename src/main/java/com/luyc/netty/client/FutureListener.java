package com.luyc.netty.client;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author luyc
 * @since 2023/2/2 15:12
 */
public class FutureListener implements GenericFutureListener {
    private static Logger log = LoggerFactory.getLogger(FutureListener.class);

    @Override
    public void operationComplete(Future future) throws Exception {
        log.info("tcp result:{}",future.isSuccess());
        if(!future.isSuccess()){
            log.error("",future.cause());
        }
    }
}
