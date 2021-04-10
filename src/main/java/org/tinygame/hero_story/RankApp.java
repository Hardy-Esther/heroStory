package org.tinygame.hero_story;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.hero_story.mq.MQConsumer;
import org.tinygame.hero_story.util.RedisUtil;

/**
 * 排行榜应用程序
 * */
public class RankApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(RankApp.class);

    public static void main(String[] args) {
        //初始化
        RedisUtil.init();
        MQConsumer.init();

        LOGGER.info("排行榜应用程序启动成功");
    }
}
