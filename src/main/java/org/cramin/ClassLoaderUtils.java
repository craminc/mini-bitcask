package org.cramin;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

/**
 * @Author: cramin
 * @Date: 2024/4/24 19:50:50
 * @Desc:
 */
public class ClassLoaderUtils {

    public static <T> List<Class<T>> loadImplement(Class<T> clazz) {
        return findImplementationsInPackage(clazz.getPackage().getName(), clazz);
    }

    private static <T> List<Class<T>> findImplementationsInPackage(String packageName, Class<T> interfaceClass) {
        List<Class<T>> implementations = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        try {
            Enumeration<URL> resources = classLoader.getResources(path);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                File directory = new File(resource.getFile());
                if (directory.exists()) {
                    implementations.addAll(
                            scanImplementations(directory, packageName, interfaceClass)
                    );
                }
            }
        } catch (Exception e) {
            // 处理异常
            e.printStackTrace();
        }

        return implementations;
    }

    private static <T> List<Class<T>> scanImplementations(File directory, String packageName, Class<T> interfaceClass) {
        List<Class<T>> implementations = new ArrayList<>();
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isDirectory()) {
                // 递归扫描子目录
                String subPackage = packageName + file.getName() + ".";
                implementations.addAll(
                        scanImplementations(file, subPackage, interfaceClass)
                );
            } else if (file.getName().endsWith(".class")) {
                // 如果是类文件，加载类并检查是否实现了指定接口
                try {
                    String className = packageName + file.getName().substring(0, file.getName().length() - 6);
                    Class<?> clazz = Class.forName(className);
                    if (interfaceClass.isAssignableFrom(clazz) && !clazz.isInterface() && !clazz.isEnum()) {
                        implementations.add((Class<T>) clazz);
                    }
                } catch (ClassNotFoundException e) {
                    // 处理类加载异常
                    e.printStackTrace();
                }
            }
        }
        return implementations;
    }
}
