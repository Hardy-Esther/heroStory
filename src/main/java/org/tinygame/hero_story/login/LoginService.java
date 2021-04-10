package org.tinygame.hero_story.login;

import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.hero_story.MySqlSessionFactory;
import org.tinygame.hero_story.async.AsyncOperationProcessor;
import org.tinygame.hero_story.async.IAsyncOperation;
import org.tinygame.hero_story.login.db.IUserDao;
import org.tinygame.hero_story.login.db.UserEntity;
import org.tinygame.hero_story.util.RedisUtil;
import redis.clients.jedis.Jedis;

import java.util.function.Function;

public final class LoginService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);

    private static final LoginService _instance = new LoginService();

    /**
     * 私有化类默认构造器
     */
    private LoginService() {

    }

    /**
     * 获取单例对象
     *
     * @return 单例对象
     */
    public static LoginService getInstance() {
        return _instance;
    }

    /**
     * 用户登陆处理
     *
     * @param userName 用户名称
     * @param password 用户密码
     * @param callback 回调函数
     * @return 用户实体
     */
    public void userLogin(String userName, String password, Function<UserEntity, Void> callback) {
        if (userName == null || password == null) {
            return;
        }

        IAsyncOperation asyncOp = new AsyncGetUserByName(userName, password) {
            @Override
            public void doFinish() {
                callback.apply(this.getUserEntity());
            }
        };
        //执行异步操作
        AsyncOperationProcessor.getInstance().process(asyncOp);

    }

    /**
     * 更新 Redis 用户基本信息
     * */
    public void updateUserBasicInfoRedis(UserEntity userEntity){
        if(userEntity == null){
            return;
        }
        try(Jedis redis = RedisUtil.getRedis()){
            //获取用户Id
            int userId = userEntity.userId;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId",userId);
            jsonObject.put("userName",userEntity.userName);
            jsonObject.put("heroAvatar",userEntity.heroAvatar);

            //更新Redis数据
            redis.hset("User_"+userId,"BasicInfo",jsonObject.toJSONString());
        }
    }

    /**
     * 异步方式获取用户
     */
    private class AsyncGetUserByName implements IAsyncOperation {
        private final String _userName;
        private final String _password;

        private UserEntity _userEntity = null;

        AsyncGetUserByName(String userName, String password) {
            this._userName = userName;
            this._password = password;
        }

        /**
         * 获取用户实体
         */
        public UserEntity getUserEntity() {
            return _userEntity;
        }

        @Override
        public int bindId(){
            return _userName.charAt(_userName.length()-1);
        }

        @Override
        public void doAsync() {
            try (SqlSession mySqlSession = MySqlSessionFactory.openSession()) {
                //获取dao
                IUserDao dao = mySqlSession.getMapper(IUserDao.class);

                //看看当前线程
                LOGGER.info("当前线程 = {}", Thread.currentThread().getName());

                UserEntity userEntity = dao.getUserByName(_userName);
                if (userEntity != null) {
                    if (!_password.equals(userEntity.password)) {
                        LOGGER.error("用户密码错误，userName = {}", _userName);
                    }
                } else {
                    userEntity = new UserEntity();
                    userEntity.userName = _userName;
                    userEntity.password = _password;
                    userEntity.heroAvatar = "Hero_Shaman";

                    //将用户实体插入数据库
                    dao.insertInto(userEntity);
                }
                this._userEntity = userEntity;

                LoginService.getInstance().updateUserBasicInfoRedis(userEntity);
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }

}
