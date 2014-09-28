package com.lison.musicplayer;

public class PlayerConstant {
	/**
	 * 播放器状态枚舉（可獲得枚舉對應數字）
	 */
	public static enum PLAYER_STATUS {

		PLAYING(1), STOPPED(-1), PAUSED(0);

		private int value = -1;

		PLAYER_STATUS(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	/**
	 * 循环方式
	 * 
	 * @author Administrator
	 * 
	 */
	public static enum ROUND_MODE {

		// 单曲
		SINGLE,

		// 全部
		WHOLE,

		// 随机
		RANDOM
	}
}
