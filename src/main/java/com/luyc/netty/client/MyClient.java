package com.luyc.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author luyc
 * @since 2022/12/15 9:31
 */
public class MyClient implements Runnable{
    private static final Logger log = LoggerFactory.getLogger(MyClient.class);

    // 服务端地址
    private String serverHost;

    //服务端端口
    private Integer serverPort;

    //工作组 用于接收
    private EventLoopGroup workerGroup;

    //通道
    private Channel channel;


    public MyClient(String serverHost, Integer serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.workerGroup = new NioEventLoopGroup();
    }

    /**
     * @author luyc
     * @Description 启动client
     * @Date 2022/12/15 15:55
     * @param
     * @return void
     **/
    @Override
    public void run() {
        try {
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
                    ch.pipeline().addLast(new StringDecoder());
                    ch.pipeline().addLast(new StringEncoder());
                    ch.pipeline().addLast(new ClientInboundHandler());
                }
            });
            ChannelFuture f = b.connect(this.serverHost, this.serverPort).sync();
            this.channel = f.channel();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * @author luyc
     * @Description 发送数据
     * @Date 2022/12/15 15:55
     * @param msg
     * @return void
     **/
    public void writeAndFlush(String msg){
        if(this.channel == null){
            log.error("socket has not connected");
            return;
        }
        if(channel.isWritable()) {
//            byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
//            ByteBuf byteBuf = Unpooled.buffer(bytes.length);
//            byteBuf = byteBuf.writeBytes(bytes);
//            channel.writeAndFlush(byteBuf);
            ChannelFuture channelFuture = channel.writeAndFlush(msg);
            channelFuture.addListener(new FutureListener());

            log.info("send msg:{}",msg);
        }else{
            log.error("channel is not writable, msg={}",msg);
        }
    }
}
