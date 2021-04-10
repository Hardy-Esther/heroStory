import org.tinygame.hero_story.util.RedisUtil;
import redis.clients.jedis.Jedis;

public class RedisTest {
    public static void main(String[] args) {
        RedisUtil.init();
        Jedis redis = RedisUtil.getRedis();
        redis.hset("User_0","BasicInfo","{userId:1}");
        System.out.println(redis);
    }
}
