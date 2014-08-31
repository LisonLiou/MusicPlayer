package com.object;

import java.io.File;
import java.io.IOException;

import org.cmc.music.metadata.IMusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;

import android.util.Log;

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
