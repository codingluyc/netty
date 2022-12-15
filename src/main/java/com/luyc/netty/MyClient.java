package com.luyc.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * @author luyc
 * @since 2022/12/15 9:31
 */
public class MyClient {
    private static final Logger log = LoggerFactory.getLogger(ClientInboundHandler.class);
    public static void main(String[] args) {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ClientInboundHandler());
                }
            });

            // Start the client.
            ChannelFuture f = b.connect("localhost", 4567).sync();
            NioSocketChannel channel = (NioSocketChannel) f.channel();

            if(channel.isWritable()) {
                byte[] bytes = "hi this is clent".getBytes(StandardCharsets.UTF_8);
                ByteBuf byteBuf = Unpooled.buffer(bytes.length);
                byteBuf = byteBuf.writeBytes(bytes);
//                channel.writeAndFlush(byteBuf);
                channel.write(byteBuf);
                log.info("send msg");
                ChannelOutboundBuffer buffer = channel.unsafe().outboundBuffer();
            }
            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
