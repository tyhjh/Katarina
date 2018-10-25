# Android录屏+视频转Gif实现

> 原文链接：https://www.zybuluo.com/Tyhj/note/946784

最近在GitHub上面看见一个视频转GIF的开源项目：https://github.com/dxjia/ffmpeg-commands-executor-library
然后很多时候都会有手机录屏后再转成GIF动态图片的需求，都是下载一个录屏软件然后录屏再下载一个转换软件，而且一般是电脑上的软件或者在线转换，效果也是不怎么好，就想借这个东西来做一个录屏转GIF合一的软件。

### [Android录屏（5.0+）](https://github.com/GLGJing/ScreenRecorder)
从 Android 4.4 开始支持手机端本地录屏，但首先需要获取 root 权限才行，Android 5.0 引入 MediaProject，
可以不用 root 就可以录屏，但需要弹权限获取窗口，需要用户允许才行，这里主要介绍 Android 5.0+ 利用
MediaProject 在非 root 情况下实现屏幕录制。

### 基本原理
在 Android 5.0，Google 终于开放了视频录制的接口，其实严格来说，是屏幕采集的接口，也就是 MediaProjection
和 MediaProjectionManager。 

### 具体实现步骤
#### 1 申请权限
在 AndroidManifest 中添加权限
```
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.RECORD_AUDIO"/>
```
Android 6.0 加入的动态权限申请，如果应用的 `targetSdkVersion` 是 23，申请敏感权限还需要动态申请
```
if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    != PackageManager.PERMISSION_GRANTED) {  
  ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);
}
if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO)
    != PackageManager.PERMISSION_GRANTED) {  
  ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECORD_AUDIO}, AUDIO_REQUEST_CODE);
}
```
#### 2 获取 MediaProjectionManager 实例
`MediaProjectionManager ` 也是系统服务的一种，通过 `getSystemService` 来获取实例
```
MediaProjectionManager projectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
```
#### 3 发起屏幕捕捉请求
```
Intent captureIntent= projectionManager.createScreenCaptureIntent(); 
startActivityForResult(captureIntent, REQUEST_CODE);
```
#### 4 获取 MediaProjection
 通过 `onActivityResult` 返回结果获取 `MediaProjection `
```
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
  if (requestCode == RECORD_REQUEST_CODE && resultCode == RESULT_OK) {
    mediaProjection = projectionManager.getMediaProjection(resultCode, data);
  }
}
```
#### 5 创建虚拟屏幕
这一步就是通过 `MediaProject` 录制屏幕的关键所在，`VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR` 参数是指创建屏幕镜像，所以我们实际录制内容的是屏幕镜像，但内容和实际屏幕是一样的，并且这里我们把 `VirtualDisplay` 的渲染目标 Surface 设置为 `MediaRecorder` 的 `getSurface`，后面我就可以通过 `MediaRecorder` 将屏幕内容录制下来，并且存成 video 文件

```
private void createVirtualDisplay() {
  virtualDisplay = mediaProjection.createVirtualDisplay(
        "MainScreen",
        width,
        height,
        dpi,
        DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
        mediaRecorder.getSurface(),
        null, null);
}
```
#### 6 录制屏幕数据
这里利用 `MediaRecord` 将屏幕内容保存下来，当然也可以利用其它方式保存屏幕内容，例如：`ImageReader`
```
private void initRecorder() {
  File file = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".mp4");
  mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
  mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
  mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
  mediaRecorder.setOutputFile(file.getAbsolutePath());
  mediaRecorder.setVideoSize(width, height);
  mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
  mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
  mediaRecorder.setVideoEncodingBitRate(5 * 1024 * 1024);
  mediaRecorder.setVideoFrameRate(30);
  try {
    mediaRecorder.prepare();
  } catch (IOException e) {
    e.printStackTrace();
  }
}

public boolean startRecord() {
  if (mediaProjection == null || running) {
    return false;
  }
  initRecorder();
  createVirtualDisplay();
  mediaRecorder.start();
  running = true;
  return true;
}
```

### 自己编译的，可以直接使用的FFmpeg库：[在Android中集成FFmpeg](https://www.jianshu.com/p/a62b6520e0de)

### ffmpeg视频转GIF命令
```java
//我感觉应该是最全的命令了，时间单位为s，会根据宽度值自适应高度
String command = "ffmpeg -i " + pathFrom + " -ss " + 跳过开头时间 + " -t " + 转换的时间 + " -vf scale=" + gif宽度 + ":-1 -r " + gif帧率 + " " + pathTo;
//ffmpeg-commands-executor-library中的方法
FFmpegNativeHelper.runCommand(command);
```
这样的确可以转化成功，但是有一个bug，就是每次打开APP只能转换一次，第二次会失败，看得出来作者已经没有维护了，这样其实也没什么问题，每次转换重新打开APP就好了，非常简单，而且还有其他ffmpeg的功能也可以用。


### 使用ffmpeg编译Android可运行库
ffmpeg这么强大的一个开源库，我们现在程序有这么大一个bug存在，那肯定是不能忍受的，我们可以自己去下载ffmpeg源码然后编译出在Android上面可以运行的库。涉及到JNI和NDk，但其实一点都不难，我之前也没有接触过，不过看了几篇教程踩了不少坑也成功了。
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

ffmpeg命名参考链接：
[使用 ffmpeg 实现 MP4 与 GIF 的互转](http://note.rpsh.net/posts/2015/04/21/mac-osx-ffmpeg-mp4-gif-convert/)
[FFmpeg续篇：截取视频片段转成GIF动画](http://itindex.net/detail/53447-ffmpeg-%E8%A7%86%E9%A2%91-%E7%89%87%E6%AE%B5)


最后我的APP连接：http://ac-FGTNB2h8.clouddn.com/a820aae748fd5d87fa14.apk

支持录屏后转gif，支持摄像后转gif，支持本地视频转gif以及各种参数设置，其实还有gif直接获取URL，但是使用的是七牛云，有流量限制所以未完全开放

