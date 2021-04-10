package org.tinygame.hero_story;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.hero_story.cmdHandle.CmdHandlerFactory;
import org.tinygame.hero_story.cmdHandle.ICmdHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class MainThreadProcessor {

    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(MainThreadProcessor.class);

    /**
     * 单例对象
     * */
    private static final MainThreadProcessor _instance = new MainThreadProcessor();

    /**
     * 创建一个单线程
     */
    private final ExecutorService _es = Executors.newSingleThreadExecutor((r)->{
        Thread newThread = new Thread(r);
        newThread.setName("MainThreadProcessor");
        return newThread;
    });

    private MainThreadProcessor() {

    }

    public static MainThreadProcessor getInstance() {
        return _instance;
    }

    /**
     * 处理消息
     *
     * @param ctx 客户端信道上下文
     * @param msg 信息对象
     */
    public void process(ChannelHandlerContext ctx, GeneratedMessageV3 msg) {
        if(msg == null || ctx == null){
            return;
        }
        this._es.submit(()->{
            // 获取消息类
            Class<?> msgClazz = msg.getClass();

            // 获取指令处理器
            ICmdHandler<? extends GeneratedMessageV3>
                    cmdHandler = CmdHandlerFactory.create(msgClazz);

            if (null == cmdHandler) {
                LOGGER.error(
                        "未找到相对应的指令处理器, msgClazz = {}",
                        msgClazz.getName()
                );
                return;
            }
            try {
                // 处理指令
                cmdHandler.handle(ctx, cast(msg));
            }catch (Exception ex){
                LOGGER.error(ex.getMessage(),ex);
            }
        });

    }
    /**
     * 处理消息
     * @param r Runnable实例
     * */
    public void process(Runnable r){
        if(r != null){
            _es.submit(r);
        }
    }

    /**
     * 转型消息对象
     * （欺骗编译器）
     * */
    private static <TCmd extends GeneratedMessageV3> TCmd cast(Object msg){
        if(msg == null || !(msg instanceof GeneratedMessageV3)){
            return null;
        }
        return (TCmd) msg;
    }

}
