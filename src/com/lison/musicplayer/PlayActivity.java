package com.lison.musicplayer;

import java.util.HashMap;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.content.provider.MusicProvider;

public class PlayActivity extends ActionBarActivity {

	private ActionBar actionBar;

	private ImageView imageViewAlbumCover;
	private TextView textViewTitle,textViewAlbum,textViewArtist,textViewDuration;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_play);

		actionBar = super.getSupportActionBar();

		imageViewAlbumCover=(ImageView)super.findViewById(R.id.imageViewAlbumCover);
		textViewTitle = (TextView) super.findViewById(R.id.textViewTitle);
		textViewAlbum=(TextView)super.findViewById(R.id.textViewAlbum);
		textViewArtist=(TextView)super.findViewById(R.id.textViewArtist);
		textViewDuration=(TextView)super.findViewById(R.id.textViewDuration);
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
}
