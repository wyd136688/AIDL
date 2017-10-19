package com.android.aidl;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "MainActivity";
    private Button onPauseButton;
    private Button onStopButton;
    private AIDLRemoteService aidlService;
    private boolean isBind = false;
    private ServiceConnection connection =new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isBind = true;
            aidlService = AIDLRemoteService.Stub.asInterface(service);
            Log.d(TAG,">>>weiyandong>>onServiceConnected ----包名："+name.getPackageName()+",,类名"+
            name.getClassName()+"binder 对象 ："+service.toString());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBind = false;
            aidlService = null;
            Log.d(TAG,">>weiyandong>>>onServiceDisconnected----包名："+name.getPackageName()+",,类名"+
                    name.getClassName()+"当前客户端线程断线操作，服务被清空");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onPauseButton = (Button)findViewById(R.id.pause);
        onStopButton = (Button)findViewById(R.id.stop);
        onPauseButton.setOnClickListener(this);
        onStopButton.setOnClickListener(this);
        bind();
    }

    public void bind() {
        Intent intent = new Intent(this,AIDLService.class);
        bindService(intent,connection,BIND_AUTO_CREATE);
        Log.d(TAG,">>weiyandong>>.Activity已经创建成功，执行绑定服务端线程活动中。。。");
    }

    public void unBind() {
        if (isBind) {
            Log.d(TAG,">>weiyandong>>> Activity 被销毁，与服务端线程的通信将被解绑停止。。。");
            unbindService(connection);
            aidlService = null;
            isBind = false;
        }
    }

    @Override
    protected void onDestroy() {
        unBind();
        Log.d(TAG,">>weiyandong>>> 客户端线程被销毁，活动被解绑停止。");
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pause:
                try {
                    aidlService.onPause();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.stop:
                try {
                    aidlService.onStop();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }
}
