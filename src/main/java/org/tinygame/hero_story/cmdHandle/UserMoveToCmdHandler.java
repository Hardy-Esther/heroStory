package org.tinygame.hero_story.cmdHandle;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.tinygame.hero_story.Broadcaster;
import org.tinygame.hero_story.model.MoveState;
import org.tinygame.hero_story.model.User;
import org.tinygame.hero_story.model.UserManager;
import org.tinygame.hero_story.msg.GameMsgProtocol;

public class UserMoveToCmdHandler implements ICmdHandler<GameMsgProtocol.UserMoveToCmd> {
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserMoveToCmd msg) {
        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if (userId == null){
            return;
        }
        //获取移动用户
        User moveUser = UserManager.getUserById(userId);
        if(moveUser == null){
            return;
        }
        MoveState mvState = moveUser.moveState;
        mvState.fromPosX = msg.getMoveFromPosX();
        mvState.fromPosY = msg.getMoveFromPosY();
        mvState.toPosX = msg.getMoveToPosX();
        mvState.toPosY = msg.getMoveToPosY();
        mvState.startTime = System.currentTimeMillis();

        GameMsgProtocol.UserMoveToResult.Builder resultBuilder = GameMsgProtocol.UserMoveToResult.newBuilder();
        resultBuilder.setMoveUserId(userId);
        resultBuilder.setMoveFromPosX(mvState.fromPosX);
        resultBuilder.setMoveFromPosY(mvState.fromPosY);
        resultBuilder.setMoveToPosX(mvState.toPosX);
        resultBuilder.setMoveToPosY(mvState.toPosY);
        resultBuilder.setMoveStartTime(mvState.startTime);
        GameMsgProtocol.UserMoveToResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);
    }
}
