package com.content.provider;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;

import com.lison.musicplayer.R;
import com.utils.common.MusicHelper;

public class MusicProvider {

	// 音乐列表CONTENT URI
	public static final Uri MUSIC_CONTENT_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

	// 音乐列表默认where条件
	public static final String MUSIC_LIST_WHERE_CONDITION_DEFAULT = "mime_type in ('audio/mpeg','audio/x-ms-wma') and bucket_display_name <> 'audio' and is_music > 0 ";

	// 音乐列表默认数据源列
	public static final String[] MUSIC_LIST_SOURCE_COLUMN_DEFAULT = { MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DURATION,
			MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media._ID, MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.DISPLAY_NAME,
			MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID, };

	// 音乐列表默认显示列
	public static final String[] MUSIC_LIST_DISPLAY_COLUMN_DEFAULT = { "_id", "title", "duration", "artist", "album", "displayName", "data", "albumCover" };

	// 音乐列表默认排序规则
	public static final String MUSIC_LIST_SORT_ORDER_DEFAULT = "_id desc";

	public static Uri getMusicContentUri() {
		return MUSIC_CONTENT_URI;
	}

	public static String getMusicListWhereConditionDefault() {
		return MUSIC_LIST_WHERE_CONDITION_DEFAULT;
	}

	public static String[] getMusicListSourceColumnDefault() {
		return MUSIC_LIST_SOURCE_COLUMN_DEFAULT;
	}

	public static String[] getMusicListDisplayColumnDefault() {
		return MUSIC_LIST_DISPLAY_COLUMN_DEFAULT;
	}

	public static String getMusicListSortOrderDefault() {
		return MUSIC_LIST_SORT_ORDER_DEFAULT;
	}

	// 当前调用源的上下文句柄对象
	private Context _context;

	public MusicProvider(Context context) {
		this._context = context;
	}

	/**
	 * 获取设备上所有符合类型的音乐列表
	 * 
	 * @return
	 */
	public ArrayList<HashMap<String, Object>> getAllMusic() {
		Cursor cursor = _context.getContentResolver().query(MUSIC_CONTENT_URI, MUSIC_LIST_SOURCE_COLUMN_DEFAULT, MUSIC_LIST_WHERE_CONDITION_DEFAULT, null,
				MUSIC_LIST_SORT_ORDER_DEFAULT);

		MusicHelper musicHelper = new MusicHelper(_context);
		ArrayList<HashMap<String, Object>> hashMusicList = new ArrayList<HashMap<String, Object>>();
		while (cursor.moveToNext()) {

			Drawable albumCover = Drawable
					.createFromPath(musicHelper.getAlbumArt(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))));
			if (albumCover == null)
				albumCover = _context.getResources().getDrawable(R.drawable.album_default_cover);

			HashMap<String, Object> hash1 = new HashMap<String, Object>();
			hash1.put("_id", cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
			hash1.put("title", cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
			hash1.put("duration", musicHelper.getDuration(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))));
			hash1.put("artist", cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
			hash1.put("_id", cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));

			hash1.put("album", cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
			hash1.put("displayName", cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
			hash1.put("data", cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
			hash1.put("albumCover", albumCover);

			hashMusicList.add(hash1);
		}

		return hashMusicList;
	}

	/**
	 * 获取指定的音乐明细
	 * 
	 * @param musicId
	 * @return
	 */
	public HashMap<String, Object> getMusicDetail(int musicId) {
		Cursor cursor = _context.getContentResolver().query(MUSIC_CONTENT_URI, MUSIC_LIST_SOURCE_COLUMN_DEFAULT, "_id=?",
				new String[] { String.valueOf(musicId) }, MUSIC_LIST_SORT_ORDER_DEFAULT);

		MusicHelper musicHelper = new MusicHelper(_context);

		while (cursor.moveToNext()) {

			Drawable albumCover = Drawable
					.createFromPath(musicHelper.getAlbumArt(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))));
			if (albumCover == null)
				albumCover = _context.getResources().getDrawable(R.drawable.album_default_cover);

			HashMap<String, Object> hash1 = new HashMap<String, Object>();
			hash1.put("_id", cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
			hash1.put("title", cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
			hash1.put("duration", musicHelper.getDuration(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))));
			hash1.put("artist", cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
			hash1.put("_id", cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));

			hash1.put("album", cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
			hash1.put("displayName", cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
			hash1.put("data", cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
			hash1.put("albumCover", albumCover);

			return hash1;
		}

		return null;
	}
}
