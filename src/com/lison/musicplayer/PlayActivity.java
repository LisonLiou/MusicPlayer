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

	/**
	 * 播放器状态枚舉（可獲得枚舉對應數字）
	 */
	public enum PLAYER_STATUS {

		PLAYING(1), STOPPED(-1), PAUSED(0);

		private int value = -1;

		PLAYER_STATUS(int value) {
			this.value = value;
		}

		public int value() {
			return this.value;
		}
	}

	/**
	 * 当前播放器状态
	 */
	public static PLAYER_STATUS currentPlayerStatus = null;

	/**
	 * 主播放控件
	 */
	public MediaPlayer mediaPlayer = null;

	// 當前已播放時長
	private int currentDuration = 0x00;

	// 音乐助手类
	private static MusicHelper musicHelper;

	// 當前播放的音樂id
	public static int CurrentPlayMusicId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_play);

		init();
	}

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

		// 设置播放器状态为正在播放（默认启动当前Activity就开始播放）
		currentPlayerStatus = PLAYER_STATUS.PLAYING;

		imageButtonControl = (ImageButton) super.findViewById(R.id.imageButtonControl);
		// 播放、暂停按钮添加事件
		imageButtonControl.setOnClickListener(new MyOnClickListener());

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

		// if (CurrentPlayMusicId == 0) {
		PlayActivity.this.mediaPlayer = new MediaPlayer();
		// } else if (CurrentPlayMusicId != musicId) {
		// mediaPlayer.stop();
		// PlayActivity.this.mediaPlayer.release();
		// PlayActivity.this.mediaPlayer = null;
		// CurrentPlayMusicId = musicId;
		// PlayActivity.this.mediaPlayer = new MediaPlayer();
		// }

		MusicProvider musicProvider = new MusicProvider(this);
		HashMap<String, Object> musicHash = musicProvider.getMusicDetail(musicId);
		play(musicHash);

		seekBarProcess = (SeekBar) super.findViewById(R.id.seekBarProcess);
		// 爲seekBar設置最大長度
		seekBarProcess.setMax((Integer) musicHash.get("durationMillionSecond"));

		// 綁定SeekBar事件
		seekBarProcess.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());

		// 线程类压入looper
		handlerProcess.post(updateThreadPlaying);

		Log.i("musicDetail------->", musicHash.get("_id").toString());
		Log.i("musicDetail------->", musicHash.get("title").toString());
		Log.i("musicDetail------->", musicHash.get("duration").toString());
		Log.i("musicDetail------->", musicHash.get("artist").toString());
		Log.i("musicDetail------->", musicHash.get("album").toString());
		Log.i("musicDetail------->", musicHash.get("displayName").toString());
		Log.i("musicDetail------->", musicHash.get("data").toString());
		Log.i("musicDetail------->", musicHash.get("albumCover").toString());

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

			if (!fromUser)
				return;

			if (PlayActivity.this.mediaPlayer != null) {

				Message m = handlerProcess.obtainMessage();
				m.what = PLAYER_STATUS.PLAYING.value;

				PlayActivity.this.mediaPlayer.seekTo(progress);
				handlerProcess.sendMessage(m);
				currentDuration = progress;
				textViewCurrentDuration.setText(musicHelper.getDuration(currentDuration));
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

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
			PlayActivity.this.mediaPlayer.stop();
			PlayActivity.this.mediaPlayer.release();
			PlayActivity.this.mediaPlayer = null;
			Toast.makeText(PlayActivity.this, "", Toast.LENGTH_SHORT).show();
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
			m.what = PLAYER_STATUS.STOPPED.value;
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

		PlayActivity.this.mediaPlayer.reset();
		PlayActivity.this.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			PlayActivity.this.mediaPlayer.setDataSource(path);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PlayActivity.this.mediaPlayer.setOnCompletionListener(new MediaCompletionListener());
		PlayActivity.this.mediaPlayer.setOnErrorListener(new MediaErrorListener());
		try {
			PlayActivity.this.mediaPlayer.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PlayActivity.this.mediaPlayer.start();

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

			if (msg.what == PLAYER_STATUS.STOPPED.value) {
				handlerProcess.removeCallbacks(updateThreadPlaying);
				textViewCurrentDuration.setText("00:00");
			} else if (msg.what == PLAYER_STATUS.PAUSED.value) {
				handlerProcess.removeCallbacks(updateThreadPlaying);
				PlayActivity.this.mediaPlayer.pause();
			} else {
				PlayActivity.this.mediaPlayer.start();
			}

			super.handleMessage(msg);
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
				currentDuration += 1000;
				m.what = PLAYER_STATUS.PLAYING.value;
				seekBarProcess.incrementProgressBy(1000);

				textViewCurrentDuration.setText(musicHelper.getDuration(currentDuration));

			} else {
				m.what = PLAYER_STATUS.STOPPED.value;
			}

			handlerProcess.handleMessage(m);
			handlerProcess.postDelayed(updateThreadPlaying, 1000);
		}
	};

	/**
	 * 綫程類，停止播放
	 */
	Runnable updateThreadStopped = new Runnable() {
		@Override
		public void run() {
			Message m = handlerProcess.obtainMessage();
			m.what = PLAYER_STATUS.STOPPED.value;

			handlerProcess.handleMessage(m);
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
				int controlDrawableId = 0;
				Message m = handlerProcess.obtainMessage();
				switch (currentPlayerStatus) {
				case PLAYING:
					controlDrawableId = R.drawable.music_player_control_play;
					currentPlayerStatus = PLAYER_STATUS.PAUSED;
					m.what = PLAYER_STATUS.PAUSED.value;
					break;
				case PAUSED:
					controlDrawableId = R.drawable.music_player_control_pause;
					currentPlayerStatus = PLAYER_STATUS.PLAYING;
					handlerProcess.post(updateThreadPlaying);
					m.what = PLAYER_STATUS.PLAYING.value;
					break;
				case STOPPED:
					controlDrawableId = R.drawable.music_player_control_play;
					currentPlayerStatus = PLAYER_STATUS.PLAYING;
					m.what = PLAYER_STATUS.PLAYING.value;
					break;
				}

				handlerProcess.sendMessage(m);
				imageButtonControl.setImageResource(controlDrawableId);
			}
				break;
			}
		}
	}
}
