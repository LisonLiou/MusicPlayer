package com.lison.musicplayer;

import cn.jpush.android.api.JPushInterface;
import android.app.Application;

public class EntryApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		try {
			// 开启debug模式，发布时需关闭
			JPushInterface.setDebugMode(true);

			// 初始化jPush
			JPushInterface.init(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
	}

}
