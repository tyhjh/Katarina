package com.yorhp.transcribescreen.utils;

import android.util.Log;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.yorhp.transcribescreen.app.MyApplication;
import com.yorhp.transcribescreen.utils.qiniuToken.Auth;

import org.json.JSONObject;

import java.io.File;

import cn.dxjia.ffmpeg.library.FFmpegNativeHelper;


/**
 * Created by Tyhj on 2017/10/19.
 */

public class Mv2Gif {

    private static final String AccessKey = "ziAwdcHcaJK5BhT9ndj7US7gKUSIMI20BgDM283X";
    private static final String SecretKey = "-c8tMsBZXAbK8epmMT2av3Blm_ePXRf47NpUhE85";

    public static boolean convert(String pathFrom, String pathTo, int time, int fps) {

        String command = "ffmpeg -i " + pathFrom + " -t " + time + " -pix_fmt rgb24 -r " + fps + " -y -f gif " + pathTo;
        Log.e("command",command);
        String result = FFmpegNativeHelper.runCommand(command);
        if (result.contains("successfully"))
            return true;
        else
            return false;
    }


    public static void upload(String pathFrom) {
        String key = "g_" + Defined.getNowTimeE();
        String pathTo = MyApplication.rootDir + "gif/" + key + ".gif";
        if (false&&convert(pathFrom, pathTo, 100, 5)) {
            String token = Auth.create(AccessKey, SecretKey).uploadToken("yorgif");
            new UploadManager().put(pathTo, key, token,
                    new UpCompletionHandler() {
                        @Override
                        public void complete(String key, ResponseInfo info, JSONObject res) {
                            //res包含hash、key等信息，具体字段取决于上传策略的设置
                            if (info.isOK()) {
                                Log.i("qiniu", "Upload Success");
                            } else {
                                Log.i("qiniu", "Upload Fail");
                                //如果失败，这里可以把info信息上报自己的服务器，便于后面分析上传错误原因
                            }
                            Log.i("qiniu", key + ",\r\n " + info + ",\r\n " + res);
                        }
                    }, null);
        }
        File file = new File(pathTo);
        if (file.exists()) {
            file.delete();
        }
    }

}
