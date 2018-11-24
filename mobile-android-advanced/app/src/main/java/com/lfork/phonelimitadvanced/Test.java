package com.lfork.phonelimitadvanced;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by 98620 on 2018/10/30.
 */
public class Test {
    private void setAirPlaneMode(boolean enable) {
        int mode = enable ? 1 : 0;
        String cmd = "settings put global airplane_mode_on " + mode;
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }



    }





}
