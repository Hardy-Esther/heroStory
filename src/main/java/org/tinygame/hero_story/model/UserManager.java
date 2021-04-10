package org.tinygame.hero_story.model;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户管理器
 */
public final class UserManager {
    /**
     * 用户字典
     */
    private static final Map<Integer, User> _userMap = new ConcurrentHashMap<>();

    /**
     * 私有化
     */
    private UserManager() {
    }

    public static void addUser(User user) {
        if (user != null) {
            _userMap.put(user.getUserId(), user);
        }
    }

    public static void removeUserById(int userId) {
        _userMap.remove(userId);
    }
    /**
     * 用户列表
     * */
    public static Collection<User> listUser(){
        return _userMap.values();
    }

    public static User getUserById(int userId){
        return _userMap.get(userId);
    }
}
