package org.tinygame.hero_story.model;

import org.tinygame.hero_story.login.db.UserEntity;

public class User {
    /**
     * 用户 ID
     * */
    private int userId;

    /**
     * 用户名称
     * */
    private String userName;

    /**
     * 用户形象
     * */
    private String heroAvatar;

    /**
     * 用户血量
     * */
    private int currHp;

    /**
     * 死亡状态
     * */
    private boolean died = false;
    /**
     * 移动状态
     * */
    public final MoveState moveState = new MoveState();

    public User(){}

    public User(int userId, String heroAvatar) {
        this.userId = userId;
        this.heroAvatar = heroAvatar;
        this.currHp = 100;
    }

    public User(UserEntity userEntity){
        this(userEntity.userId, userEntity.heroAvatar,100);
        this.userName = userEntity.userName;
    }

    public User(int userId, String heroAvatar,int currHp) {
        this(userId,heroAvatar);
        this.currHp = currHp;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getHeroAvatar() {
        return heroAvatar;
    }

    public void setHeroAvatar(String heroAvatar) {
        this.heroAvatar = heroAvatar;
    }

    public int getCurrHp() {
        return currHp;
    }

    public void setCurrHp(int currHp) {
        this.currHp = currHp;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isDied() {
        return died;
    }

    public void setDied(boolean died) {
        this.died = died;
    }
}
