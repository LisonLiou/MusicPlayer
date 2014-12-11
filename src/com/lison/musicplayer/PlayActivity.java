package com.lison.musicplayer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.lison.musicplayer.PlayerConstant.PLAYER_STATUS;
import com.lison.musicplayer.PlayerConstant.ROUND_MODE;
import com.service.audio.PlayerService;
import com.utils.common.MusicHelper;
import com.widget.custom.LrcTextView;

public class PlayActivity extends ActionBarActivity {

	// Support ActionBar
	private ActionBar actionBar;

	// 专辑封面图片控件
	private ImageView imageViewAlbumCover;

	// 控制播放按钮
	private ImageButton imageButtonControl, imageButtonPrevious, imageButtonNext, imageButtonRound, imageButtonShuffler;

	// 歌曲标题、专辑名称、演唱者、时长、當前已播放時長
	private TextView textViewTitle, textViewAlbum, textViewArtist, textViewDuration, textViewCurrentDuration;

	// 歌词显示控件
	private LrcTextView textViewLrc;

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

	/**
	 * 当前歌曲循环方式（默认设置为全部播放）
	 */
	public static ROUND_MODE currentRoundMode = ROUND_MODE.WHOLE;

	// 设置timer为守护进程（輪詢查看currentPlayerStatus并給Handler發送消息供其處理）
	Timer timer = new Timer(true);

	// 是否循环播放
	public static Boolean RANDOM = false;

	// 前当前时间，用于对比歌词显示
	private String preCurrentDate = null;
	private SimpleDateFormat dateFormatter = null;
	private List<String> lrcTimes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_play);
		initView();

		timer.schedule(taskTick, 0, 1000);
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
	@SuppressLint("SimpleDateFormat")
	void initView() {

		dateFormatter = new SimpleDateFormat("mm:ss:SS");// 设置日期格式
		preCurrentDate = dateFormatter.format(new Date());// new Date()为获取当前系统时间

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
		imageButtonRound = (ImageButton) super.findViewById(R.id.imageButtonRound);
		imageButtonRound.setOnClickListener(new MyOnClickListener());
		imageButtonShuffler = (ImageButton) super.findViewById(R.id.imageButtonShuffler);
		imageButtonShuffler.setOnClickListener(new MyOnClickListener());

		// 设置播放器状态为正在播放（默认启动当前Activity就开始播放）
		currentPlayerStatus = PLAYER_STATUS.PLAYING;

		// 绑定音乐信息
		imageViewAlbumCover = (ImageView) super.findViewById(R.id.imageViewAlbumCover);
		imageViewAlbumCover.setOnClickListener(new MyOnClickListener());
		textViewTitle = (TextView) super.findViewById(R.id.textViewTitle);
		textViewAlbum = (TextView) super.findViewById(R.id.textViewAlbum);
		textViewArtist = (TextView) super.findViewById(R.id.textViewArtist);
		textViewDuration = (TextView) super.findViewById(R.id.textViewDuration);
		textViewCurrentDuration = (TextView) super.findViewById(R.id.textViewCurrentDuration);
		textViewLrc = (LrcTextView) super.findViewById(R.id.textViewLrc);
		textViewLrc.setOnClickListener(new MyOnClickListener());

		textViewLrc.setList(loadLrc());
		// textViewLrc.updateUI();
		lrcTimes = loadLrcTimes();

		timer.schedule(taskScrollLrc, 0, 1);

		musicHelper = new MusicHelper(this);

		seekBarProcess = (SeekBar) super.findViewById(R.id.seekBarProcess);
		// 綁定SeekBar事件
		seekBarProcess.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());

		showAlbum();
	}

	TimerTask taskScrollLrc = new TimerTask() {

		@Override
		public void run() {
			String currentDate = dateFormatter.format(new Date());// newDate()为获取当前系统时间
			try {
				Date d1;
				d1 = dateFormatter.parse(currentDate);
				Date d2 = dateFormatter.parse(preCurrentDate);

				//Log.i("minus------------------------>", dateFormatter.format(d1.getTime() - d2.getTime()));

				if (lrcTimes.contains(dateFormatter.format((d1.getTime() - d2.getTime())))) {
					Message m = textViewLrc.handler.obtainMessage();
					m.what = 1;
					textViewLrc.handler.handleMessage(m);
				}

			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * 
	 * @return
	 */
	List<String> loadLrcTimes() {
		List<String> times = new ArrayList<String>();

		times.add("00:02:85");
		times.add("00:04:93");
		times.add("00:06:92");
		times.add("00:11:58");
		times.add("00:16:12");
		times.add("00:20:84");
		times.add("00:26:08");
		times.add("00:31:08");
		times.add("00:36:13");
		times.add("00:41:00");
		times.add("00:45:94");
		times.add("00:51:12");
		times.add("00:55:69");
		times.add("01:00:74");
		times.add("01:05:64");
		times.add("01:09:09");
		times.add("01:20:56");
		times.add("01:25:47");
		times.add("01:30:38");
		times.add("01:35:35");
		times.add("01:40:24");
		times.add("01:45:45");
		times.add("01:50:30");
		times.add("01:55:14");
		times.add("01:58:39");
		times.add("02:00:14");
		times.add("02:05:05");
		times.add("02:08:74");

		return times;
	}

	/**
	 * 根据歌曲名字及演唱者查找歌词（先写个静态的）
	 * 
	 * @return
	 */
	List<String> loadLrc() {
		List<String> list = new ArrayList<String>();

		list.add("骊歌");
		list.add("演唱：Gala");

		list.add("当这一切都结束");
		list.add("你是否失落");
		list.add("当我随烟云消散");
		list.add("谁为我难过");
		list.add("没有不散的伴侣");
		list.add("你要走下去");
		list.add("没有不终的旋律");
		list.add("但我会继续");
		list.add("倘若有天想起我");
		list.add("你蓦然寂寞");
		list.add("人生是一场错过");
		list.add("愿你别蹉跎");

		list.add("当这一切已结束");
		list.add("请不要失落");
		list.add("我将随烟云消散");
		list.add("别为我难过");
		list.add("千言万语不必说");
		list.add("只有一首歌");
		list.add("都知欢聚最难得");
		list.add("难奈别离多");

		list.add("都知欢聚最难得");
		list.add("难奈别离多");

		return list;
	}

	/**
	 * 根据歌曲名字及演唱者查找歌词（先写个静态的）
	 * 
	 * @return
	 */
	String loadLrc(String songName, String artist) {
		return "[00:02.85]骊歌" + "[00:04.93]演唱：Gala" + "[00:06.92]" + "[00:11.58]当这一切都结束" + "[00:16.12]你是否失落" + "[00:20.84]当我随烟云消散" + "[00:26.08]谁为我难过"
				+ "[00:31.08]没有不散的伴侣" + "[00:36.13]你要走下去" + "[00:41.00]没有不终的旋律" + "[00:45.94]但我会继续" + "[00:51.12]倘若有天想起我" + "[00:55.69]你蓦然寂寞"
				+ "[01:00.74]人生是一场错过" + "[01:05.64]愿你别蹉跎" + "[01:09.09]" + "[01:20.56]当这一切已结束" + "[01:25.47]请不要失落" + "[01:30.38]我将随烟云消散" + "[01:35.35]别为我难过"
				+ "[01:40.24]千言万语不必说" + "[01:45.45]只有一首歌" + "[01:50.30]都知欢聚最难得" + "[01:55.14]难奈别离多" + "[01:58.39]" + "[02:00.14]都知欢聚最难得" + "[02:05.05]难奈别离多"
				+ "[02:08.74]";
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

		if (PlayerService.currentPlayingMusicIndex == MainActivity.currentMusicListIndex) {
			currentDuration = PlayerService.mediaPlayer.getCurrentPosition();
			seekBarProcess.setProgress(currentDuration);
		} else {
			// 重置当前播放进度
			textViewCurrentDuration.setText("00:00");
			currentDuration = 0;
			seekBarProcess.setProgress(currentDuration);
		}
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
				currentPlayerStatus = PLAYER_STATUS.PLAYING;
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

			int controlDrawableId = 0;
			if (msg.what == PLAYER_STATUS.STOPPED.getValue()) {
				controlDrawableId = R.drawable.music_player_control_play;
				PlayerService.mediaPlayer.stop();
			} else if (msg.what == PLAYER_STATUS.PAUSED.getValue()) {
				controlDrawableId = R.drawable.music_player_control_play;
				PlayerService.mediaPlayer.pause();
			} else if (msg.what == 111) {
				// 滚动歌词

				// this.post(r)
			} else {
				controlDrawableId = R.drawable.music_player_control_pause;
				PlayerService.mediaPlayer.start();

				if (currentDuration <= seekBarProcess.getMax()) {
					if (currentPlayerStatus == PLAYER_STATUS.PLAYING) {
						currentDuration += 1000;

						seekBarProcess.incrementProgressBy(1000);
						textViewCurrentDuration.setText(musicHelper.getDuration(currentDuration));
					}
				} else {
					currentDuration = 0;

					textViewCurrentDuration.setText("00:00");
					seekBarProcess.setProgress(0);
				}
			}

			imageButtonControl.setImageResource(controlDrawableId);
		}
	};

	/**
	 * TimerTask，輪詢用於檢測當前播放狀態currentPlayerStatus并發送消息給Handler供其處理）
	 */
	TimerTask taskTick = new TimerTask() {

		@Override
		public void run() {

			Message m = handlerProcess.obtainMessage();

			switch (currentPlayerStatus) {
			case PLAYING:
				m.what = PLAYER_STATUS.PLAYING.getValue();
				break;
			case PAUSED:
				m.what = PLAYER_STATUS.PAUSED.getValue();
				break;
			case STOPPED:
				m.what = PLAYER_STATUS.STOPPED.getValue();
				break;
			}

			handlerProcess.sendMessage(m);
		}
	};

	/**
	 * 播放下一首
	 * 
	 * @param isComplete
	 */
	public void playNext(Boolean isComplete) {

		int currentIndex = MainActivity.currentMusicIndexQueue.indexOf(MainActivity.currentMusicListIndex);
		switch (currentRoundMode) {
		case WHOLE:

			if (++currentIndex > MainActivity.currentMusicIndexQueue.size() - 1) {
				MainActivity.currentMusicListIndex = MainActivity.currentMusicIndexQueue.get(0);
			} else {
				MainActivity.currentMusicListIndex = MainActivity.currentMusicIndexQueue.get(currentIndex);
			}
			break;
		case SINGLE:
			if (!isComplete) {

				if (++currentIndex > MainActivity.currentMusicIndexQueue.size() - 1) {
					MainActivity.currentMusicListIndex = MainActivity.currentMusicIndexQueue.get(0);
				} else {
					MainActivity.currentMusicListIndex = MainActivity.currentMusicIndexQueue.get(currentIndex);
				}
			}
			break;
		}

		currentPlayerStatus = PLAYER_STATUS.PLAYING;
		handlerProcess.sendEmptyMessage(currentPlayerStatus.getValue());
		showAlbum();

		MainActivity.play(PlayActivity.this, PlayActivity.this, PLAYER_STATUS.PLAYING.getValue());
	}

	/**
	 * 播放上一首
	 * 
	 * @param isComplete
	 */
	public void playPrevious(Boolean isComplete) {

		int currentIndex = MainActivity.currentMusicIndexQueue.indexOf(MainActivity.currentMusicListIndex);
		switch (currentRoundMode) {
		case WHOLE:

			if (--currentIndex < 0) {
				MainActivity.currentMusicListIndex = MainActivity.currentMusicIndexQueue.get(MainActivity.currentMusicIndexQueue.size() - 1);
			} else {
				MainActivity.currentMusicListIndex = MainActivity.currentMusicIndexQueue.get(currentIndex);
			}
			break;
		case SINGLE:
			if (!isComplete) {
				if (--currentIndex < 0) {
					MainActivity.currentMusicListIndex = MainActivity.currentMusicIndexQueue.get(MainActivity.currentMusicIndexQueue.size() - 1);
				} else {
					MainActivity.currentMusicListIndex = MainActivity.currentMusicIndexQueue.get(currentIndex);
				}
			}
			break;
		}

		currentPlayerStatus = PLAYER_STATUS.PLAYING;
		handlerProcess.sendEmptyMessage(currentPlayerStatus.getValue());
		showAlbum();

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

				switch (currentPlayerStatus) {
				case PLAYING:
					currentPlayerStatus = PLAYER_STATUS.PAUSED;
					handlerProcess.sendEmptyMessage(currentPlayerStatus.getValue());
					break;
				case PAUSED:
					currentPlayerStatus = PLAYER_STATUS.PLAYING;
					handlerProcess.sendEmptyMessage(currentPlayerStatus.getValue());
					break;
				case STOPPED:
					currentPlayerStatus = PLAYER_STATUS.PLAYING;
					handlerProcess.sendEmptyMessage(currentPlayerStatus.getValue());
					break;
				}
			}
				break;
			case R.id.imageButtonPrevious: {
				playPrevious(false);
				break;
			}
			case R.id.imageButtonNext: {
				playNext(false);
				break;
			}
			case R.id.imageButtonRound:
				switch (currentRoundMode) {
				case WHOLE:
					currentRoundMode = ROUND_MODE.SINGLE;
					((ImageButton) view).setImageResource(R.drawable.music_player_round_one);
					break;
				case SINGLE:
					currentRoundMode = ROUND_MODE.WHOLE;
					((ImageButton) view).setImageResource(R.drawable.music_player_round_all);
					break;
				default:
					break;
				}
				break;
			case R.id.imageButtonShuffler:

				if (RANDOM) {
					((ImageButton) view).setImageResource(R.drawable.music_player_shuffler_inactive);
				} else {
					((ImageButton) view).setImageResource(R.drawable.music_player_shuffler_active);
				}

				RANDOM = !RANDOM;
				MainActivity.Shuffle(RANDOM);

				break;

			case R.id.imageViewAlbumCover:

				setAlphaForView(textViewLrc, 0.6f);
				textViewLrc.bringToFront();
				textViewLrc.setVisibility(View.VISIBLE);
				break;
			case R.id.textViewLrc:

				setAlphaForView(textViewLrc, 0f);
				imageViewAlbumCover.bringToFront();
				break;
			}
		}
	}

	void showLrc() {

	}

	void hideLrc() {

	}

	/**
	 * 設置控件透明度
	 * 
	 * @param v
	 * @param alpha
	 */
	private void setAlphaForView(View v, float alpha) {
		AlphaAnimation animation = new AlphaAnimation(alpha, alpha);
		animation.setDuration(0);
		animation.setFillAfter(true);
		v.startAnimation(animation);
	}
}
