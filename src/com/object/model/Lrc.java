package com.object.model;

/**
 * 歌词实体（暂未使用）
 * 
 * @author Administrator
 * 
 */
public class Lrc {
	private String lrcStr; // 歌词内容
	private int lrcTime; // 当前歌词时间

	public String getLrcStr() {
		return lrcStr;
	}

	public void setLrcStr(String lrcStr) {
		this.lrcStr = lrcStr;
	}

	public int getLrcTime() {
		return lrcTime;
	}

	public void setLrcTime(int lrcTime) {
		this.lrcTime = lrcTime;
	}
}
