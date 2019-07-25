package com.frontcamera.zhousong.async;

import android.os.AsyncTask;

import com.frontcamera.zhousong.Manager.OKHttpClientManager;
import com.frontcamera.zhousong.constant.Channel;

public class TrainTask extends AsyncTask {
    @Override
    protected Object doInBackground(Object[] objects) {
        OKHttpClientManager okHttpClientManage = OKHttpClientManager.getOkHttpClientManage(Channel.urlTrain);
        String post = null;
        try {
            post = okHttpClientManage.post(null, OKHttpClientManager.MEDIATYPE_JSON);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
