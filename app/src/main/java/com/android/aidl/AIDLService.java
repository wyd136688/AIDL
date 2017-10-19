package com.android.aidl;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.IOException;

public class AIDLService extends Service {
    private static final String TAG = "AIDLService";
    MediaPlayer player;
    private IBinder iBinder = new AIDLRemoteService.Stub(){

        @Override
        public void onPause() throws RemoteException {
            try {
                if (player.isPlaying()) {
                //player.pause();
                    return;
               }
               Log.d(TAG,">>weiyandong>>>客户端进程对服务端进程执行了开始操作。。。");
                player.prepare();
                player.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onStop() throws RemoteException {
            if (player.isPlaying()) {
                Log.d(TAG,">>weiyandong>>> 客户端进程对服务端进程执行了暂停操作。。。");
                player.stop();
            }

        }
    };
    @Override
    public IBinder onBind(Intent intent) {
        //初始化工作
        Log.d(TAG,">>weiyandong>>> AIDLService is onBind ...  来自客户端的绑定操作已经完成");
        if (player == null) {
            player = MediaPlayer.create(this,R.raw.music);
            player = new MediaPlayer();
            try {
                FileDescriptor file = getResources().openRawResourceFd(R.raw.music).getFileDescriptor();
                player.setDataSource(file);
                player.setLooping(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(TAG,">>weiyandong>>>player is created ...  服务端播放器已经实例化，准备就绪 。。。");
        }
        return iBinder;
    }
    public boolean onUnbind(Intent intent) {
        if (player != null) {
            player.release();
        }
        Log.d(TAG,">>weiyandong>> AIDLService is onUnBind ...  客户端已经解绑断线，服务停止。。。");
        return super.onUnbind(intent);
    }
}
