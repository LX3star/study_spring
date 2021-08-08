package study.job.spring.dao.impl;


import study.job.spring.annotion.Autowired;
import study.job.spring.annotion.Component;
import study.job.spring.dao.AccountDao;
import study.job.spring.utils.MySQLConnectionUtils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @ClassName AccountDaoImpl
 * @Description TODO
 * @Author huangzq
 * @Mailbox 1375529585@qq.com
 * @Date 2021/8/7 20:53
 * @Version 1.0
 */
@Component
public class AccountDaoImpl implements AccountDao {

    @Autowired
    private MySQLConnectionUtils mySQLConnectionUtils;

    @Override
    public int queryBalanceByNo(String cardNo) throws Exception {

        int result = 0;

        // 获取该线程当前的数据库连接
        Connection connection = mySQLConnectionUtils.getCurrentThreadConn();

        String sql = "select balance from account where accountNo=?";
        PreparedStatement preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
        preparedStatement.setString(1,cardNo);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            result = resultSet.getInt("balance");
        }
        resultSet.close();
        preparedStatement.close();
        return result;
    }

    @Override
    public int updateBalanceByCardNo(String accountNo, int newBalance) throws Exception {
        // 获取该线程当前的数据库连接
        Connection connection = mySQLConnectionUtils.getCurrentThreadConn();
        String sql = "update account set balance=? where accountNo=?";
        PreparedStatement preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
        preparedStatement.setInt(1, newBalance);
        preparedStatement.setString(2, accountNo);
        int i = preparedStatement.executeUpdate();
        preparedStatement.close();
        return i;
    }
}
