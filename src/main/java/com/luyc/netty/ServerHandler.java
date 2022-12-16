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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(ServerHandler.class);


    private volatile ConcurrentHashMap<String,String> tempMap = new ConcurrentHashMap<>();


    private ReentrantLock lock = new ReentrantLock();

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
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        String temp = tempMap.get(ctx.channel().id().toString());
        log.info("received msg:{}",temp);
        tempMap.put(ctx.channel().id().toString(),"");
        String[] strs = temp.split("\n");
        super.channelReadComplete(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String str = (String) msg;
        String temp = tempMap.get(ctx.channel().id().toString());
        if(temp == null){
            temp = "";
        }
        StringBuilder sb = new StringBuilder(temp);
        sb.append(str);
        temp = sb.toString();
        tempMap.put(ctx.channel().id().toString(),temp);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("server handler error",cause);
        ctx.close();
    }

    public static void main(String[] args) {
    }
}
