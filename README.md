# Android录屏+视频转Gif实现

原文链接：https://www.jianshu.com/p/81cb36b610f4

### 录屏转动图
最近看见一个视频转GIF的开源项目：[ffmpeg-commands-executor-library](https://github.com/dxjia/ffmpeg-commands-executor-library)，有时会有手机录屏转成GIF动图的需求，都是下载一个录屏软件录屏，再下一个转换软件，一般是电脑上的软件或者在线转换，效果也不怎么好，就想借这个东西来做一个录屏转GIF合一的APP


#### Android录屏
Android 5.0+ 可以利用MediaProject 在非 root 情况下实现屏幕录制，具体过程就是开启录屏服务，设置SurfaceView去接收内容，获取视频流，然后通过MediaCodec来实现视频的硬编码，然后保存为视频文件

> [Android 5.0+ 屏幕录制](https://github.com/GLGJing/ScreenRecorder)：介绍了如何进行屏幕录制还有具体的demo
[Android截屏、录屏工具](https://www.jianshu.com/p/8a428fb45098)：可以快速依赖，集成录屏功能


### FFmpeg的使用

1.FFmpeg视频转GIF命令，我感觉应该这应该是是最全的命令了，时间单位为s，会根据宽度值自适应高度
```java
String command = "ffmpeg -i " + pathFrom + " -ss " + 跳过开头时间 + " -t " + 转换的时间 + " -vf scale=" + gif宽度 + ":-1 -r " + gif帧率 + " " + pathTo;
//ffmpeg-commands-executor-library中的方法
```

2.使用ffmpeg-commands-executor-library中的方法来执行命令
```java
FFmpegNativeHelper.runCommand(command);
```
这样的确可以转化成功，但是有一个bug，就是每次打开APP只能转换一次，第二次会失败，看得出来作者已经没有维护了，这样其实也没什么问题，每次转换重新打开APP就好了，非常简单，而且还有其他ffmpeg的功能也可以用。


### 编译FFmpeg库
ffmpeg这么强大的一个开源库，现在程序有这么大一个bug存在，那肯定是不能忍受的，可以自己去下载ffmpeg源码然后编译出在Android上面可以运行的库。涉及到JNI和NDk，但其实一点都不难，我之前也没有接触过，不过看了几篇教程踩了不少坑也成功了。

> [在Android中集成FFmpeg](https://www.jianshu.com/p/a62b6520e0de)：我编译的库，可以快速依赖，集成FFmpeg，也有源码

网上文章很多，但是真的有各种问题，我试了不少，下面两篇文章完全照着做其实也是不行的，但是改改还是可以搞定。

[在Mac下编译 FFmpeg ，并在Android中使用：
](http://www.jianshu.com/p/c7bab9c020f0)[在Android 中使用FFmpeg命令](http://www.jianshu.com/p/a18fdae2fa7b)

#### 注意问题
* [下载版本相同的源码（3.3）](https://github.com/FFmpeg/FFmpeg/tree/release/3.3)
* 文章里面教的复制的ffmpeg的源码有点小问题，找不到的就先不要管；
* 在Android里面报错找不到c++的类，我们要根据找不到的类的名字去FFmpeg里面去找，然后添加进去；
* 然后c++中有些明明同一个包下的类找不到，因为在同一个文件夹下面的类其实不用导入，原c++文件导入时候添加了当前包名导致路径出错，可以直接删除导入的代码（就是import*什么的删除掉就好了）
* 其实到了文章最后，也讲了之前那个二次调用失败的问题，并且给出了解决方案：
```
FFmpeg每次执行完命令后会调用 ffmpeg_cleanup 函数清理内存，并且会调用exit(0)结束当前进程，但是经过我们的修改，exit()的代码已经被删掉，而我们的App还在运行，不能杀死进程，所以有些变量的值还在内存中，这样就会导致下次执行的时候可能会出错。
打开ffmpeg.c找到刚修改的run函数，然后在 return 前加上如下代码即可:
```
```c
nb_filtergraphs = 0;
     progress_avio = NULL;

     input_streams = NULL;
     nb_input_streams = 0;
     input_files = NULL;
     nb_input_files = 0;

     output_streams = NULL;
     nb_output_streams = 0;
     output_files = NULL;
     nb_output_files = 0;
```
#### 完美方案
但是这个方法只解决了函数清理的问题，这样去运行命名，还是有问题程序完成后直接退出了，原因很简单，其实就是c++完成后调用了下面这个函数，是结束当前进程，这个东西还是没有改好
```c
int exit_program(int ret)
{
    if (program_exit)
        program_exit(ret);
    return ret;
}
```
只需要改成这样就好了：
```c
int exit_program(int ret)
{
    return ret;
}
```

参考了这篇文章后面的坑点修改：
http://www.jianshu.com/p/ceaa286d8aff

Android录屏参考链接：
https://github.com/GLGJing/ScreenRecorder

ffmpeg命令参考链接：
[使用 ffmpeg 实现 MP4 与 GIF 的互转](http://note.rpsh.net/posts/2015/04/21/mac-osx-ffmpeg-mp4-gif-convert/)
[FFmpeg续篇：截取视频片段转成GIF动画](http://itindex.net/detail/53447-ffmpeg-%E8%A7%86%E9%A2%91-%E7%89%87%E6%AE%B5)


[在Android中集成FFmpeg](https://www.jianshu.com/p/a62b6520e0de)
项目源码：https://github.com/tyhjh/FFmpeg
最后我的APP连接：http://lc-fgtnb2h8.cn-n1.lcfile.com/eb77c867e490eba1d9ba.apk

支持录屏后转gif，支持摄像后转gif，支持本地视频转gif以及各种参数设置（侧边栏菜单中设置）

