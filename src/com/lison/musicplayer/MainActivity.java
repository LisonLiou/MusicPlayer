package com.lison.musicplayer;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;

import com.content.provider.MusicProvider;
import com.service.audio.PlayerService;

public class MainActivity extends ActionBarActivity {

	// http://blog.csdn.net/zzy916853616/article/details/6450753

	// ArrayList文件列表对象（数据源：当前）
	public static ArrayList<HashMap<String, Object>> hashMusicList = new ArrayList<HashMap<String, Object>>();

	// 当前播放的音乐索引
	public static int currentMusicListIndex = 0;

	// ListView文件列表控件
	private ListView listViewMusic;

	// Support ActionBar
	private ActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 初始化控件
		initView();

		// 获取音乐文件列表
		bindList();
	}

	/**
	 * 初始化控件
	 */
	void initView() {
		listViewMusic = (ListView) super.findViewById(R.id.listViewMusicList);
		actionBar = super.getSupportActionBar();

		listViewMusic.setOnItemClickListener(new MyListViewOnItemClickListener());
	}

	/**
	 * 自定义ListView项单机事件监听器
	 * 
	 * @author Lison-Liou
	 * 
	 */
	public class MyListViewOnItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			currentMusicListIndex = position;
			play(PlayerConstant.PLAYER_STATUS.PLAYING.getValue());

			Intent intentPlay = new Intent();
			intentPlay.setClass(MainActivity.this, PlayActivity.class);
			startActivity(intentPlay);
		}
	}

	/**
	 * 开启service播放音乐
	 * 
	 * @param action
	 */
	public void play(int action) {
		Intent intent = new Intent();
		intent.putExtra("CURRENT_PLAYER_STATUS", action);
		intent.setClass(MainActivity.this, PlayerService.class);
		startService(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 绑定MusicListView
	 * 
	 */
	void bindList() {

		// http://blog.csdn.net/zhang31jian/article/details/21231467

		MusicProvider musicProvider = new MusicProvider(this);
		hashMusicList = musicProvider.getAllMusic();

		int[] displayControls = { 0, R.id.textViewMusicTitle, R.id.textViewMusicDuration, 0, R.id.textViewMusicAuthor, R.id.textViewAlbumName,
				R.id.textViewFileDisplayName, R.id.textViewFileAbsolutePath, R.id.imageViewAlbumCover };

		SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, hashMusicList, R.layout.music_list, MusicProvider.getMusicListDisplayColumnDefault(),
				displayControls);
		listViewMusic.setAdapter(adapter);

		adapter.setViewBinder(new ViewBinder() {

			@Override
			public boolean setViewValue(View view, Object data, String textRepresentation) {
				if (view instanceof ImageView && data instanceof Drawable) {
					ImageView iv = (ImageView) view;
					iv.setImageDrawable((Drawable) data);
					return true;
				} else
					return false;
			}
		});
	}
}
