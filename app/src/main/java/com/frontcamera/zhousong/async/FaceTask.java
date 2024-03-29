package com.frontcamera.zhousong.async;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Message;
import android.util.Log;

import com.frontcamera.zhousong.Manager.OKHttpClientManager;
import com.frontcamera.zhousong.constant.Channel;
import com.frontcamera.zhousong.activity.MainActivity;
import com.frontcamera.zhousong.util.StringUtil;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

/**
 * Created by zhousong on 2016/9/28.
 * 单独的任务类。继承AsyncTask，来处理从相机实时获取的耗时操作
 */
public class FaceTask extends AsyncTask{
    private byte[] mData;
    Camera mCamera;
    MainActivity mainActivity;
    private static final String TAG = "CameraTag";
    //构造函数
    public FaceTask(byte[] data , Camera camera , MainActivity mainActivity)
    {
        this.mData = data;
        this.mCamera = camera;
        this.mainActivity = mainActivity;
    }
    @Override
    protected Object doInBackground(Object[] params) {
        Camera.Parameters parameters = mCamera.getParameters();
        int imageFormat = parameters.getPreviewFormat();
        int w = parameters.getPreviewSize().width;
        int h = parameters.getPreviewSize().height;
        String text = "";

        Rect rect = new Rect(0, 0, w, h);
        YuvImage yuvImg = new YuvImage(mData, imageFormat, w, h, null);
        try {
            ByteArrayOutputStream outputstream = new ByteArrayOutputStream();
            yuvImg.compressToJpeg(rect, 100, outputstream);
            Bitmap rawbitmap = BitmapFactory.decodeByteArray(outputstream.toByteArray(), 0, outputstream.size());
            Bitmap cuttedBitmap = Bitmap.createBitmap(rawbitmap, 0, 0, h, h);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(cuttedBitmap, 64, 64, true);
            ByteArrayOutputStream jpgOutputStream = new ByteArrayOutputStream();
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG,100,jpgOutputStream);

            byte[] bytes = jpgOutputStream.toByteArray();
//          发送预测请求
            switch (Channel.opt_type){
                case Channel.OPT_PREDICT_START:
                    OKHttpClientManager okHttpClientManage = OKHttpClientManager.getOkHttpClientManage(Channel.baseUrl + Channel.predict);
                    String post = okHttpClientManage.post(bytes);
                    text = post;
                    break;
                case Channel.OPT_COLLECT_START:
                    if(StringUtil.isNotEmpty(Channel.y)){
                        okHttpClientManage = OKHttpClientManager.getOkHttpClientManage(Channel.baseUrl + Channel.collect);
                        HashMap<String, String> headers = new HashMap<>(1);
                        headers.put("y",Channel.y);
                        post = okHttpClientManage.post(bytes,headers);
                        text = post;
                    }
                    break;
                    default :
                        text = "";
            }
        }
        catch (Exception e)
        {
            text = "获取相机实时数据失败" + e.getLocalizedMessage();
            Log.e(TAG, "onPreviewFrame: 获取相机实时数据失败" + e.getLocalizedMessage());
        }
        if(StringUtil.isNotEmpty(text)){
            Message message =Message.obtain();
            message.what = 10000;
            message.obj = text;
            mainActivity.getUiHandler().sendMessage(message);
        }
        return null;
    }
}
