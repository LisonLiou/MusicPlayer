package com.lison.musicplayer;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.lison.musicplayer.PlayerConstant.PLAYER_STATUS;
import com.service.audio.PlayerService;
import com.utils.common.MusicHelper;

public class PlayActivity extends ActionBarActivity {

	// Support ActionBar
	private ActionBar actionBar;

	// 专辑封面图片控件
	private ImageView imageViewAlbumCover;

	// 控制播放按钮
	private ImageButton imageButtonControl, imageButtonPrevious, imageButtonNext;

	// 歌曲标题、专辑名称、演唱者、时长、當前已播放時長
	private TextView textViewTitle, textViewAlbum, textViewArtist, textViewDuration, textViewCurrentDuration;

	// SeekBar
	private SeekBar seekBarProcess;

	// 音乐助手类
	private static MusicHelper musicHelper;

	// 當前已播放時長
	private int currentDuration = 0x00;

	/**
	 * 当前播放器状态
	 */
	public static PLAYER_STATUS currentPlayerStatus = null;

	// 處理播放進度與控制的新綫程
	private Thread thread1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_play);
		initView();

		Log.i(MainActivity.TAG, "PlayActivity-->onCreate-->initView()");

		handlerProcess.post(updateThreadPlaying);
		Log.i(MainActivity.TAG, "PlayActivity-->onCreate-->handlerProcess.post(updateThreadPlaying)");

		PlayerService.playActivity = this;
	}

	/**
	 * 菜单项单击处理
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			// 点击到ActionBar中设置的Home按钮
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * 初始Activity逻辑
	 */
	void initView() {
		// 獲得当前acitonBar
		actionBar = super.getSupportActionBar();
		// 设置是否显示应用程序的图标
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		// 将应用程序图标设置为可点击的按钮,并且在图标上添加向左的箭头，该句代码起到了决定性作用
		actionBar.setDisplayHomeAsUpEnabled(true);

		imageButtonControl = (ImageButton) super.findViewById(R.id.imageButtonControl);
		// 播放、暂停按钮添加事件
		imageButtonControl.setOnClickListener(new MyOnClickListener());
		imageButtonPrevious = (ImageButton) super.findViewById(R.id.imageButtonPrevious);
		imageButtonPrevious.setOnClickListener(new MyOnClickListener());
		imageButtonNext = (ImageButton) super.findViewById(R.id.imageButtonNext);
		imageButtonNext.setOnClickListener(new MyOnClickListener());

		// 设置播放器状态为正在播放（默认启动当前Activity就开始播放）
		currentPlayerStatus = PLAYER_STATUS.PLAYING;
		Log.i(MainActivity.TAG, "PlayActivity-->onCreate-->initView-->currentPlayerStatus=PLAYER_STATUS.PLAYING");

		// 绑定音乐信息
		imageViewAlbumCover = (ImageView) super.findViewById(R.id.imageViewAlbumCover);
		textViewTitle = (TextView) super.findViewById(R.id.textViewTitle);
		textViewAlbum = (TextView) super.findViewById(R.id.textViewAlbum);
		textViewArtist = (TextView) super.findViewById(R.id.textViewArtist);
		textViewDuration = (TextView) super.findViewById(R.id.textViewDuration);
		textViewCurrentDuration = (TextView) super.findViewById(R.id.textViewCurrentDuration);
		musicHelper = new MusicHelper(this);

		seekBarProcess = (SeekBar) super.findViewById(R.id.seekBarProcess);
		// 綁定SeekBar事件
		seekBarProcess.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());

		showAlbum();

		if (PlayerService.currentPlayingMusicIndex == MainActivity.currentMusicListIndex) {
			currentDuration = PlayerService.mediaPlayer.getCurrentPosition();
			seekBarProcess.setProgress(currentDuration);
		} else {
			// 重置当前播放进度
			textViewCurrentDuration.setText("00:00");
			currentDuration = 0;
			seekBarProcess.setProgress(currentDuration);
		}

		Log.i(MainActivity.TAG, "PlayActivity-->onCreate-->initView-->currentDuration=" + currentDuration);
	}

	/**
	 * 显示专辑信息
	 */
	void showAlbum() {

		HashMap<String, Object> musicHash = MainActivity.hashMusicList.get(MainActivity.currentMusicListIndex);

		if (Integer.parseInt(musicHash.get("albumCoverExist").toString()) == 1) {
			Drawable d = (Drawable) musicHash.get("albumCover");
			imageViewAlbumCover.setImageDrawable(d);
			imageViewAlbumCover.setScaleType(ScaleType.CENTER_CROP);
		} else {
			imageViewAlbumCover.setImageResource(R.drawable.album_default_cover_normal);
			imageViewAlbumCover.setScaleType(ScaleType.CENTER_INSIDE);
		}

		textViewTitle.setText(musicHash.get("title").toString());
		textViewAlbum.setText(musicHash.get("album").toString());
		textViewArtist.setText(musicHash.get("artist").toString());
		textViewDuration.setText(musicHash.get("duration").toString());

		// 爲seekBar設置最大長度
		seekBarProcess.setMax((Integer) musicHash.get("durationMillionSecond"));

		Log.i(MainActivity.TAG, "PlayActivity-->showAlbum()-->duration=" + textViewDuration.getText());
		Log.i(MainActivity.TAG, "PlayActivity-->showAlbum()-->seekBarProcess.Max=" + seekBarProcess.getMax());
	}

	/**
	 * 自定义SeekBar拖动事件监听器
	 * 
	 * @author Administrator
	 * 
	 */
	private class MyOnSeekBarChangeListener implements OnSeekBarChangeListener {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {

			if (PlayerService.mediaPlayer != null) {

				int progress = seekBar.getProgress();
				PlayerService.mediaPlayer.seekTo(progress);

				currentDuration = progress;
				textViewCurrentDuration.setText(musicHelper.getDuration(currentDuration));

				Log.i(MainActivity.TAG, "PlayActivity-->SeekBarProcess.onStopTrackingTouch-->currentDuration=" + progress);
			}
		}
	}

	/**
	 * 消息处理
	 */
	@SuppressLint("HandlerLeak")
	Handler handlerProcess = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			Log.i(MainActivity.TAG, "PlayActivity-->handlerProcess-->msg.what=" + msg.what);

			int controlDrawableId = 0;
			if (msg.what == PLAYER_STATUS.STOPPED.getValue()) {
				controlDrawableId = R.drawable.music_player_control_play;
				PlayerService.mediaPlayer.stop();
			} else if (msg.what == PLAYER_STATUS.PAUSED.getValue()) {
				controlDrawableId = R.drawable.music_player_control_play;
				PlayerService.mediaPlayer.pause();
			} else {
				controlDrawableId = R.drawable.music_player_control_pause;
				PlayerService.mediaPlayer.start();
			}

			imageButtonControl.setImageResource(controlDrawableId);
		}
	};

	/**
	 * 线程类，正在播放
	 */
	Runnable updateThreadPlaying = new Runnable() {

		@Override
		public void run() {

			Message m = handlerProcess.obtainMessage();

			Log.i(MainActivity.TAG, "PlayActivity-->updateThreadPlaying-->currentDuration=" + currentDuration);
			Log.i(MainActivity.TAG, "PlayActivity-->updateThreadPlaying-->seekBarProcess.Max=" + seekBarProcess.getMax());

			if (currentDuration <= seekBarProcess.getMax()) {
				if (currentPlayerStatus == PLAYER_STATUS.PLAYING) {
					currentDuration += 1000;
					m.what = PLAYER_STATUS.PLAYING.getValue();
					seekBarProcess.incrementProgressBy(1000);

					textViewCurrentDuration.setText(musicHelper.getDuration(currentDuration));

					Log.i(MainActivity.TAG, "PlayActivity-->updateThreadPlaying-->handlerProcess.postDelayed(updateThreadPlaying, 1000)");
					handlerProcess.postDelayed(updateThreadPlaying, 1000);
				}
			} else {
				currentDuration = 0;
				m.what = PLAYER_STATUS.STOPPED.getValue();
				textViewCurrentDuration.setText("00:00");
				seekBarProcess.setProgress(0);
				Log.i(MainActivity.TAG, "PlayActivity-->updateThreadPlaying-->handlerProcess.postremoveCallBacks(updateThreadPlaying)");
				handlerProcess.removeCallbacks(updateThreadPlaying);
			}

			handlerProcess.handleMessage(m);
		}
	};

	/**
	 * 播放下一首
	 */
	public void playNext() {
		if (++MainActivity.currentMusicListIndex > MainActivity.hashMusicList.size() - 1) {
			MainActivity.currentMusicListIndex = 0;
		}
		initView();
		currentPlayerStatus = PLAYER_STATUS.STOPPED;
		showAlbum();

		Log.i(MainActivity.TAG, "PlayActivity-->playNext()-->currentPlayerStatus=" + currentPlayerStatus);
		MainActivity.play(PlayActivity.this, PlayActivity.this, PLAYER_STATUS.PLAYING.getValue());
	}

	/**
	 * 播放上一首
	 */
	public void playPrevious() {
		if (--MainActivity.currentMusicListIndex < 0) {
			MainActivity.currentMusicListIndex = MainActivity.hashMusicList.size() - 1;
		}

		initView();
		currentPlayerStatus = PLAYER_STATUS.STOPPED;
		showAlbum();

		Log.i(MainActivity.TAG, "PlayActivity-->playPrevious()-->currentPlayerStatus=" + currentPlayerStatus);
		MainActivity.play(PlayActivity.this, PlayActivity.this, PLAYER_STATUS.PLAYING.getValue());
	}

	/**
	 * 自定义onclick事件
	 * 
	 * @author Lison-Liou
	 * 
	 */
	class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View view) {

			switch (view.getId()) {
			case R.id.imageButtonControl: {

				Message m = handlerProcess.obtainMessage();
				switch (currentPlayerStatus) {
				case PLAYING:
					currentPlayerStatus = PLAYER_STATUS.PAUSED;
					m.what = PLAYER_STATUS.PAUSED.getValue();
					break;
				case PAUSED:
					currentPlayerStatus = PLAYER_STATUS.PLAYING;
					m.what = PLAYER_STATUS.PLAYING.getValue();
					break;
				case STOPPED:
					currentPlayerStatus = PLAYER_STATUS.PLAYING;
					m.what = PLAYER_STATUS.PLAYING.getValue();
					break;
				}

				handlerProcess.sendMessage(m);
			}
				break;
			case R.id.imageButtonPrevious: {
				playPrevious();
				break;
			}
			case R.id.imageButtonNext: {
				playNext();
				break;
			}
			}
		}
	}
}
