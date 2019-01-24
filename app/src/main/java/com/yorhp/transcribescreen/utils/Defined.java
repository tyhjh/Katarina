package com.yorhp.transcribescreen.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Outline;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Tyhj on 2016/12/5.
 */

public class Defined {

    public static int scrWidth = 0;

    //获取控件宽高
    public static int[] getViewMeasure(View view) {
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthMeasureSpec, heightMeasureSpec);
        Log.e("获取控件宽高", "：" + view.getMeasuredWidth());
        return new int[]{view.getMeasuredWidth(), view.getMeasuredHeight()};
    }


    //是否有网络
    public static boolean isIntenet(Context context) {
        ConnectivityManager con = (ConnectivityManager) context.getSystemService(Activity.CONNECTIVITY_SERVICE);
        boolean wifi = con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        boolean internet = con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        if (wifi || internet) {
            return true;
        } else {
            return false;
        }
    }

    //设置控件轮廓
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static ViewOutlineProvider getOutline(boolean b, final int pading, final int circularBead) {
        if (b) {
            return new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    final int margin = Math.min(view.getWidth(), view.getHeight()) / pading;
                    outline.setOval(margin, margin, view.getWidth() - margin, view.getHeight() - margin);
                }
            };
        } else {
            return new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    final int margin = Math.min(view.getWidth(), view.getHeight()) / pading;
                    outline.setRoundRect(margin, margin, view.getWidth() - margin, view.getHeight() - margin, circularBead);
                    //outline.setOval(margin, margin, view.getWidth() - margin, view.getHeight() - margin);
                }
            };
        }
    }

    //获取bitmap颜色
    public static Palette.Swatch getColor(Bitmap bitmap) {
        // Palette的部分
        Palette palette = Palette.generate(bitmap);
        Palette.Swatch swatche = null;
        if (palette != null) {
            swatche = palette.getVibrantSwatch();
        }
        return swatche;
    }

    //从uri得到path
    public static String getFilePathFromContentUri(Uri uri, ContentResolver contentResolver) {
        String filePath;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};
        Cursor cursor = contentResolver.query(uri, filePathColumn, null, null, null);
//      也可用下面的方法拿到cursor
//      Cursor cursor = this.context.managedQuery(selectedVideoUri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }


    //图片压缩
    public static void ImgCompress(String filePath, File newFile, int IMAGE_SIZE) {
        //图片质量
        int imageMg = 100;
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        //规定要压缩图片的分辨率
        options.inSampleSize = calculateInSampleSize(options, 1080, 1920);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, imageMg, baos);
        //如果文件大于100KB就进行质量压缩，每次压缩比例增加百分之五
        while (baos.toByteArray().length / 1024 > IMAGE_SIZE && imageMg > 60) {
            baos.reset();
            imageMg -= 5;
            bitmap.compress(Bitmap.CompressFormat.JPEG, imageMg, baos);
        }
        //然后输出到指定的文件中
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(newFile);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //图片压缩
    public static void ImgCompress(String filePath, File newFile, int x, int y, int size) {
        int imageMg = 100;
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        //规定要压缩图片的分辨率
        options.inSampleSize = calculateInSampleSize(options, x, y);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, imageMg, baos);
        //如果文件大于100KB就进行质量压缩，每次压缩比例增加百分之五
        while (baos.toByteArray().length / 1024 > size && imageMg > 50) {
            baos.reset();
            imageMg -= 5;
            bitmap.compress(Bitmap.CompressFormat.JPEG, imageMg, baos);
        }
        //然后输出到指定的文件中
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(newFile);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //计算图片的缩放值
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    //文件复制
    public static void copyFile(File source, File dest) throws IOException {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } finally {
            inputChannel.close();
            outputChannel.close();
        }
    }

    //判断 服务 是否运行
    public static boolean isServiceRun(Context mContext, String className) {
        boolean isRun = false;
        ActivityManager activityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(40);
        int size = serviceList.size();
        for (int i = 0; i < size; i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRun = true;
                break;
            }
        }
        return isRun;
    }

    //手机号是否正确
    public static boolean isMobile(String mobiles) {
        if (mobiles == null || mobiles.equals("")) {
            return false;
        }
        Pattern p = Pattern.compile("^1(3|4|5|7|8)[0-9]\\d{8}$");
        Matcher m = p.matcher(mobiles);
        boolean is = m.matches();
        return is;
    }

    //邮箱是否正确
    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        boolean is = m.matches();
        return is;
    }

    //获取屏幕大小
    public static int getWidth(Context context, boolean is) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        final int width = wm.getDefaultDisplay().getWidth();
        final int height = wm.getDefaultDisplay().getHeight();
        if (is)
            return width;
        else
            return height;
    }


    //保存信息到SharedPreferences
    public static void savaShare(String key, String value, String name, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(name, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    //清除SharedPreference数据
    public static void cleanSharedPreference(Context context) {
        deleteFilesByDirectory(new File("/data/data/"
                + context.getPackageName() + "/shared_prefs"));
    }

    //清除数据库的数据
    public static void cleanDatabases(Context context) {
        deleteFilesByDirectory(new File("/data/data/"
                + context.getPackageName() + "/databases"));
    }

    //删除文件
    private static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }

    public static boolean isNull(String[] str) {
        for (int i = 0; i < str.length; i++) {
            if (str[i] == null || str[i].equals(""))
                return false;
        }
        return true;
    }


    public static String getNowTime() {
        String time;
        Date now = new Date();
        SimpleDateFormat df = new SimpleDateFormat("MM月dd日");
        time = df.format(now);
        return time;
    }


    public static String getNowTimeE() {
        String time;
        Date now = new Date();
        SimpleDateFormat df = new SimpleDateFormat("MMddHHmmss");
        time = df.format(now);
        return time;
    }


    public static String getNextTime() {
        String time;
        Date next = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(next);
        calendar.add(calendar.DATE, 1);//把日期往后增加一天.整数往后推,负数往前移动
        next = calendar.getTime(); //这个时间就是日期往后推一天的结果
        SimpleDateFormat df = new SimpleDateFormat("MM月dd日");
        time = df.format(next);
        return time;
    }

    public static String getIntText(double a) {
        int b = (int) a;
        if (b == a) {
            return b + "";
        } else {
            return a + "";
        }
    }

    public static String String2Time(String times) {
        if (times == null)
            return "";

        Timestamp ts;
        try {
            if (times.length() == 13)
                ts = new Timestamp(Long.parseLong(times));
            else
                ts = new Timestamp(Long.parseLong(times + "000"));
        } catch (NumberFormatException e) {
            return null;
        }

        String tsStr = "";
        DateFormat sdf = new SimpleDateFormat("MM月dd日");
        tsStr = sdf.format(ts);
        return tsStr;
    }


    public static int dp2px(Context context, int dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }


}
