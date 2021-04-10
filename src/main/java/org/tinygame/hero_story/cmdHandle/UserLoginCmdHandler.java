package org.tinygame.hero_story.cmdHandle;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.hero_story.login.LoginService;
import org.tinygame.hero_story.model.User;
import org.tinygame.hero_story.model.UserManager;
import org.tinygame.hero_story.msg.GameMsgProtocol;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserLoginCmdHandler implements ICmdHandler<GameMsgProtocol.UserLoginCmd> {
    /**
     * 日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserLoginCmdHandler.class);

    /**
     * 用户登陆状态字典, 防止用户连点登陆按钮
     */
    private static final Map<String, Long> USER_LOGIN_STATE_MAP = new ConcurrentHashMap<>();

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserLoginCmd cmd) {


        String userName = cmd.getUserName();
        String password = cmd.getPassword();

        LOGGER.info("userName = {},password = {}", userName, password);

        // 事先清理超时的登陆时间
        clearTimeoutLoginTime(USER_LOGIN_STATE_MAP);

        if (USER_LOGIN_STATE_MAP.containsKey(userName)) {
            // 如果正在处理登陆操作,
            return;
        }

        // 获取系统当前时间
        final long currTime = System.currentTimeMillis();
        // 设置用户登陆时间
        USER_LOGIN_STATE_MAP.putIfAbsent(
                userName, currTime
        );

        LoginService.getInstance().userLogin(userName, password,(userEntity)->{
            if (userEntity == null) {
                LOGGER.error("用户登陆失败，userName = {}", cmd.getUserName());
                return null;
            }

            int userId = userEntity.userId;
            //将用户加入字典
            User newUser = new User(userEntity);

            UserManager.addUser(newUser);

            //将用户ID附着到Channel
            ctx.channel().attr(AttributeKey.valueOf("userId")).set(userId);

            GameMsgProtocol.UserLoginResult.Builder
                    resultBuilder = GameMsgProtocol.UserLoginResult.newBuilder();
            resultBuilder.setUserId(newUser.getUserId());
            resultBuilder.setUserName(newUser.getUserName());
            resultBuilder.setHeroAvatar(newUser.getHeroAvatar());

            GameMsgProtocol.UserLoginResult newResult = resultBuilder.build();
            ctx.writeAndFlush(newResult);
            return null;
        });

    }

    /**
     * 清理超时的登陆时间
     *
     * @param userLoginTimeMap 用户登陆时间字典
     */
     private static void clearTimeoutLoginTime(Map<String, Long> userLoginTimeMap) {
        if (null == userLoginTimeMap ||
                userLoginTimeMap.isEmpty()) {
            return;
        }

        // 获取系统时间
        final long currTime = System.currentTimeMillis();
        // 获取迭代器
        Iterator<String> it = userLoginTimeMap.keySet().iterator();

        while (it.hasNext()) {
            // 根据用户名称获取登陆时间
            String userName = it.next();
            Long loginTime = userLoginTimeMap.get(userName);

            if (null == loginTime ||
                    currTime - loginTime > 5000) {
                // 如果已经超时,
                it.remove();
            }
        }
    }

}
