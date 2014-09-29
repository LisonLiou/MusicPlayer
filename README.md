MusicPlayer
===========

Music Player现存bug及未完成功能

1.[已完成][bug]音乐列表中若点击的音乐为当前正在播放的，音乐依然从头播放。不应该替换掉当前正在播放，而是转过来继续。
     1.1[bug]上述问题已解决，但是发现新问题为：虽然选择当前正在播放的音乐继续播放了，但是播放界面的进度条却重新计算了。。。

2.[已完成][func]未设置循环模式：单曲，全部，随机

3.[已完成][func]本曲播放完成后根据上述规则进行跳转。上一曲，下一曲雷同。

4.[已完成][bug]播放完成时，继续播放有bug，切换回列表也有bug(修改之后还有bug)

5.[已完成][log]为Service设置最高优先级，防止被系统回收。修改service的exported为false，防止其他程序调用此service

6.[func]音乐频谱功能参考地址：
http://blog.csdn.net/gigatron/article/details/7866910

7.[func]安卓遮罩层效果，可用于歌词显示（但是未同步显示，还需要另外的同步歌词的部分）
http://blog.csdn.net/onerain88/article/details/6434208

8.[advanced func]可以选择音频源，例如当前音频源为本地音乐的ContentProvider，若同属某WLAN下，可以进行DLNA或UPNP播放。

9.[advanced func]设置中可以选择默认的音乐排序规则：按最后修改时间；字母索引。。。

10.[已完成][bug]PlayActivity中若当前专辑无封面，则搞个清晰度高的封面放上去，现在这个太小，已经失真。

11.[func]在actionBar中加入手动刷新音乐列表的功能。

12.[已完成][UI]为MainActivity音乐列表界面加入图片原型遮罩层(还有点瑕疵)

13.[func]歌词部分参考地址：http://www.marschen.com/forum.php?mod=viewthread&tid=23786

