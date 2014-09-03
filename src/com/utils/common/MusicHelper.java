package com.utils.common;

import java.math.BigDecimal;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class MusicHelper {

	// 音樂封面Content Uri
	private static final String MUSIC_ALBUMS_CONTENT_URI_STRING = "content://media/external/audio/albums";

	// 当前调用源的上下文句柄对象
	private Context _context;

	// Constructor
	public MusicHelper(Context context) {
		this._context = context;
	}

	/**
	 * 
	 * 根据int时长获取时长：02:33
	 * 
	 * @param d
	 * @return
	 */
	public String getDuration(int d) {

		// //example -:)
		// 369396/1000/60, 369396/1000/60.00,
		// (369396/1000/60.00-369396/1000/60)*60
		// 6 6.150000 9.000000
		// m s

		String formatter = "";

		int m = d / 1000 / 60;
		int h = m / 60;
		int s = (int) ((d / 1000 / 60.00 - m) * 60);

		if (h != 0)
			if (h / 10 == 0) {
				formatter = "0" + h;
			} else
				formatter = String.valueOf(h);

		if (formatter != "")
			formatter += ":";

		if (m != 0)
			if (m / 10 == 0) {
				formatter += "0" + m;
			} else
				formatter += "" + String.valueOf(m);

		if (s != 0)
			if (s / 10 == 0) {
				formatter += ":0" + s;
			} else
				formatter += ":" + String.valueOf(s);

		return formatter;

		// B double f = d / 1000.00 / 60.00;
		// BigDecimal b = new BigDecimal(f);
		// double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		//
		// return String.valueOf(f1);
	}

	/**
	 * 
	 * 功能 通过album_id查找 album_art 如果找不到返回null
	 * 
	 * @param album_id
	 * @return album_art
	 */
	public String getAlbumArt(int album_id) {

		String[] projection = new String[] { "album_art" };
		Cursor cur = _context.getContentResolver().query(Uri.parse(MUSIC_ALBUMS_CONTENT_URI_STRING + "/" + Integer.toString(album_id)), projection, null, null,
				null);
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
