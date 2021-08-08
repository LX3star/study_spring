package study.job.spring.dao;

import java.math.BigDecimal;

/**
 * @ClassName AccountDao
 * @Description TODO
 * @Author huangzq
 * @Mailbox 1375529585@qq.com
 * @Date 2021/8/7 20:54
 * @Version 1.0
 */
public interface AccountDao {

    int queryBalanceByNo(String accountNo) throws Exception;

    int updateBalanceByCardNo(String accountNo, int newBalance) throws Exception;
}
