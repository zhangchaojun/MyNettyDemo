package com.example.myclient.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ThreadPoolUtil {

    private static ExecutorService threadPoolExecutor;

    /**
     * 获取fixed线程池单例 
     *
     * @return 线程池
     */
    public static ExecutorService getFixedThreadPool() {
        if (threadPoolExecutor == null) {
            synchronized (ThreadPoolUtil.class) {
                if (threadPoolExecutor == null) {
                    //创建定长线程池
                    threadPoolExecutor = Executors.newFixedThreadPool(10);
                }
            }
        }
        return threadPoolExecutor;
    }

}
