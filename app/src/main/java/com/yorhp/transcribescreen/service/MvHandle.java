package com.yorhp.transcribescreen.service;

import android.graphics.Point;
import android.util.Log;

import com.yorhp.transcribescreen.utils.FFmpeg;

/**
 * 作者：Tyhj on 2018/11/6 20:30
 * 邮箱：tyhj5@qq.com
 * github：github.com/tyhjh
 * description：
 */

public class MvHandle {

    /**
     * 裁剪视频尺寸
     *
     * @param pathFrom
     * @param pathTo
     * @param startPoint 开始的点
     * @param width      裁剪后视频的宽度
     * @param height
     * @return
     */
    public static boolean cropMvSize(String pathFrom, String pathTo, Point startPoint, int width, int height) {



        String command = "ffmpeg -i " + pathFrom + " -filter:v crop=" + width + ":" + height + ":" + startPoint.x + ":" + startPoint.y +" "+ pathTo;
        Log.e("command", command);
        int result = FFmpeg.getsInstance().run(command.split(" "));
        if (result == 0)
            return true;
        else
            return false;
    }


    public static boolean getFirstFrame() {
        return true;
    }


}
