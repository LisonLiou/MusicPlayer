package com.widget.custom;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class LrcTextView extends TextView {

	private float float2 = 0.01f;

	// 普通画笔
	private Paint mPaint;
	// 高亮画笔
	private Paint mLightPaint;

	// Y偏移量
	private float mOffsetY = 0.0f;

	private float mDy = 2.0f;
	private double mDf;

	// 高亮行的索引
	private int mLightIndex = 0;
	public static int count = 0;

	public LrcTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public LrcTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public LrcTextView(Context context) {
		super(context);
		init();
	}

	private void init() {

		setFocusable(true);
		if (mTextList == null) {
			mTextList = new ArrayList<String>();
			mTextList.add(0, "什么都木有啊");
		}

		// 非高亮部分
		mPaint = new Paint();
		// 打开抗锯齿
		mPaint.setAntiAlias(true);
		// 设置字体大小
		mPaint.setTextSize(20);
		// 文字对其方式
		mPaint.setTextAlign(Paint.Align.CENTER);

		// 高亮部分 当前歌词
		mLightPaint = new Paint(mPaint);
		mLightPaint.setColor(Color.WHITE);

		float count = mPaint.getTextSize() * 2 / mDy;
		mDf = 1.0 / count;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mTextList == null || mTextList.size() == 0) {
			return;
		}

		int size = mTextList.size();
		int vHeight = getHeight();
		int light = mLightIndex;
		String text = null;
		float lineLeft = getLayout().getLineLeft(0);

		for (int i = 0; i < size; i++) {
			if (i != light) {
				text = mTextList.get(i);
				float y = vHeight / 2 + (i * 2) * mPaint.getTextSize() - mOffsetY;
				canvas.drawText(text, lineLeft, y, mPaint);
			}
		}

		calcFloat();

		text = mTextList.get(light);
		float y = vHeight / 2 + (light * 2) * mPaint.getTextSize() - mOffsetY;

		canvas.drawText(mTextList.get(light), lineLeft, y, mLightPaint);

		mOffsetY += mDy;

		if (mOffsetY >= getHeight() / 2 + mTextList.size() * mPaint.getTextSize()) {
			mOffsetY = 0;
			mLightIndex = 0;
		}
	}

	private void calcFloat() {
		float2 += mDf;
		if (float2 > 1.0) {
			float2 = 0.01f;
			mLightIndex++;
			if (mLightIndex < 0) {
				mLightIndex = 0;
			} else if (mLightIndex >= mTextList.size()) {
				mLightIndex = mTextList.size() - 1;
			}
		}
	}

	private List<String> mTextList;

	public List<String> getTextList() {
		return mTextList;
	}

	public void setTextList(List<String> list) {
		mTextList = list;
	}

	private List<String> mTimeList;

	public List<String> getTimeList() {
		return mTimeList;
	}

	public void setTimeList(List<String> list) {
		mTimeList = list;
	}

	public void updateUI() {
		dateFormatter = new SimpleDateFormat("mm:ss:SS");// 设置日期格式
		preCurrentDate = dateFormatter.format(new Date());// new Date()为获取当前系统时间
		new Thread(new updateThread()).start();
	}

	@SuppressLint("HandlerLeak")
	public Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case 1:

				Log.i("lrcTextView------------------->", "postInvalidate()");

				postInvalidate();
				break;
			}
		}
	};

	// 前当前时间，用于对比歌词显示
	private String preCurrentDate = null;
	private SimpleDateFormat dateFormatter = null;
	int light = -1;

	class updateThread implements Runnable {
		public void run() {
			while (true) {
				String currentDate = dateFormatter.format(new Date());// newDate()为获取当前系统时间
				Date d1;
				Object obj = null;
				try {
					d1 = dateFormatter.parse(currentDate);
					Date d2 = dateFormatter.parse(preCurrentDate);

					obj = dateFormatter.format((d1.getTime() - d2.getTime()));
					if (mTimeList.indexOf(obj) != -1) {
						light = mTimeList.indexOf(obj);

						Log.i("matched", obj.toString());
					}

					// Log.i("light========================>", obj.toString() +
					// " " + lrcTimes.indexOf(obj) + " " + light + " ");
				} catch (ParseException e) {
					e.printStackTrace();
				}

				//Log.i("matched=======================>", obj.toString());

				postInvalidate();
				try {
					if (obj != null) {

						int mi = dateFormatter.parse(obj.toString()).getSeconds() * 1000;

						//Log.i("matched=======================>", mi + "");
						//Thread.sleep(mi);
						
						Thread.sleep(200);
					}

				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
