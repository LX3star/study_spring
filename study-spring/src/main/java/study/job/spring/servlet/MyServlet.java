package study.job.spring.servlet;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import study.job.spring.factory.MyBeanFactory;
import study.job.spring.service.MyService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * @ClassName MyServlet
 * @Description TODO
 * @Author huangzq
 * @Mailbox 1375529585@qq.com
 * @Date 2021/8/7 18:37
 * @Version 1.0
 */
@WebServlet(name="servlet",urlPatterns = "/servlet")
public class MyServlet extends HttpServlet {

    MyService myService = (MyService) MyBeanFactory.getBean("myService");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // 设置请求体的字符编码
        req.setCharacterEncoding("UTF-8");

        String sendNo = req.getParameter("sendNo");
        String recieveNo = req.getParameter("recieveNo");
        String moneyStr = req.getParameter("money");
        int money = Integer.parseInt(moneyStr);
        JSONObject reslt = new JSONObject();
        try {
            JSONObject data = myService.transfer(sendNo, recieveNo, money);
            reslt.put("code", 200);
            reslt.put("msg", "执行成功");
            reslt.put("data", data);

        } catch (Exception e) {
            String mes = "";
            // 在利用 Method 对象的 invoke 方法调用目标对象的方法时, 若在目标对象的方法内部抛出异常, 会抛出 InvocationTargetException 异常,
            // 该异常包装了目标对象的方法内部抛出异常, 可以通过调用 InvocationTargetException 异常类的的 getTargetException() 方法得到原始的异常.
            if (e instanceof InvocationTargetException) {
                mes = ((InvocationTargetException) e).getTargetException().toString();
            } else {
                mes = e.getMessage();
            }
            reslt.put("code", 501);
            reslt.put("msg", mes);
            reslt.put("data", new JSONObject());
        }
        // 响应
        resp.setContentType("application/json;charset=utf-8");


        resp.getWriter().print(reslt);
    }
}
