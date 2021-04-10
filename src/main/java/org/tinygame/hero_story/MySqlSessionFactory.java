package org.tinygame.hero_story;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;

/**
 * Mysql会话工厂
 * */
public final class MySqlSessionFactory {
    private static SqlSessionFactory _sqlSessionFactory;

    private MySqlSessionFactory(){}

    public static void init(){
        try {
            _sqlSessionFactory = (new SqlSessionFactoryBuilder())
                    .build(Resources.getResourceAsStream("MyBatisConfig.xml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static SqlSession openSession(){
        if(_sqlSessionFactory == null){
            throw new RuntimeException("_sqlSessionFactory 尚未初始化");
        }
        return _sqlSessionFactory.openSession(true);
    }
}
