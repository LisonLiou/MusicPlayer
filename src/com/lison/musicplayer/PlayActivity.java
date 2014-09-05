package com.lison.musicplayer;

import java.io.IOException;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
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
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.content.provider.MusicProvider;
import com.utils.common.MusicHelper;

public class PlayActivity extends ActionBarActivity {

	// Support ActionBar
	private ActionBar actionBar;

	// 专辑封面图片控件
	private ImageView imageViewAlbumCover;

	// 控制播放按钮
	private ImageButton imageButtonControl;

	// 歌曲标题、专辑名称、演唱者、时长、當前已播放時長
	private TextView textViewTitle, textViewAlbum, textViewArtist, textViewDuration, textViewCurrentDuration;

	// SeekBar
	private SeekBar seekBarProcess;

	// 音乐助手类
	private static MusicHelper musicHelper;

	// 當前已播放時長
	private int currentDuration = 0x00;

	/**
	 * 播放器状态枚舉（可獲得枚舉對應數字）
	 */
	public static enum PLAYER_STATUS {

		PLAYING(1), STOPPED(-1), PAUSED(0);

		private int value = -1;

		PLAYER_STATUS(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	/**
	 * 当前播放器状态
	 */
	public static PLAYER_STATUS currentPlayerStatus = null;

	// 處理播放進度與控制的新綫程
	private Thread thread1;

	// 当前播放的媒体id
	private static int currentMediaId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_play);

		Log.e(TAG, "start onCreate~~~");
		init();
	}

	/**
	 * 菜单项单击处理
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			// 点击到我们ActionBar中设置的Home按钮
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * 初始Activity逻辑
	 */
	void init() {
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

		// 设置播放器状态为正在播放（默认启动当前Activity就开始播放）
		currentPlayerStatus = PLAYER_STATUS.PLAYING;

		// 绑定音乐信息
		imageViewAlbumCover = (ImageView) super.findViewById(R.id.imageViewAlbumCover);
		textViewTitle = (TextView) super.findViewById(R.id.textViewTitle);
		textViewAlbum = (TextView) super.findViewById(R.id.textViewAlbum);
		textViewArtist = (TextView) super.findViewById(R.id.textViewArtist);
		textViewDuration = (TextView) super.findViewById(R.id.textViewDuration);
		textViewCurrentDuration = (TextView) super.findViewById(R.id.textViewCurrentDuration);
		musicHelper = new MusicHelper(this);

		// 獲取傳遞來的數據musicId ： _id
		Intent intentPlay = super.getIntent();
		int musicId = Integer.parseInt(intentPlay.getStringExtra("musicId"));

		if (MainActivity.mediaPlayer == null)
			MainActivity.mediaPlayer = new MediaPlayer();

		if (MainActivity.mediaPlayer.isPlaying())
			if (currentMediaId != musicId) {
				MainActivity.mediaPlayer.stop();
				MainActivity.mediaPlayer.reset();
			}

		currentMediaId = musicId;

		MusicProvider musicProvider = new MusicProvider(this);
		HashMap<String, Object> musicHash = musicProvider.getMusicDetail(currentMediaId);
		play(musicHash);

		seekBarProcess = (SeekBar) super.findViewById(R.id.seekBarProcess);
		// 爲seekBar設置最大長度
		seekBarProcess.setMax((Integer) musicHash.get("durationMillionSecond"));

		// 綁定SeekBar事件
		seekBarProcess.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());

		// 启动新线程
		if (thread1 == null) {
			thread1 = new Thread(updateThreadPlaying);
			thread1.start();
		}

		Log.i("musicDetail------->", musicHash.get("_id").toString());
		Log.i("musicDetail------->", musicHash.get("title").toString());
		Log.i("musicDetail------->", musicHash.get("duration").toString());
		Log.i("musicDetail------->", musicHash.get("artist").toString());
		Log.i("musicDetail------->", musicHash.get("album").toString());
		Log.i("musicDetail------->", musicHash.get("displayName").toString());
		Log.i("musicDetail------->", musicHash.get("data").toString());
		Log.i("musicDetail------->", musicHash.get("albumCover").toString());

	}

	private static final String TAG = "ActivityLifeCircle";

	@Override
	protected void onStart() {
		super.onStart();
		Log.e(TAG, "start onStart~~~");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.e(TAG, "start onRestart~~~");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.e(TAG, "start onResume~~~");
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.e(TAG, "start onPause~~~");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.e(TAG, "start onStop~~~");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.e(TAG, "start onDestroy~~~");
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

			if (MainActivity.mediaPlayer != null) {

				int progress = seekBar.getProgress();
				MainActivity.mediaPlayer.seekTo(progress);

				currentDuration = progress;
				textViewCurrentDuration.setText(musicHelper.getDuration(currentDuration));
			}
		}
	}

	/**
	 * 播放出错
	 * 
	 * @author Administrator
	 * 
	 */
	private class MediaErrorListener implements OnErrorListener {
		@Override
		public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
			MainActivity.mediaPlayer.stop();
			MainActivity.mediaPlayer.release();
			MainActivity.mediaPlayer = null;
			Toast.makeText(PlayActivity.this, R.string.music_play_not_music_found, Toast.LENGTH_SHORT).show();
			return false;
		}
	}

	/**
	 * 播放完成
	 * 
	 * @author Administrator
	 * 
	 */
	private class MediaCompletionListener implements OnCompletionListener {
		@Override
		public void onCompletion(MediaPlayer arg0) {
			Message m = handlerProcess.obtainMessage();
			m.what = PLAYER_STATUS.STOPPED.getValue();
			handlerProcess.sendMessage(m);
		}
	}

	/**
	 * 播放音乐
	 * 
	 * @param hash
	 */
	private void play(HashMap<String, Object> hash) {

		String path = hash.get("data").toString();

		if (path == null || "".equals(path)) {
			Toast.makeText(this, R.string.music_play_not_music_found, Toast.LENGTH_SHORT).show();
			return;
		}

		MainActivity.mediaPlayer.reset();
		MainActivity.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			MainActivity.mediaPlayer.setDataSource(path);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		MainActivity.mediaPlayer.setOnCompletionListener(new MediaCompletionListener());
		MainActivity.mediaPlayer.setOnErrorListener(new MediaErrorListener());
		try {
			MainActivity.mediaPlayer.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		MainActivity.mediaPlayer.start();

		imageViewAlbumCover.setImageDrawable((Drawable) hash.get("albumCover"));
		textViewTitle.setText(hash.get("title").toString());
		textViewAlbum.setText(hash.get("album").toString());
		textViewArtist.setText(hash.get("artist").toString());
		textViewDuration.setText(hash.get("duration").toString());
	}

	/**
	 * 消息处理
	 */
	@SuppressLint("HandlerLeak")
	Handler handlerProcess = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			int controlDrawableId = 0;
			if (msg.what == PLAYER_STATUS.STOPPED.getValue()) {
				controlDrawableId = R.drawable.music_player_control_play;
				MainActivity.mediaPlayer.stop();
				handlerProcess.removeCallbacks(updateThreadPlaying);
				textViewCurrentDuration.setText("00:00");
			} else if (msg.what == PLAYER_STATUS.PAUSED.getValue()) {
				controlDrawableId = R.drawable.music_player_control_play;
				MainActivity.mediaPlayer.pause();
			} else {
				controlDrawableId = R.drawable.music_player_control_pause;
				MainActivity.mediaPlayer.start();
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
			if (currentDuration <= seekBarProcess.getMax()) {
				if (currentPlayerStatus == PLAYER_STATUS.PLAYING) {
					currentDuration += 1000;
					m.what = PLAYER_STATUS.PLAYING.getValue();
					seekBarProcess.incrementProgressBy(1000);

					textViewCurrentDuration.setText(musicHelper.getDuration(currentDuration));
				}

			} else {
				m.what = PLAYER_STATUS.STOPPED.getValue();
			}

			handlerProcess.handleMessage(m);
			handlerProcess.postDelayed(updateThreadPlaying, 1000);
		}
	};

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
			}
		}
	}
}
