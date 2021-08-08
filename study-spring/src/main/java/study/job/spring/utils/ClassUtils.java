package study.job.spring.utils;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @ClassName ClassUtils
 * @Description TODO
 * @Author huangzq
 * @Mailbox 1375529585@qq.com
 * @Date 2021/8/7 16:09
 * @Version 1.0
 */
public class ClassUtils {

    /**
     * 根据包名获取包下所有类的全名，此时只存在class文件
     * @param packageName 自定包名
     * @param isHaveChildPackage 是否包括子包
     * @return
     */
    public static List<String> getClassNameListUnderBackage(String packageName, boolean isHaveChildPackage) {

        List<String> result = new ArrayList<String>();


        // 根据当前线程获取类加载器
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        try {

            // 获取class根目录下指定路径的统一资源定位符集合
            Enumeration<URL> urls = classLoader.getResources(packageName.replaceAll("\\.", "/"));
            // 迭代url
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if (url != null) {
                    // 只获取class文件类型
                    String protocol = url.getProtocol();
                    if (protocol.equals("file")) {
                        // path 为文件或者文件夹的路径，当是不包括文件或者文件夹的名称，
                        String path = url.getPath();
                        File file = new File(path);
                        result.addAll(getClassNameListByFile(file, true, packageName));
                        System.out.println(url.getPath());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static List<String> getClassNameListByFile(File file, boolean isHaveChildPackage, String packageName) {
        List<String> result = new ArrayList<String>();

        if (!file.exists()) {
            return result;
        }
        if (file.isFile()) {
            // 包含文件名
            String path = file.getPath();
            if (path.endsWith(".class")) {
                int index = path.lastIndexOf(packageName.replaceAll("\\.", "\\\\"));
                int count = path.lastIndexOf(".class");
                if (count > index) {
                    String className = path.substring(index, count);
                    className = className.replaceAll("\\\\", "\\.");
                    result.add(className);
                }

            }
        } else {
            // 文件夹
            File[] listFile = file.listFiles();
            if (listFile != null && listFile.length > 0) {
                for (File fileTemp : listFile) {
                    if (!fileTemp.isFile()) {
                        result.addAll(getClassNameListByFile(fileTemp, isHaveChildPackage, packageName));
                    } else {
                        // 包含文件名
                        String path = fileTemp.getPath();
                        if (path.endsWith(".class")) {
                            int index = path.lastIndexOf(packageName.replaceAll("\\.", "\\\\"));
                            int count = path.lastIndexOf(".class");
                            if (count > index) {
                                String className = path.substring(index, count);
                                className = className.replaceAll("\\\\", "\\.");
                                result.add(className);
                            }
                        }
                    }
                }
            }


        }
        return result;
    }

    public static void main(String[] args) {
        getClassNameListUnderBackage("study.job.spring", true);
    }
}
