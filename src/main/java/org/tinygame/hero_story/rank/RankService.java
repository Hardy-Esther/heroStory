package org.tinygame.hero_story.rank;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.hero_story.async.AsyncOperationProcessor;
import org.tinygame.hero_story.async.IAsyncOperation;
import org.tinygame.hero_story.util.RedisUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * 排行榜服务
 */
public final class RankService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RankService.class);

    /**
     * 单例对象
     */
    private static final RankService _instance = new RankService();

    /**
     * 私有化类默认构造器
     */
    private RankService() {

    }

    /**
     * 获取单例对象
     */
    public static RankService getInstance() {
        return _instance;
    }

    /**
     * 获取排名列表
     *
     * @param callBack 回调函数
     */
    public void getRank(Function<List<RankItem>, Void> callBack) {
        if (callBack == null) {
            return;
        }

        IAsyncOperation asyncOp = new AsyncGetRank() {
            @Override
            public void doFinish() {
                callBack.apply(this.getRankItemList());
            }
        };

        AsyncOperationProcessor.getInstance().process(asyncOp);

    }

    /**
     * 刷新打排行榜
     * @param winnerId 赢家 ID
     * @param loserId 输家 ID
     * */
    public void refreshRank(int winnerId, int loserId) {
        try(Jedis redis = RedisUtil.getRedis()){
            //增加用户输赢次数
            redis.hincrBy("User_"+winnerId,"Win",1);
            redis.hincrBy("User_"+loserId,"Lose",1);

            //看看赢家赢了多少次
            String winStr = redis.hget("User_"+winnerId,"Win");
            int winInt = Integer.parseInt(winStr);

            //修改排行榜
            redis.zadd("Rank",winInt,String.valueOf(winnerId));

        }
    }


    /**
     * 异步方式获取排名列表
     */
    private class AsyncGetRank implements IAsyncOperation {

        /**
         * 排名列表
         */
        private List<RankItem> _rankItemList = null;

        /**
         * 获取排名列表
         */
        public List<RankItem> getRankItemList() {
            return _rankItemList;
        }

        @Override
        public void doAsync() {
            try (Jedis redis = RedisUtil.getRedis()) {
                Set<Tuple> valSet = redis.zrevrangeWithScores("Rank", 0, 9);

                List<RankItem> rankItemList = new ArrayList<>();

                int rankId = 0;

                for (Tuple t : valSet) {
                    //获取用户Id
                    int userId = Integer.parseInt(t.getElement());
                    //获取用户的基本信息
                    String jsonStr = redis.hget("User_" + userId, "BasicInfo");
                    if (jsonStr == null || jsonStr.isEmpty()) {
                        continue;
                    }
                    JSONObject jsonObj = JSONObject.parseObject(jsonStr);

                    RankItem newItem = new RankItem();
                    newItem.rankId = ++rankId;
                    newItem.userId = userId;
                    newItem.userName = jsonObj.getString("userName");
                    newItem.heroAvatar = jsonObj.getString("heroAvatar");
                    newItem.win = (int) t.getScore();

                    rankItemList.add(newItem);
                }
                _rankItemList = rankItemList;
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }

}
