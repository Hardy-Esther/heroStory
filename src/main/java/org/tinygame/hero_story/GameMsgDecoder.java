package org.tinygame.hero_story;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameMsgDecoder extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameMsgDecoder.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (!(msg instanceof BinaryWebSocketFrame)) {
            LOGGER.error("无法识别消息类型");
            return;
        }
        //websocket 二进制消息会通过HttpServerCodec解码成BinaryWebSocketFrame类对象
        BinaryWebSocketFrame frame = (BinaryWebSocketFrame) msg;
        ByteBuf byteBuf = frame.content();


        byteBuf.readShort();//读取消息的长度
        int msgCode = byteBuf.readShort();//读取消息的编号

        //获取消息构建者
        Message.Builder msgBuilder = GameMsgRecognizer.getBuilderByMsgCode(msgCode);
        if (msgBuilder == null) {
            LOGGER.error("无法识别的消息编号 msgCode = {}", msgCode);
            return;
        }

        //拿到消息体
        byte[] msgBody = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(msgBody);

        msgBuilder.clear();
        msgBuilder.mergeFrom(msgBody);
        Message newMsg = msgBuilder.build();
        if (newMsg != null) {
            ctx.fireChannelRead(newMsg);
        }
    }
}
