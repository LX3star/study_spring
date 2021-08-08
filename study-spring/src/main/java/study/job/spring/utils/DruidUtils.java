package study.job.spring.utils;

import com.alibaba.druid.pool.DruidDataSource;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @ClassName DruidUtils
 * @Description TODO
 * @Author huangzq
 * @Mailbox 1375529585@qq.com
 * @Date 2021/8/7 20:36
 * @Version 1.0
 */
public class DruidUtils {

    public DruidUtils() {

    }

    private static DruidDataSource druidDataSource = new DruidDataSource();

    static {

        druidDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        druidDataSource.setUrl("jdbc:mysql://localhost:3306/demo");
        druidDataSource.setUsername("root");
        druidDataSource.setPassword("root");

    }

    public static DruidDataSource getInstance() {
        return druidDataSource;
    }

    public static void main(String[] args) throws SQLException {
        Connection connection = getInstance().getConnection();
        String sql = "select balance from account where accountNo=?";
        PreparedStatement preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
        preparedStatement.setString(1, "001");
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            int i = resultSet.getInt("balance");
            System.out.println(i);
        }
        resultSet.close();
        preparedStatement.close();


    }
}