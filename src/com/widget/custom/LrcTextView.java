package com.widget.custom;

import java.util.ArrayList;
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
		if (mList == null) {
			mList = new ArrayList<String>();
			mList.add(0, "什么都木有啊");
		}

		// 非高亮部分
		mPaint = new Paint();
		// 打开抗锯齿
		mPaint.setAntiAlias(true);
		// 设置字体大小
		mPaint.setTextSize(30);
		// 文字对其方式
		mPaint.setTextAlign(Paint.Align.CENTER);

		// 高亮部分 当前歌词
		mLightPaint = new Paint(mPaint);
		mLightPaint.setColor(Color.RED);

		float count = mPaint.getTextSize() * 2 / mDy;
		mDf = 1.0 / count;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mList == null || mList.size() == 0) {
			return;
		}

		int size = mList.size();
		int vHeight = getHeight();
		int light = mLightIndex;
		String text = null;
		float lineLeft = getLayout().getLineLeft(0);

		for (int i = 0; i < size; i++) {
			if (i != light) {
				text = mList.get(i);
				float y = vHeight / 2 + (i * 2) * mPaint.getTextSize() - mOffsetY;
				canvas.drawText(text, lineLeft, y, mPaint);
			}
		}

		calcFloat();

		text = mList.get(light);
		float y = vHeight / 2 + (light * 2) * mPaint.getTextSize() - mOffsetY;

		canvas.drawText(mList.get(light), lineLeft, y, mLightPaint);

		mOffsetY += mDy;

		if (mOffsetY >= getHeight() / 2 + mList.size() * mPaint.getTextSize()) {
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
			} else if (mLightIndex >= mList.size()) {
				mLightIndex = mList.size() - 1;
			}
		}
	}

	private List<String> mList;

	public List<String> getList() {
		return mList;
	}

	public void setList(List<String> list) {
		mList = list;
	}

	public void updateUI() {
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

	class updateThread implements Runnable {
		public void run() {
			while (true) {
				postInvalidate();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
