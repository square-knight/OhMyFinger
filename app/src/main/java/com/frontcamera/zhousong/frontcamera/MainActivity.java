package com.frontcamera.zhousong.frontcamera;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.os.Handler;
import android.widget.Toast;

import com.frontcamera.zhousong.Manager.SharedPreferencesManager;
import com.frontcamera.zhousong.async.TrainTask;
import com.frontcamera.zhousong.constant.Channel;
import com.frontcamera.zhousong.util.StringUtil;

import java.lang.ref.WeakReference;

public class MainActivity extends Activity {
    Context context = MainActivity.this;
    SurfaceView surfaceView;
    TextView numberTextView;
    EditText uriEditText;
    EditText collectEditText;
    Switch collectSwitch;
    Button trainButton;
    Button predictButton;
    CameraSurfaceHolder mCameraSurfaceHolder = new CameraSurfaceHolder();
    private UIHandler uiHandler;
    private long firstTime;
    private TrainTask trainTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHandler=new UIHandler(this);
        initView();
        addListener();

    }

    private void initData() {
        Channel.opt_type = Channel.OPT_PREDICT_STOP;
        Channel.baseUrl = SharedPreferencesManager.getUrl(MainActivity.this);
        Channel.y = SharedPreferencesManager.getY(MainActivity.this);
    }

    private void initView()
    {
        initData();
        setContentView(R.layout.activity_main);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
        View photoFrameLayout = findViewById(R.id.photoFrameLayout);
        resizeView(photoFrameLayout);
        mCameraSurfaceHolder.setCameraSurfaceHolder(context,surfaceView);
        numberTextView = (TextView) findViewById(R.id.numberTextView);
        uriEditText = (EditText) findViewById(R.id.uriEditText);
        uriEditText.setText(StringUtil.isEmpty(Channel.baseUrl) ? Channel.URL_UPLOAD : Channel.baseUrl);
        collectEditText = (EditText) findViewById(R.id.collectEditText);
        collectEditText.setText(Channel.y);
        collectSwitch = (Switch) findViewById(R.id.collectSwitch);
        trainButton = (Button) findViewById(R.id.trainButton);
        predictButton = (Button) findViewById(R.id.predictButton);
    }
    private void addListener(){
        uriEditText.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    Channel.baseUrl = uriEditText.getText().toString();
                    SharedPreferencesManager.setUrl(Channel.baseUrl,MainActivity.this);
                }
            }
        });
        collectEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(StringUtil.isEmpty(s.toString())){
                    collectSwitch.setChecked(false);
                    Channel.opt_type = Channel.OPT_COLLECT_STOP;
                    collectSwitch.setEnabled(false);
                }else {
                    collectSwitch.setEnabled(true);
                }
                Channel.y = s.toString();
                SharedPreferencesManager.setY(s.toString(), MainActivity.this);
            }
        });
        collectSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isPressed()){
                    // 每次 setChecked 时会触发onCheckedChanged 监听回调，
                    // 而有时我们在设置setChecked后不想去自动触发 onCheckedChanged 里的具体操作,
                    // 即想屏蔽掉onCheckedChanged;加上此判断
                    Channel.y = collectEditText.getText().toString();
                    if(isChecked){
                        if(StringUtil.isNotEmpty(Channel.y)){
                            predictButton.setText(R.string.predict_start);
                            predictButton.setEnabled(false);
                            trainButton.setEnabled(false);
                            Channel.opt_type = Channel.OPT_COLLECT_START;
                            SharedPreferencesManager.setY(Channel.y, MainActivity.this);
                        }else{
                            numberTextView.setText("图片组不能为空");
                        }
                    }else{
                        predictButton.setText(R.string.predict_start);
                        predictButton.setEnabled(true);
                        trainButton.setEnabled(true);
                        Channel.opt_type = Channel.OPT_COLLECT_STOP;
                    }
                }
            }
        });
        trainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getResources().getString(R.string.train_start)
                        .equals(trainButton.getText().toString())){
                    Channel.opt_type = Channel.OPT_PREDICT_START;
                    predictButton.setText(R.string.training1);
                    trainTask = new TrainTask();
                    trainTask.execute((Void) null);
                }
                //起个线程轮询接口
            }
        });
        predictButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getResources().getString(R.string.predict_start)
                        .equals(predictButton.getText().toString())){
                    Channel.opt_type = Channel.OPT_PREDICT_START;
                    predictButton.setText(R.string.predict_stop);
                }else{
                    Channel.opt_type = Channel.OPT_PREDICT_STOP;
                    predictButton.setText(R.string.predict_start);
                }
            }
        });

    }
    private void resizeView(View view){
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        ViewGroup.LayoutParams linearParams = view.getLayoutParams();
        linearParams.height = width - 20;
        view.setLayoutParams(linearParams);
    }
    public UIHandler getUiHandler(){
        return uiHandler;
    }
    public static class UIHandler extends Handler {
        WeakReference<MainActivity> activity;
        public UIHandler(MainActivity activity){
            this.activity=new WeakReference<MainActivity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(null==this.activity){
                return;
            }
            this.activity.get().numberTextView.setText((String)msg.obj);


        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                showToast("再戳一下退出哦");
                firstTime = secondTime;
                return true;
            } else{
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    private void showToast(String text){
        Toast toast=Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        toast.setText(text);
        toast.show();
    }
}
