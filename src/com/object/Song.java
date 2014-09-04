package com.object;

import java.io.File;
import java.io.IOException;

import android.util.Log;

/**
 * 未使用此model，哪天有空的时候重构代码，改成OOP
 * @author Administrator
 *
 */
public class Song {

	private String title;
	private String artist;
	private String album;
	private String year;
	private String trackLength;

	public String getTitle() {
		return title;
	}

	public String getArtist() {
		return artist;
	}

	public String getAlbum() {
		return album;
	}

	public String getYear() {
		return year;
	}

	public String getTrackLength() {
		return trackLength;
	}

	public Song(String fileName) {
		
	}
}
