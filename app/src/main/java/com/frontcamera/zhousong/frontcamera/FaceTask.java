package com.frontcamera.zhousong.frontcamera;

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
import com.frontcamera.zhousong.util.StringUtil;

import java.io.ByteArrayOutputStream;

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
    FaceTask(byte[] data , Camera camera)
    {
        this.mData = data;
        this.mCamera = camera;

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
            if(Channel.predicting){
                OKHttpClientManager okHttpClientManage = OKHttpClientManager.getOkHttpClientManage(Channel.url);
                String post = okHttpClientManage.post(bytes);
                text = post;
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
