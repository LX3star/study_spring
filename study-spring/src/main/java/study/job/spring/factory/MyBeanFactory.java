package study.job.spring.factory;


import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import study.job.spring.annotion.Autowired;
import study.job.spring.annotion.Component;
import study.job.spring.annotion.Service;
import study.job.spring.annotion.Transactional;
import study.job.spring.utils.ClassUtils;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName MyBeanFactory
 * @Description TODO 完成对注解的 解析， Bean对象创建及依赖注入维护
 * @Author huangzq
 * @Mailbox 1375529585@qq.com
 * @Date 2021/8/7 15:50
 * @Version 1.0
 */
public class MyBeanFactory {

    /**
     * 存储bean对象的容器，将使用注解的实例对放进容器中
     */
    private static ConcurrentHashMap<String, Object> beanMap = new ConcurrentHashMap<String, Object>();

    /**
     * 指定扫描包下的类全名 包含特定注解
     */
    private static List<String> classNameList = new ArrayList<String>();

    static {
        try {
            // 加载xml
            InputStream resourceAsStream = MyBeanFactory.class.getClassLoader().getResourceAsStream("beans.xml");
            // 解析xml
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(resourceAsStream);
            Element rootElement = document.getRootElement();
            //Element scanElement = (Element) rootElement.selectSingleNode("//component-scan");
            //String scanPackage = scanElement.attributeValue("base-package");
            String scanPackage = "study.job.spring";
            // 扫描并实例化特定注解的
            doScanAndInstance(scanPackage);
            // 注入依赖
            doAutoWired();
            // 增加事务
            doTransactional();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void doTransactional() {

        MyProxyFactory myProxyFactory = (MyProxyFactory) beanMap.get("myProxyFactory");

        // 遍历beanMap
        for (Map.Entry<String,Object> entry: beanMap.entrySet()) {

            String beanName = entry.getKey();
            Object obj = entry.getValue();
            Class<?> clazz = entry.getValue().getClass();

            if (clazz.isAnnotationPresent(Transactional.class)) {
                // 需要进行事务控制
                // 有实现接口
                Class<?>[] interfaces = clazz.getInterfaces();
                if(interfaces != null && interfaces.length > 0) {
                    // 使用jdk动态代理
                    beanMap.put(beanName, myProxyFactory.getJdkProxy(obj));

                } else {
                    // 使用cglib动态代理
                    beanMap.put(beanName, myProxyFactory.getCglibProxy(obj));
                }
            }

        }
    }

    // 获取自定包下有注解的类
    private static void doScanAndInstance(String scanPackage) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        // 获取该包下所有的类名
        List<String> classNameListTemp = ClassUtils.getClassNameListUnderBackage(scanPackage, true);
        classNameList.addAll(classNameListTemp);
        // 实例化有特定注解的类
        int len = classNameList.size();
        for (int i = 0; i < len; i++) {
            String className = classNameList.get(i);
            if (className.isEmpty()) {
                continue;
            }
            // 通过反射获取类
            Class<?> clazz = Class.forName(className);

            // 只处理标注了注解@MyService、@MyRepository和@MyComponent的类
            if (clazz.isAnnotationPresent(Service.class)
                    || clazz.isAnnotationPresent(Transactional.class)
                    || clazz.isAnnotationPresent(Component.class)) {
                //获取注解value值
                String beanId = null;
                if (clazz.isAnnotationPresent(Service.class)) {
                    beanId = clazz.getAnnotation(Service.class).value();

                } else if (clazz.isAnnotationPresent(Transactional.class)) {
                    beanId = clazz.getAnnotation(Transactional.class).value();

                } else if (clazz.isAnnotationPresent(Component.class)) {
                    beanId = clazz.getAnnotation(Component.class).value();
                }

                if (beanId.trim().equals("")) {
                    // 不存在value值
                    beanId = toLowerCaseFirstOne(clazz.getSimpleName());
                }
                Object bean = null;
                try {
                    bean = clazz.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                beanMap.put(beanId, bean);

                // service dao层往往是有接口的，面向接口开发，此时再以接口名为id，放入一份对象到容器中，便于后期根据接口类型注入
                Class<?>[] interfaces = clazz.getInterfaces();
                if(interfaces != null && interfaces.length > 0) {
                    for (int j = 0; j < interfaces.length; j++) {
                        Class<?> anInterface = interfaces[j];
                        // 以接口的全限定类名作为id放入
                        beanMap.put(toLowerCaseFirstOne(anInterface.getSimpleName()), clazz.newInstance());
                    }
                }
            }
        }

    }

    private static void doAutoWired() throws IllegalAccessException {
        // 遍历beanMap
        for (Map.Entry<String,Object> entry: beanMap.entrySet()) {
            // 这里要解决相互依赖的问题
            // 实例化bean
            Object bean = entry.getValue();
            // 类的成员变量，autowired只能作用在类的成员变量上
            Field[] declaredFields = bean.getClass().getDeclaredFields();
            // 遍历类的成员变量
            for (Field field : declaredFields) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    Autowired autowired = field.getAnnotation(Autowired.class);
                    //获取属性名称
                    String name =autowired.value();
                    if (name.trim().equals("")){
                        String className = field.getName();
                        name = toLowerCaseFirstOne(className);
                    }
                    Object obj =getBean(name);
                    field.setAccessible(true);
                    //给属性赋值
                    field.set(bean, obj);
                }
            }
        }
    }

    //首字母转小写
    public static String toLowerCaseFirstOne(String s){
        if(Character.isLowerCase(s.charAt(0))){
            return s;
        }else{
            return (new StringBuffer()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
        }
    }

    public static Object getBean(String beanId) {

        //从bean对象池map中获取
        Object bean = beanMap.get(beanId);
        return bean;
    }

}
