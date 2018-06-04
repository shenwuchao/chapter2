package org.smart4j.chapter2.helper;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smart4j.chapter2.util.PropsUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/*
* 数据库操作助手类
* */
public class DatabaseHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseHelper.class);

    private static final QueryRunner QUERY_RUNNER = new QueryRunner();

    private static final ThreadLocal<Connection> CONNECTION_THREAD_LOCAL = new ThreadLocal<Connection>();
    private final static String DRIVER;
    private final static String URL;
    private final static String USER_NAME;
    private final static String PASS_WORD;

    static {
        Properties conf = PropsUtil.loadProps("config.properties");
        DRIVER = conf.getProperty("jdbc.driver");
        URL = conf.getProperty("jdbc.url");
        USER_NAME = conf.getProperty("jdbc.username");
        PASS_WORD = conf.getProperty("jdbc.password");
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            LOGGER.error("can not load jdbc driver ", e);
        }
    }

    /*
    * 获取数据库连接
    * */
    public static Connection getConnection () {
        Connection connection = CONNECTION_THREAD_LOCAL.get();
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL, USER_NAME, PASS_WORD);
                LOGGER.info("get connection success");
            } catch (SQLException e) {
                e.printStackTrace();
                LOGGER.error("get connection failure", e);
                throw new RuntimeException(e);
            } finally {
                CONNECTION_THREAD_LOCAL.set(connection);
            }
        }
        return connection;
    }
    /*
    * 关闭数据库连接（输入连接）
    * */
    public static void closeConnection (Connection connection) {

        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                LOGGER.error("close connection failure", e);
                throw new RuntimeException(e);
            }
        }
    }
    /*
     * 关闭数据库连接（当前线程）
     * */
    public static void closeConnection () {
        Connection connection = CONNECTION_THREAD_LOCAL.get();
        try {
            closeConnection(connection);
        } finally {
            CONNECTION_THREAD_LOCAL.remove();
        }
    }
    /*
    * 查询实体列表
    * */
    public static <T> List<T> queryEntityList
            (Class<T> entityClass, String sql, Object...parameters) {
        List<T> entityList = null;
        try {
            Connection connection = getConnection();
            entityList = QUERY_RUNNER.query(connection, sql, new BeanListHandler<T>(entityClass));
            LOGGER.info("query entity list success");
        } catch (SQLException e) {
            e.printStackTrace();
            LOGGER.error("query entity list failure", e);
            throw new RuntimeException(e);
        } finally {
            closeConnection();
        }
        return entityList;
    }
    /*
    * 查询实体
    * */
    public static <T> T queryEntity (Class<T> entityClass, String sql, Object...parameters) {
        T entity = null;
        try {
            Connection connection = getConnection();
            entity = QUERY_RUNNER.query(connection, sql, new BeanHandler<T>(entityClass), parameters);
        } catch (SQLException e) {
            e.printStackTrace();
            LOGGER.error("query entity failure", e);
            throw new RuntimeException(e);
        } finally {
            closeConnection();
        }
        return entity;
    }
    /*
    * 执行查询语句
    * */
    public static List<Map<String, Object>> executeQuery (String sql, Object...parameters) {
        List<Map<String, Object>> result = null;
        try {
            Connection connection = getConnection();
            result = QUERY_RUNNER.query(connection, sql, new MapListHandler(), parameters);
            LOGGER.info("query success");
        } catch (SQLException e) {
            e.printStackTrace();
            LOGGER.error("execute query failure", e);
            throw new RuntimeException(e);
        } finally {
            closeConnection();
        }
        return result;
    }
}
