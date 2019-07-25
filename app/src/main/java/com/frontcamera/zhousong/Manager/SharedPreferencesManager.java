package com.frontcamera.zhousong.Manager;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {

    //创建一个SharedPreferences    类似于创建一个数据库，库名为 data
    public static SharedPreferences share(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("finger", Context.MODE_PRIVATE);
        return sharedPreferences;
    }

    //name 账号
    //调用上面的 share(Context context) 方法 获取标识为 "name" 的数据
    public static String getUrl(Context context){
        return share(context).getString("baseUrl",null);
    }
    //调用上面的 share(Context context) 方法 将数据存入，并标识为 "name"
    //使用 commit() 方法保存会给出反应（保存成功或失败）
    public static boolean setUrl(String url, Context context){
        SharedPreferences.Editor e = share(context).edit();
        e.putString("baseUrl",url);
        Boolean bool = e.commit();
        return bool;
    }
    public static String getY(Context context){
        return share(context).getString("y","");
    }
    public static boolean setY(String y, Context context){
        SharedPreferences.Editor e = share(context).edit();
        e.putString("y",y);
        Boolean bool = e.commit();
        return bool;
    }
}
