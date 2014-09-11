package com.service.audio;

import java.io.IOException;
import java.util.HashMap;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import com.content.provider.MusicProvider;
import com.lison.musicplayer.MainActivity;
import com.lison.musicplayer.PlayerConstant;

public class PlayerService extends Service implements Runnable, MediaPlayer.OnCompletionListener {

	// 定义一个多媒体对象
	public static MediaPlayer mediaPlayer = null;

	// 播放器当前状态 ( 参照枚举：Player_Status )
	private int currentPlayerStatus;

	@Override
	public IBinder onBind(Intent intent) {
		Log.i("Service ------------->", "onBind");
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		if (mediaPlayer != null) {
			mediaPlayer.reset();
			mediaPlayer.release();
			mediaPlayer = null;
		}

		mediaPlayer = new MediaPlayer();

		// 监听播放是否完成
		mediaPlayer.setOnCompletionListener(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}

		System.out.println("service onDestroy");
	}

	/**
	 * 启动service时执行的方法
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Log.i("Service ------------->", "onStartCommand");

		// 得到从startService传来的动作
		currentPlayerStatus = intent.getIntExtra("CURRENT_PLAYER_STATUS", PlayerConstant.PLAYER_STATUS.PAUSED.getValue());

		if (currentPlayerStatus == PlayerConstant.PLAYER_STATUS.PLAYING.getValue() || currentPlayerStatus == PlayerConstant.PLAYER_STATUS.STOPPED.getValue()) {
			playMusic();
		} else if (currentPlayerStatus == PlayerConstant.PLAYER_STATUS.PAUSED.getValue()) {
			if (mediaPlayer.isPlaying())
				mediaPlayer.pause();
			else
				mediaPlayer.start();
		}

		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 播放音乐
	 */
	private void playMusic() {

		try {
			// 重置多媒体
			mediaPlayer.reset();

			// 读取mp3文件
			HashMap<String, Object> media = MainActivity.hashMusicList.get(MainActivity.currentMusicListIndex);
			mediaPlayer.setDataSource(media.get("data").toString());

			// 准备播放
			mediaPlayer.prepare();

			// 开始播放
			mediaPlayer.start();

			new Thread(this).start();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}
}
