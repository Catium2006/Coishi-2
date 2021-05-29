package cn.tonyn.util;

import cn.tonyn.coishi.Main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class Log {
    public StringBuilder sb;
    public static void write(String event) {
        //日志
        event="[Info] "+event;
        File log = new File("data/log/Info.log");
        Date date = new Date();
        SimpleDateFormat bjSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");     // 北京
        bjSdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));  // 设置北京时区

        try {
            event=bjSdf.format(date)+"/"+event+System.getProperty("line.separator");
            System.out.print(event);

            FileWriter writer = null;
            writer = new FileWriter(log , true);
            BufferedWriter out = new BufferedWriter(writer);
            out.write(event);
            out.flush();
            out.close();
        } catch (IOException e) {
            System.out.println("Error:"+e.getMessage());
            e.printStackTrace();
        }
    }
    public static void write(String event,String label) {
        event="["+label+"] "+event;
        File log = new File("data/log/"+label+".log");
        Date date = new Date();
        SimpleDateFormat bjSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");     // 北京
        bjSdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));  // 设置北京时区
        try {
            event=bjSdf.format(date)+"/"+event+System.getProperty("line.separator");
            System.out.print(event);
            FileWriter writer = null;
            writer = new FileWriter(log , true);
            BufferedWriter out = new BufferedWriter(writer);
            out.write(event);
            out.flush();
            out.close();
        } catch (IOException e) {
            System.out.println("Error:"+e.getMessage());
            e.printStackTrace();
        }
    }
    public static void msg(String msg,String label,long number) {
        File f=new File("data/msg/"+ Main.BotNumber+"/"+label+"("+number+").txt");
        FileWriter writer = null;
        SimpleDateFormat bjSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");     // 北京
        bjSdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));  // 设置北京时区
        msg=bjSdf.format(new Date())+"/"+msg+System.getProperty("line.separator");
        try {
            writer = new FileWriter(f , true);
            BufferedWriter out = new BufferedWriter(writer);
            out.write(msg);
            out.flush();
            out.close();
        }catch(IOException e){
            e.printStackTrace();
        }


    }
}
