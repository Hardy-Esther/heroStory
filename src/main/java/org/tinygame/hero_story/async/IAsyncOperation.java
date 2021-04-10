package org.tinygame.hero_story.async;

/**
 * 异步操作接口
 */
public interface IAsyncOperation {
    /**
     * 执行异步操作
     */
    void doAsync();

    /**
     * 执行完成逻辑
     */
    default void doFinish() {

    }
    /**
     * 获取绑定ID
     * @return 绑定ID
     * */
    default int bindId(){
        return 0;
    }
}
