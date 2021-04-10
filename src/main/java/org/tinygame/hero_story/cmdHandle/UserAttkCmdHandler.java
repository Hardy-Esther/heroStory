package org.tinygame.hero_story.cmdHandle;


import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.tinygame.hero_story.Broadcaster;
import org.tinygame.hero_story.model.User;
import org.tinygame.hero_story.model.UserManager;
import org.tinygame.hero_story.mq.MQProducer;
import org.tinygame.hero_story.mq.VictorMsg;
import org.tinygame.hero_story.msg.GameMsgProtocol;

public class UserAttkCmdHandler implements ICmdHandler<GameMsgProtocol.UserAttkCmd> {
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserAttkCmd cmd) {
        if (ctx == null || cmd == null) {
            return;
        }

        // 获取攻击者 Id
        Integer attkUserId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if (attkUserId == null) {
            return;
        }

        // 获取被攻击者 Id
        int targetUserId = cmd.getTargetUserId();

        GameMsgProtocol.UserAttkResult.Builder resultBuilder = GameMsgProtocol.UserAttkResult.newBuilder();
        resultBuilder.setAttkUserId(attkUserId);
        resultBuilder.setTargetUserId(targetUserId);

        GameMsgProtocol.UserAttkResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);

        User targetUser = UserManager.getUserById(targetUserId);
        if (targetUser == null) {
            return;
        }

        int subtractHp = 10;
        targetUser.setCurrHp(targetUser.getCurrHp() - subtractHp);

        Broadcaster.userSubtractHp(targetUserId,subtractHp);

        if(targetUser.getCurrHp() <= 0){
            //广播死亡消息
            Broadcaster.userDie(targetUserId);

            if(!targetUser.isDied()){
                //把用户死亡状态改成true
                targetUser.setDied(true);

                VictorMsg mqMsg = new VictorMsg();
                mqMsg.winnerId = attkUserId;
                mqMsg.loserId = targetUserId;
                MQProducer.sendMsg("Victor",mqMsg);
            }

        }

    }

}
