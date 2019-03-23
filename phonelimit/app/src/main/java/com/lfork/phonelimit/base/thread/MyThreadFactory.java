package com.lfork.phonelimit.base.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author 98620
 * @date 2018/7/16
 */
public class MyThreadFactory implements ThreadFactory {
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    public MyThreadFactory(String namePrefix) {
        this.namePrefix = namePrefix+"-";
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread( r,namePrefix + threadNumber.getAndIncrement());
        if (t.isDaemon()) {
            t.setDaemon(true);
        }
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }
}
