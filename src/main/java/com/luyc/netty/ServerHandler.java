package com.luyc.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(ServerHandler.class);


    private MyServer server;

    public ServerHandler(MyServer server) {
        this.server = server;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.info("client register");
        Channel channel = ctx.channel();
        InetSocketAddress clientAddress = ((NioSocketChannel)channel).remoteAddress();
        String clientHost = clientAddress.getHostName();
        log.info("client host:{} port:{}",clientHost,clientAddress.getPort());
        super.channelRegistered(ctx);
    }


    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.info("client unregister");
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("client active");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("client inactive");
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        try {
            StringBuilder sb = new StringBuilder();
            while (in.isReadable()) { // (1)
                sb.append((char) in.readByte());
            }
            log.info("receive msg:{}",sb.toString());
        } finally {
            ReferenceCountUtil.release(msg); // (2)
        }
        ByteBuf byteBuf = Unpooled.buffer(256);
        byteBuf = byteBuf.writeBytes("hi this is server".getBytes(StandardCharsets.UTF_8));
        Channel channel = ctx.channel();
        ctx.writeAndFlush(byteBuf);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("server handler error",cause);
        ctx.close();
    }
}
