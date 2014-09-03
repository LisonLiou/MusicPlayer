package com.lison.musicplayer;

import java.util.HashMap;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.content.provider.MusicProvider;

public class PlayActivity extends ActionBarActivity {

	// Support ActionBar
	private ActionBar actionBar;

	// 专辑封面图片控件
	private ImageView imageViewAlbumCover;

	// 控制播放按钮
	private ImageButton imageButtonControl;

	// 歌曲标题、专辑名称、演唱者、时长
	private TextView textViewTitle, textViewAlbum, textViewArtist, textViewDuration;

	/*
	 * 播放器状态
	 */
	public enum PLAYER_STATUS {
		PLAYING, STOPPED, PAUSED
	}

	/**
	 * 当前播放器状态
	 */
	public static PLAYER_STATUS currentPlayerStatus = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_play);

		init();

		Intent intentPlay = super.getIntent();
		int musicId = Integer.parseInt(intentPlay.getStringExtra("musicId"));

		MusicProvider musicProvider = new MusicProvider(this);
		HashMap<String, Object> musicHash = musicProvider.getMusicDetail(musicId);

		imageViewAlbumCover.setImageDrawable((Drawable) musicHash.get("albumCover"));
		textViewTitle.setText(musicHash.get("title").toString());
		textViewAlbum.setText(musicHash.get("album").toString());
		textViewArtist.setText(musicHash.get("artist").toString());
		textViewDuration.setText(musicHash.get("duration").toString());

		Log.i("musicDetail------->", musicHash.get("_id").toString());
		Log.i("musicDetail------->", musicHash.get("title").toString());
		Log.i("musicDetail------->", musicHash.get("duration").toString());
		Log.i("musicDetail------->", musicHash.get("artist").toString());
		Log.i("musicDetail------->", musicHash.get("album").toString());
		Log.i("musicDetail------->", musicHash.get("displayName").toString());
		Log.i("musicDetail------->", musicHash.get("data").toString());
		Log.i("musicDetail------->", musicHash.get("albumCover").toString());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

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
		actionBar = super.getSupportActionBar();
		// 设置是否显示应用程序的图标
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		// 将应用程序图标设置为可点击的按钮,并且在图标上添加向左的箭头
		// 该句代码起到了决定性作用
		actionBar.setDisplayHomeAsUpEnabled(true);

		// 设置播放器状态为正在播放（默认启动当前Activity就开始播放）
		currentPlayerStatus = PLAYER_STATUS.PLAYING;

		imageButtonControl = (ImageButton) super.findViewById(R.id.imageButtonControl);
		imageButtonControl.setOnClickListener(new MyOnClickListener());

		imageViewAlbumCover = (ImageView) super.findViewById(R.id.imageViewAlbumCover);
		textViewTitle = (TextView) super.findViewById(R.id.textViewTitle);
		textViewAlbum = (TextView) super.findViewById(R.id.textViewAlbum);
		textViewArtist = (TextView) super.findViewById(R.id.textViewArtist);
		textViewDuration = (TextView) super.findViewById(R.id.textViewDuration);
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
				int controlDrawableId = 0;
				switch (currentPlayerStatus) {
				case PLAYING:
					controlDrawableId = R.drawable.music_player_control_play;
					currentPlayerStatus = PLAYER_STATUS.PAUSED;
					break;
				case PAUSED:
					controlDrawableId = R.drawable.music_player_control_pause;
					currentPlayerStatus = PLAYER_STATUS.PLAYING;
					break;
				case STOPPED:
					controlDrawableId = R.drawable.music_player_control_play;
					currentPlayerStatus = PLAYER_STATUS.PLAYING;
					break;
				}

				imageButtonControl.setImageResource(controlDrawableId);
			}
				break;
			}
		}
	}
}
