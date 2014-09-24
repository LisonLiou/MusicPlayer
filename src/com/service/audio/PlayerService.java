package com.service.audio;

import java.io.IOException;
import java.util.HashMap;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.IBinder;
import android.util.Log;

import com.lison.musicplayer.MainActivity;
import com.lison.musicplayer.PlayActivity;
import com.lison.musicplayer.PlayerConstant;

public class PlayerService extends Service implements Runnable {

	// 定义一个多媒体对象
	public static MediaPlayer mediaPlayer = null;

	// 播放器当前状态 ( 参照枚举：Player_Status )
	private int currentPlayerStatus;

	// 当前正在播放的音乐索引，用于重新请求对比
	public static int currentPlayingMusicIndex = -1;

	@Override
	public IBinder onBind(Intent intent) {
		Log.i(MainActivity.TAG, "PlayerService-->onBind()");
		return null;
	}

	public static PlayActivity playActivity = null;

	@Override
	public void onCreate() {
		super.onCreate();

		if (mediaPlayer != null) {
			mediaPlayer.reset();
			mediaPlayer.release();
			mediaPlayer = null;
		}

		mediaPlayer = new MediaPlayer();
		Log.i(MainActivity.TAG, "PlayerService-->onCreate()");

		// 监听播放是否完成
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {

				PlayActivity.currentPlayerStatus = PlayerConstant.PLAYER_STATUS.STOPPED;
				Log.i(MainActivity.TAG, "PlayerService-->onCompletion()");

				playActivity.playNext();
			}
		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}

		Log.i(MainActivity.TAG, "PlayerService-->onDestroy()");
	}

	/**
	 * 启动service时执行的方法
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		// 得到从startService传来的动作
		currentPlayerStatus = intent.getIntExtra("CURRENT_PLAYER_STATUS", PlayerConstant.PLAYER_STATUS.PAUSED.getValue());

		Log.i(MainActivity.TAG, "PlayerService-->onStartCommand()-->currentPlayerStatus=" + currentPlayerStatus);

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

			Log.i(MainActivity.TAG, "PlayerService-->playMusic()-->currentPlayingMusicIndex==MainActivity.currentMusicListIndex:"
					+ (currentPlayingMusicIndex == MainActivity.currentMusicListIndex));

			// 重新请求的音乐索引与当前播放的是一样的
			if (currentPlayingMusicIndex == MainActivity.currentMusicListIndex)
				return;

			mediaPlayer.stop();

			// 重置多媒体
			mediaPlayer.reset();

			// 读取mp3文件
			HashMap<String, Object> media = MainActivity.hashMusicList.get(MainActivity.currentMusicListIndex);

			currentPlayingMusicIndex = MainActivity.currentMusicListIndex;
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
	public void run() {
		// TODO Auto-generated method stub
	}
}
