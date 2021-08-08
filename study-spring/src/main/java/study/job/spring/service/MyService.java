package study.job.spring.service;

import java.math.BigDecimal;

public interface MyService {

    void transfer(String sendNo, String recievedNo, int money) throws Exception;
}
