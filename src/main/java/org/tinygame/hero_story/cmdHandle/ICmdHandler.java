package org.tinygame.hero_story.cmdHandle;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;
/**
 * 指令处理器接口
 * */
public interface ICmdHandler<TCmd extends GeneratedMessageV3> {
    /**
     * 处理指令
     * */
    public void handle(ChannelHandlerContext ctx,TCmd cmd);
}
