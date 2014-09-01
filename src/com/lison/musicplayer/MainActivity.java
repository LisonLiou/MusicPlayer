package com.lison.musicplayer;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

	private ActionBar actionBar;
	private ArrayList<HashMap<String, Object>> hashMusicList = new ArrayList<HashMap<String, Object>>();

	private ListView listViewMusic;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		actionBar = getSupportActionBar();
		listViewMusic = (ListView) super.findViewById(R.id.listViewMusicList);

		BindList();
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

	class MusicListViewAdapter extends BaseAdapter {
		private ArrayList<HashMap<String, String>> hashList = new ArrayList<HashMap<String, String>>();
		private Activity activity;

		public MusicListViewAdapter(Activity activity, ArrayList<HashMap<String, String>> hash) {
			super();
			this.activity = activity;
			this.hashList = hash;

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return hashList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub

			return hashList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		public class ViewHolder {
			TextView txtId;
			TextView txtDeviceIp;
			TextView txtDeviceName;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub

			ViewHolder holder;
			LayoutInflater inflater = activity.getLayoutInflater();

			if (arg1 == null) {
				arg1 = inflater.inflate(R.layout.music_list, null);
				holder = new ViewHolder();
				holder.txtId = (TextView) arg1.findViewById(R.id.textView1);
				holder.txtDeviceIp = (TextView) arg1.findViewById(R.id.textView2);
				holder.txtDeviceName = (TextView) arg1.findViewById(R.id.textView3);

				arg1.setTag(holder);
			} else {
				holder = (ViewHolder) arg1.getTag();
			}

			HashMap<String, String> map = hashList.get(arg0);
			holder.txtId.setText(map.get("id"));
			holder.txtDeviceIp.setText(map.get("deviceIp"));
			holder.txtDeviceName.setText(map.get("deviceName"));

			return arg1;
		}
	}

	// 绑定ListView
	void BindList() {

		// http://blog.csdn.net/zhang31jian/article/details/21231467
		String whereCondition = "mime_type in ('audio/mpeg','audio/x-ms-wma') and bucket_display_name <> 'audio' and is_music > 0 ";
		String[] sourceColumns = { MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media._ID,
				MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID, };
		String[] displayColumns = { "title", "duration", "artist", "album", "displayName", "data", "albumCover" };
		int[] displayControls = { R.id.textView1, R.id.textView2, R.id.textView3, R.id.textView4, R.id.textView5, R.id.textView6, R.id.imageView1 };

		Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, sourceColumns, whereCondition, null, "_id desc");

		while (cursor.moveToNext()) {
			Log.i("MEDIA----------", cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
			Log.i("MEDIA>>>>>>>>>>", cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));

			Drawable albumCover = Drawable.createFromPath(getAlbumArt(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))));
			if (albumCover == null)
				albumCover = getResources().getDrawable(R.drawable.album_default_cover);

			Log.i("MEDIA**********", albumCover == null ? String.valueOf(R.drawable.album_default_cover) : albumCover.toString());

			HashMap<String, Object> hash1 = new HashMap<String, Object>();
			hash1.put("title", cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
			hash1.put("duration", getDuration(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))));
			hash1.put("artist", cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
			hash1.put("_id", cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));

			hash1.put("album", cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
			hash1.put("displayName", cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
			hash1.put("data", cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
			hash1.put("albumCover", albumCover);

			hashMusicList.add(hash1);
		}

		SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, hashMusicList, R.layout.music_list, displayColumns, displayControls);
		listViewMusic.setAdapter(adapter);

		adapter.setViewBinder(new ViewBinder() {

			@Override
			public boolean setViewValue(View view, Object data, String textRepresentation) {
				// TODO Auto-generated method stub
				if (view instanceof ImageView && data instanceof Drawable) {
					ImageView iv = (ImageView) view;
					iv.setImageDrawable((Drawable) data);
					return true;
				} else
					return false;
			}
		});
	}

	/**
	 * 
	 * 根据int时长获取时长：02:33
	 * 
	 * @param d
	 * @return
	 */
	private String getDuration(int d) {

		double f = d / 1000.00 / 60.00;
		BigDecimal b = new BigDecimal(f);
		double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

		return String.valueOf(f1);
	}

	/**
	 * 
	 * 功能 通过album_id查找 album_art 如果找不到返回null
	 * 
	 * @param album_id
	 * @return album_art
	 */
	private String getAlbumArt(int album_id) {
		String mUriAlbums = "content://media/external/audio/albums";
		String[] projection = new String[] { "album_art" };
		Cursor cur = this.getContentResolver().query(Uri.parse(mUriAlbums + "/" + Integer.toString(album_id)), projection, null, null, null);
		String album_art = null;
		if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
			cur.moveToNext();
			album_art = cur.getString(0);
		}
		cur.close();
		cur = null;
		return album_art;
	}
}
