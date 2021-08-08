package study.job.spring.service;

import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;

public interface MyService {

    JSONObject transfer(String sendNo, String recievedNo, int money) throws Exception;
}
