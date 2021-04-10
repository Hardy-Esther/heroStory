package org.tinygame.hero_story.mq;

import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MQProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MQProducer.class);

    /**
     * 生产者
     */
    private static DefaultMQProducer _producer = null;

    private MQProducer() {
    }

    /**
     * 初始化
     * */
    public static void init() {

        try {
            DefaultMQProducer producer = new DefaultMQProducer("heroStory");
            producer.setNamesrvAddr("192.168.76.100:9876");

            producer.start();
            producer.setRetryTimesWhenSendAsyncFailed(3);
            _producer = producer;
        } catch (MQClientException e) {
            LOGGER.error(e.getMessage(), e);
        }


    }

    /**
     * 发送消息
     * @param topic 主题
     * @param msg 消息对象
     * */
    public static void sendMsg(String topic, Object msg) {
        if(topic == null || msg == null){
            return;
        }

        if(_producer == null){
            throw new RuntimeException("_producer 尚未初始化");
        }
        Message mqMsg = new Message();
        mqMsg.setTopic(topic);
        mqMsg.setBody(JSONObject.toJSONBytes(msg));

        try{
            _producer.send(mqMsg);
        }catch (Exception e){
            LOGGER.error(e.getMessage(),e);
        }

    }

}
