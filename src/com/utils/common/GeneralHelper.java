package com.utils.common;

import java.util.LinkedList;
import java.util.Random;

public class GeneralHelper {
	/**
	 * 随机排序
	 * 
	 * @param list
	 *            要进行随机排序的数据集合
	 * @return 排序结果
	 */
	public static int[] getRandomList(int[] list) {
		// 数组长度
		int count = list.length;
		// 结果集
		int[] resultList = new int[count];
		// 用一个LinkedList作为中介
		LinkedList<Integer> temp = new LinkedList<Integer>();
		// 初始化temp
		for (int i = 0; i < count; i++) {
			temp.add((Integer) list[i]);
		}
		// 取数
		Random rand = new Random();
		for (int i = 0; i < count; i++) {
			int num = rand.nextInt(count - i);
			resultList[i] = (Integer) temp.get(num);
			temp.remove(num);
		}

		return resultList;
	}
}
