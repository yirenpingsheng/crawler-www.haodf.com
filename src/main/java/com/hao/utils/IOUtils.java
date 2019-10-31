package com.hao.utils;

@SuppressWarnings("all")
public class IOUtils {

    /**
     * 关闭此流并释放与此流关联的所有系统资源
     *
     * @param autoCloseables 继承AutoCloseable接口的数据源
     */
    public static void close(AutoCloseable... autoCloseables) {
        for (AutoCloseable autoCloseable : autoCloseables)
            if (autoCloseable != null)
                try {
                    autoCloseable.close();
                } catch (Exception e) {
                }
    }

}