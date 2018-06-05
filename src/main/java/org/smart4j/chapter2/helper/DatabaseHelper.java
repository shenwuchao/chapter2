package org.smart4j.chapter2.helper;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smart4j.chapter2.util.CollectionUtil;
import org.smart4j.chapter2.util.PropsUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/*
* 数据库操作助手类
* */
public class DatabaseHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseHelper.class);

    private static final QueryRunner QUERY_RUNNER = new QueryRunner();

    private static final BasicDataSource DATA_SOURCE;
    private static final ThreadLocal<Connection> CONNECTION_THREAD_LOCAL = new ThreadLocal<Connection>();

    static {
        Properties conf = PropsUtil.loadProps("config.properties");
        String driver = conf.getProperty("jdbc.driver");
        String url = conf.getProperty("jdbc.url");
        String userName = conf.getProperty("jdbc.username");
        String password = conf.getProperty("jdbc.password");
        DATA_SOURCE = new BasicDataSource();
        DATA_SOURCE.setDriverClassName(driver);
        DATA_SOURCE.setUrl(url);
        DATA_SOURCE.setUsername(userName);
        DATA_SOURCE.setPassword(password);
        /*
        * 另一种实例化数据源的方式是直接将dbcp2的配置文件导入
        *         Properties p = new Properties();
        *         InputStream inStream = Thread.currentThread()
                    .getContextClassLoader()..getResourceAsStream("/conf/jdbc.properties");
                  p.load(inStream);
        *         BasicDataSource bs = BasicDataSourceFactory.createDataSource(p);
        * */
    }

    /*
    * 获取数据库连接
    * */
    public static Connection getConnection () {
        Connection connection = CONNECTION_THREAD_LOCAL.get();
        if (connection == null) {
            try {
                connection = DATA_SOURCE.getConnection();
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
        }
        return result;
    }
    /*
    * 执行更新语句（包括update\insert\delete）
    * */
    public static int executeUpdate (String sql, Object...parameters) {
        int rows = 0;
        try {
            Connection connection = getConnection();
            rows = QUERY_RUNNER.update(connection, sql, parameters);

        } catch (SQLException e) {
            e.printStackTrace();
            LOGGER.error("execute update failure", e);
            throw new RuntimeException(e);
        }
        return rows;
    }
    /*
    * 批处理
    * */
    public static int[] executeBatch (String sql, Object[][] parameters) {
        int[] rows = null;
        try {
            Connection connection = getConnection();
            rows = QUERY_RUNNER.batch(connection, sql, parameters);
        } catch (SQLException e) {
            e.printStackTrace();
            LOGGER.error("execute batch failure", e);
            throw new RuntimeException(e);
        }
        return rows;
    }
    /*
    * 插入实体
    * */
    public static <T> boolean insertEntity (Class<T> entityClass, Map<String, Object> feildMap) {
        if (CollectionUtil.isEmpty(feildMap)) {
            LOGGER.error("can not insert entity:fieldMap is empty");
            return false;
        }
        String sql = "INSERT INTO " + getTableName(entityClass);
        StringBuilder columns = new StringBuilder("(");
        StringBuilder values = new StringBuilder("(");
        for (String fieldName : feildMap.keySet()) {
            columns.append(fieldName).append(", ");
            values.append("?, ");
        }
        columns.replace(columns.lastIndexOf(", "), columns.length(), ")");
        values.replace(values.lastIndexOf(", "), values.length(), ")");
        sql += columns + " VALUES " + values;

        Object[] parameters = feildMap.values().toArray();
        return 1 == executeUpdate(sql, parameters);
    }
    /*
    * 更新实体
    * */
    public static <T> boolean updateEntity (Class<T> entityClass, Map<String, Object> fieldMap, long id) {
        if (CollectionUtil.isEmpty(fieldMap)) {
            LOGGER.error("can not update entity:fieldMap is empty");
            return false;
        }
        String sql = "UPDATE " + getTableName(entityClass) + " SET ";
        StringBuilder columns = new StringBuilder();
        for(String fieldName : fieldMap.keySet()) {
            columns.append(fieldName).append("=?, ");
        }
        sql += columns.substring(0, columns.lastIndexOf(", ")) + "WHERE id = ?";
        List<Object> paraList = new ArrayList<Object>();
        paraList.addAll(fieldMap.values());
        paraList.add(id);
        Object [] parameters = paraList.toArray();
        return 1 == executeUpdate(sql, parameters);
    }
    /*
    * 删除实体
    * */
    public static <T> boolean deleteEntity (Class<T> entityClass, long id) {
        String sql = "DELETE FROM " + getTableName(entityClass) + " WHERE id = ?";
        return 1 == executeUpdate(sql, id);
    }
    /*
    * 获取实体类名（即表名）
    * */
    public static <T> String getTableName (Class<T> entityClass) {
        return entityClass.getSimpleName();
    }
    /*
    * 执行sql文件
    * */
    public static void executeSqlFile (String filePath) {
        InputStream inStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inStream));
        try {
            String sql = null;
            while ((sql = bufferedReader.readLine()) != null) {
                LOGGER.info(sql);
                executeUpdate(sql);
            }
            LOGGER.info("execute sql file success");
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("execute sql file failure", e);
            throw new RuntimeException(e);
        }
    }
}
