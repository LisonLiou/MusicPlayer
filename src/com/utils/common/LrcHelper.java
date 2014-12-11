package com.utils.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.util.Log;
import com.object.model.Lrc;

public class LrcHelper {
	private List<Lrc> lrcList;

	public LrcHelper() {
		super();
		lrcList = new ArrayList<Lrc>();
		lrcList.clear();
	}

	public String loadLrc(String searchPath) {
		String path = searchPath;
		StringBuffer stringBuffer = new StringBuffer();
		// 得到歌词文件路径
		String lrcPathString = path.substring(0, path.lastIndexOf(".")) + ".lrc";
		int index = lrcPathString.lastIndexOf("/");

		String parentPath;
		String lrcName;
		parentPath = lrcPathString.substring(0, index);
		lrcName = lrcPathString.substring(index);
		File file = new File(lrcPathString);

		// 匹配Lyrics
		if (!file.exists()) {
			file = new File(parentPath + "/../" + "Lyrics/" + lrcName);
		}

		if (!file.exists()) {
			stringBuffer.append("未找到歌词文件");
			return stringBuffer.toString();
		}

		try {
			FileInputStream fin = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fin, "utf-8");
			BufferedReader br = new BufferedReader(isr);

			String s;
			boolean isLrc = false;
			while ((s = br.readLine()) != null) {
				// if(isLrc){

				s = s.replace("[", ""); // 去掉左边括号

				String lrcData[] = s.split("]");

				// 这句是歌词
				if (lrcData[0].matches("^\\d{2}:\\d{2}.\\d+$")) {
					int len = lrcData.length;
					int end = lrcData[len - 1].matches("^\\d{2}:\\d{2}.\\d+$") ? len : len - 1;

					for (int i = 0; i < end; i++) {
						Lrc lrcContent = new Lrc();
						//int lrcTime = TimeUtil.getLrcMillTime(lrcData[i]);
						//lrcContent.setLrcTime(lrcTime);
						if (lrcData.length == end)
							lrcContent.setLrcStr(""); // 空白行
						else
							lrcContent.setLrcStr(lrcData[len - 1]);

						lrcList.add(lrcContent);
					}
				}
			}
//			// 按时间排序
//			Collections.sort(lrcList, new LrcComparator());
//
//			if (lrcList.size() == 0) {
//				stringBuffer.append(MusicManager.OperateState.READLRC_LISTNULL);
//			} else {
//				stringBuffer.append(MusicManager.OperateState.READLRC_SUCCESS);
//			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// stringBuffer.append("未找到歌词文件");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// stringBuffer.append("不支持的编码");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// stringBuffer.append("IO错误");
		}

		return stringBuffer.toString();
	}

	public List<Lrc> getLrclists() {
		return lrcList;
	}
}
