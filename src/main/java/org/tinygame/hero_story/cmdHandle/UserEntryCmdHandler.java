package org.tinygame.hero_story.cmdHandle;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.tinygame.hero_story.Broadcaster;
import org.tinygame.hero_story.model.User;
import org.tinygame.hero_story.model.UserManager;
import org.tinygame.hero_story.msg.GameMsgProtocol;

public class UserEntryCmdHandler implements ICmdHandler<GameMsgProtocol.UserEntryCmd> {
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserEntryCmd msg) {
        //从指令对象中获取用户ID和英雄形象
        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if(userId == null){
            return;
        }
        User existUser = UserManager.getUserById(userId);
        if(existUser == null){
            return;
        }
        String heroAvatar = existUser.getHeroAvatar();
        GameMsgProtocol.UserEntryResult.Builder resultBuilder = GameMsgProtocol.UserEntryResult.newBuilder();
        resultBuilder.setUserId(userId);
        resultBuilder.setHeroAvatar(heroAvatar);

        //构建结果并发送
        GameMsgProtocol.UserEntryResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);
    }
}
