package com.log.myloglibrary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MyLogReveiver extends BroadcastReceiver {
    public static String OUT_ACTION = "com.example.myapplication.OUT_ACTION";
    public String outAction = null;
    private StringBuffer newLogContent = null;

    @Override
    public void onReceive(Context context, Intent intent){
        outAction = intent.getAction();
        if(OUT_ACTION.equals(outAction)){
            begin();
         }
    }

    public void begin(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Process pro = null;
                BufferedReader bufferedReader = null;
                String fileName = "data/" + new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime()) + ".log";
                try {
                    String[] running=new String[]{ "logcat","-v","time","-s", "ReactNativeJS | ScreenModeService:I *:S" };
                    pro = Runtime.getRuntime().exec(running);
                    bufferedReader = new BufferedReader(new InputStreamReader(pro.getInputStream()));
                    Runtime.getRuntime().exec("logcat -c").waitFor();


                    //筛选需要的字串
                    String line = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        if (line.contains("PushAlarmActivity") || line.contains("ReactNativeJS")){
                            //读出每行log信息
                            newLogContent = new StringBuffer(line + "\n");
//                            System.out.println(newLogContent.toString());
                            if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                                //读取路径
                                File sdFile = Environment.getExternalStorageDirectory();
                                //拼接文件名
                                File path = new File(sdFile,fileName);
//                                System.out.println(path);
                                try {
                                    //保存收集到的日志
                                    OutputStreamWriter oStreamWriter = new OutputStreamWriter(new FileOutputStream(path,true), "gb2312");
                                    oStreamWriter.append(newLogContent.toString());
                                    oStreamWriter.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            Thread.yield();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e1){
                    e1.printStackTrace();
                }
            }
        }).start();
    }
}
