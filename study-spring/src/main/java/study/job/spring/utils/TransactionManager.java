package study.job.spring.utils;

import study.job.spring.annotion.Autowired;
import study.job.spring.annotion.Component;

import java.sql.SQLException;

/**
 * @ClassName TransactionManager
 * @Description TODO
 * @Author huangzq
 * @Mailbox 1375529585@qq.com
 * @Date 2021/8/7 21:13
 * @Version 1.0
 */
@Component
public class TransactionManager {

    @Autowired
    private MySQLConnectionUtils mySQLConnectionUtils;

    public void setConnectionUtils(MySQLConnectionUtils mySQLConnectionUtils) {
        this.mySQLConnectionUtils = mySQLConnectionUtils;
    }



    // 开启手动事务控制
    public void beginTransaction() throws SQLException {
        mySQLConnectionUtils.getCurrentThreadConn().setAutoCommit(false);
    }


    // 提交事务
    public void commit() throws SQLException, SQLException {
        mySQLConnectionUtils.getCurrentThreadConn().commit();
    }


    // 回滚事务
    public void rollback() throws SQLException {
        mySQLConnectionUtils.getCurrentThreadConn().rollback();
    }
}
