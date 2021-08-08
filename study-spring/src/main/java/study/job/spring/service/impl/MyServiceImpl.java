package study.job.spring.service.impl;

import com.alibaba.fastjson.JSONObject;
import study.job.spring.annotion.Autowired;
import study.job.spring.annotion.Service;
import study.job.spring.annotion.Transactional;
import study.job.spring.dao.AccountDao;
import study.job.spring.service.MyService;

import java.math.BigDecimal;

/**
 * @ClassName MyServiceImpl
 * @Description TODO
 * @Author huangzq
 * @Mailbox 1375529585@qq.com
 * @Date 2021/8/7 19:46
 * @Version 1.0
 */
@Service("myService")
@Transactional
public class MyServiceImpl implements MyService {

    @Autowired
    private AccountDao accountDao;

    @Override
    public JSONObject transfer(String sendNo, String recievedNo, int money) throws Exception {

        JSONObject result = new JSONObject();

        try {
            // 从sendNo转账到recievedNo
            int sendMoneyBofore = accountDao.queryBalanceByNo(sendNo);

            int sendMoneyAfter = sendMoneyBofore - money;

            // 更新信息

            accountDao.updateBalanceByCardNo(sendNo, sendMoneyAfter);

            if (sendMoneyAfter < 0) {
                throw new Exception("余额不能为负数");
            }

            int recievedMoneyBofore = accountDao.queryBalanceByNo(recievedNo);

            int recievedMoneyAfter = recievedMoneyBofore + money;

            // 更新信息

            accountDao.updateBalanceByCardNo(recievedNo, recievedMoneyAfter);

            result.put("sendNoMoney", sendMoneyAfter);
            result.put("recievedNoMoney", recievedMoneyAfter);

        } catch (Exception e) {
            throw e;
        }

        return result;



    }
}
