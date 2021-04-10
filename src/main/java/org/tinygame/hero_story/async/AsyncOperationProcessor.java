package org.tinygame.hero_story.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.hero_story.MainThreadProcessor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class AsyncOperationProcessor {

    /**
     * 日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncOperationProcessor.class);


    private static final AsyncOperationProcessor _instance = new AsyncOperationProcessor();

    private final ExecutorService[] _esArray = new ExecutorService[8];
    /**
     * 私有化类默认构造器
     * */
    private AsyncOperationProcessor(){
        for (int i = 0; i < _esArray.length; i++) {
            //线程名称
            final String threadName = "AsyncOperationProcessor_" + i;
            //创建一个单线程
            _esArray[i] = Executors.newSingleThreadExecutor((newRunnable)->{
               Thread newThread = new Thread(newRunnable);
               newThread.setName(threadName);
               return newThread;
            });
        }
    }


    public static AsyncOperationProcessor getInstance(){
        return _instance;
    }

    /**
     * 处理异步操作
     * @param asyncOp 异步操作
     * */
    public void process(IAsyncOperation asyncOp){
        if(asyncOp == null){
            return;
        }
        //根据bindId获取线程索引
        int bindId = Math.abs(asyncOp.bindId());
        int esIndex = bindId%_esArray.length;

        _esArray[esIndex].submit(()->{
            try{
                //执行异步操作
                asyncOp.doAsync();
                //返回主线程执行完成逻辑
                MainThreadProcessor.getInstance().process(asyncOp::doFinish);
            }catch (Exception ex){
                LOGGER.error(ex.getMessage(),ex);
            }
        });
    }

}
