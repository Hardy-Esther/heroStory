package org.tinygame.hero_story;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.hero_story.cmdHandle.CmdHandlerFactory;
import org.tinygame.hero_story.mq.MQProducer;
import org.tinygame.hero_story.util.RedisUtil;

public class ServerMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerMain.class);

    public static void main(String[] args) {

        //初始化数据
        GameMsgRecognizer.init();
        CmdHandlerFactory.init();
        MySqlSessionFactory.init();
        RedisUtil.init();
        MQProducer.init();

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();

        b.group(bossGroup, workerGroup);
        b.channel(NioServerSocketChannel.class);
        b.childHandler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(
                        new HttpServerCodec(),//http服务器编解码器
                        new HttpObjectAggregator(65535),//内容限制长度
                        new WebSocketServerProtocolHandler("/websocket"),//WebSocker 协议处理器，在这里处理握手，ping,pong等消息
                        new GameMsgDecoder(),//自定义的消息解码器
                        new GameMsgEncoder(),//自定义的消息编码器
                        new GameMsgHandler() //自定义的消息处理器
                );
            }
        });
        try {
            ChannelFuture f = b.bind(12345).sync();
            if (f.isSuccess()) {
                LOGGER.info("服务器启动成功");
            }
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
