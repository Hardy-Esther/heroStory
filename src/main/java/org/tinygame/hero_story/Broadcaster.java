package org.tinygame.hero_story;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.tinygame.hero_story.msg.GameMsgProtocol;

public final class Broadcaster {
    /**
     * 客户端通过数组，一定要使用static，否则无法实现群发
     */
    private static final ChannelGroup _channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 私有化类默认构造器
     */
    private Broadcaster() {
    }
    /**
     * 增加通道
     * @param channel 通道
     * */
    public static void addChannel(Channel channel) {
        _channelGroup.add(channel);
    }
    /**
     * 删除通道
     * @param channel 通道
     * */
    public static void removeChannel(Channel channel) {
        _channelGroup.remove(channel);
    }
    /**
     * 广播消息
     * */
    public static void broadcast(Object msg) {
        if (msg == null) {
            return;
        }
        _channelGroup.writeAndFlush(msg);
    }
    /**
     * 广播减血消息
     * @param targetUserId 目标用户ID
     * @param subtractHp 减血量
     * */
    public static void userSubtractHp(int targetUserId,int subtractHp){
        if(targetUserId <= 0 || subtractHp <= 0){
            return;
        }
        GameMsgProtocol.UserSubtractHpResult.Builder resultBuilder = GameMsgProtocol.UserSubtractHpResult.newBuilder();
        resultBuilder.setTargetUserId(targetUserId);
        resultBuilder.setSubtractHp(subtractHp);

        GameMsgProtocol.UserSubtractHpResult newResult = resultBuilder.build();
        broadcast(newResult);
    }
    /**
     * 广播死亡消息
     * @param targetUserId 目标用户ID
     * */
    public static void userDie(int targetUserId){
        if(targetUserId <= 0){
            return;
        }
        GameMsgProtocol.UserDieResult.Builder resultBuilder = GameMsgProtocol.UserDieResult.newBuilder();
        resultBuilder.setTargetUserId(targetUserId);

        GameMsgProtocol.UserDieResult newResult = resultBuilder.build();
        broadcast(newResult);
    }
}
