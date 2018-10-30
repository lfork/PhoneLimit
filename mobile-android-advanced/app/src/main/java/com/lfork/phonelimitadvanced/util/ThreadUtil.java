package com.lfork.phonelimitadvanced.util;

import java.util.concurrent.Executors;

/**
 * Created by 98620 on 2018/7/15.
 */
public class ThreadUtil {

    void getFixedThreadPool(){
//        Executors.newFixedThreadPool();
        Executors.newCachedThreadPool();
    }
}
