package com.luyc.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 客户端入站信息处理器
 * @author luyc
 * @since 2022/12/15 9:33
 */
public class ClientInboundHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(ClientInboundHandler.class);

    private String temp = "";



    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.info("channelReadComplete");
        log.info("receive msg:{}",temp);
        temp = "";
        super.channelReadComplete(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("channelRead");
        ByteBuf in = (ByteBuf) msg;
        try {

            StringBuilder sb = new StringBuilder(temp);
            while (in.isReadable()) { // (1)
                sb.append((char) in.readByte());
            }
            log.info("receive msg:{}",sb.toString());

        } finally {
            ReferenceCountUtil.release(msg); // (2)
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("client handler error",cause);
        ctx.close();
    }
}
