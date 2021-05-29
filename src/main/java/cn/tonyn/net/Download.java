package cn.tonyn.net;


import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.StreamProgress;
import cn.hutool.core.lang.Console;
import cn.hutool.http.HttpUtil;

public class Download{
    /*
    作者: kongbai

     */
    public static InputStream getInputStreamFromUrl(String ImageUrl) {
        InputStream inputStream = null;
        try{
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(ImageUrl).openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.150 Safari/537.36");
            httpURLConnection.setRequestProperty("Accept-Encoding", "gzip");
            httpURLConnection.setRequestProperty("Referer","no-referrer");
            httpURLConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setReadTimeout(10000);
            inputStream = httpURLConnection.getInputStream();
        }catch (IOException e){
            e.printStackTrace();
        }
        return inputStream;
    }



    public static boolean StreamToFile(InputStream inputStream, String path){
        boolean flag = true;
        File file = new File(path);
        if (file.exists()){
            return flag;
        }
        File fileParent = file.getParentFile();
        if (!fileParent.exists()){
            fileParent.mkdirs();//创建路径
        }
        try {
            OutputStream out=new FileOutputStream(path);
            int temp = 0;
            // 开始拷贝
            while ((temp = inputStream.read()) != -1) {
                // 边读边写
                out.write(temp);
            }
        }catch (Exception e) {
            e.printStackTrace();
            flag = false;
        }
        return flag;
    }

    public static void download(String fileUrl,String filepath) {
        // 带进度显示的文件下载
        HttpUtil.downloadFile(fileUrl, FileUtil.file(filepath), new StreamProgress() {

            @Override
            public void start() {
                Console.log("开始下载...");
            }

            @Override
            public void progress(long progressSize) {
                Console.log("已下载:{}", FileUtil.readableFileSize(progressSize));
            }

            @Override
            public void finish() {
                Console.log("下载完成!");
            }
        });
    }

}