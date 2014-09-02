package com.lison.musicplayer;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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

import com.content.provider.MusicProvider;

public class MainActivity extends ActionBarActivity {

	// Music ArrayList
	private ArrayList<HashMap<String, Object>> hashMusicList = new ArrayList<HashMap<String, Object>>();

	// Music ListView
	private ListView listViewMusic;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		listViewMusic = (ListView) super.findViewById(R.id.listViewMusicList);

		bindList();
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

	/**
	 * 绑定MusicListView
	 * 
	 */
	void bindList() {

		// http://blog.csdn.net/zhang31jian/article/details/21231467

		MusicProvider musicProvider = new MusicProvider(this);
		hashMusicList = musicProvider.getAllMusic();

		int[] displayControls = { R.id.textView1, R.id.textView2, R.id.textView3, R.id.textView4, R.id.textView5, R.id.textView6, R.id.imageView1 };

		SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, hashMusicList, R.layout.music_list, musicProvider.getMusicListDisplayColumnDefault(),
				displayControls);
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

}
