package com.lison.musicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;
import android.widget.Toast;

public class PlayActivity extends ActionBarActivity {

	private TextView textViewTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_play);

		textViewTitle = (TextView) super.findViewById(R.id.textViewTitle);

		Intent intentPlay = super.getIntent();
		textViewTitle.setText(intentPlay.getStringExtra("musicId"));
	}
}
