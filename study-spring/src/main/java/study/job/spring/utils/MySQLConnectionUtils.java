package study.job.spring.utils;

import study.job.spring.annotion.Component;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @ClassName MySQLConnectionUtils
 * @Description TODO
 * @Author huangzq
 * @Mailbox 1375529585@qq.com
 * @Date 2021/8/7 20:46
 * @Version 1.0
 */
@Component
public class MySQLConnectionUtils {
    // 每个线程连接数据库都有一个独立的连接，才可以保证事务
    private ThreadLocal<Connection> threadLocal = new ThreadLocal<>();

    /**
     * 从当前线程获取连接
     */
    public Connection getCurrentThreadConn() throws SQLException {
        /**
         * 判断当前线程中是否已经绑定连接，如果没有绑定，需要从连接池获取一个连接绑定到当前线程
         */
        Connection connection = threadLocal.get();
        if(connection == null) {
            // 从连接池拿连接并绑定到线程
            connection = DruidUtils.getInstance().getConnection();
            // 绑定到当前线程
            threadLocal.set(connection);
        }
        return connection;

    }
}
