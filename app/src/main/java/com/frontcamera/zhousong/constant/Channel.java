package com.frontcamera.zhousong.constant;

public class Channel {
    public static final String URL_UPLOAD = "http://192.168.10.59:9003";
    public static String baseUrl = URL_UPLOAD;
    public static String urlPredict = baseUrl + "/predict";
    public static String urlTrain = baseUrl + "/train";
    public static String urlCollect = baseUrl + "/collect";
    public static String opt_type = "";
    public static final String OPT_TRAIN_STOP = "train_stop";
    public static final String OPT_TRAIN_START = "train_start";
    public static final String OPT_PREDICT_STOP = "predict_stop";
    public static final String OPT_PREDICT_START = "predict_start";
    public static final String OPT_COLLECT_STOP = "collect_start";
    public static final String OPT_COLLECT_START = "collect_stop";

    public static String y = "";
}
