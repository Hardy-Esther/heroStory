package org.tinygame.hero_story;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import org.tinygame.hero_story.msg.GameMsgProtocol;
/**
 * 消息识别器
 */
public final class GameMsgRecognizer {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameMsgRecognizer.class);
    /**
     * 消息代码和消息体字典
     * */
    private static final Map<Integer, GeneratedMessageV3> _msgCodeAndMsgBodyMap = new HashMap<>();

    /**
     * 消息类型和消息代码字典
     * */
    private static final Map<Class<?>,Integer> _msgClazzAndMsgCodeMap = new HashMap<>();

    /**
     * 初始化数据
     * */
    public static void init() {
        //获取GameMsgProtocol类里的所有内部类
        Class<?>[] innerClazzArray = GameMsgProtocol.class.getDeclaredClasses();
        for(Class<?> innerClazz : innerClazzArray){
            if(!GeneratedMessageV3.class.isAssignableFrom(innerClazz)){
                continue;
            }
            String clazzName = innerClazz.getSimpleName();
            clazzName = clazzName.toLowerCase();
            for(GameMsgProtocol.MsgCode msgCode : GameMsgProtocol.MsgCode.values()){
                String strMsgCode = msgCode.name();
                strMsgCode = strMsgCode.replaceAll("_","");
                strMsgCode = strMsgCode.toLowerCase();
                if(!strMsgCode.startsWith(clazzName)){
                    continue;
                }
                try {
                    Object returnObj = innerClazz.getDeclaredMethod("getDefaultInstance").invoke(innerClazz);

                    LOGGER.info("{} <==> {}",innerClazz.getName(),msgCode.getNumber());

                    _msgCodeAndMsgBodyMap.put(
                            msgCode.getNumber(),
                            (GeneratedMessageV3) returnObj
                    );

                    _msgClazzAndMsgCodeMap.put(
                            innerClazz,
                            msgCode.getNumber()
                    );

                } catch (Exception e) {
                    LOGGER.error(e.getMessage(),e);
                }
            }
        }

    }

    private GameMsgRecognizer() {
    }

    public static Message.Builder getBuilderByMsgCode(int msgCode) {
        if(msgCode < 0){
            return null;
        }
        GeneratedMessageV3 msg = _msgCodeAndMsgBodyMap.get(msgCode);
        if (msg == null){
            return null;
        }
        return msg.newBuilderForType();
    }

    public static int getMsgCodeByMsgClazz(Class<?> msgClazz) {
        if(msgClazz == null){
            return -1;
        }
        Integer msgCode = _msgClazzAndMsgCodeMap.get(msgClazz);
        if(msgCode != null){
            return msgCode.intValue();
        }
        return -1;
    }
}
