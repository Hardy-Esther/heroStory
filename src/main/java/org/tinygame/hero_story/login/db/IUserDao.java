package org.tinygame.hero_story.login.db;

import org.apache.ibatis.annotations.Param;

public interface IUserDao {
    /**
     * 根据用户名称获取用户
     * @param userName 用户名称
     * */
    public UserEntity getUserByName(@Param("userName") String userName);

    /**
     * 添加用户实体
     * */
    public void insertInto(UserEntity newUserEntity);
}
