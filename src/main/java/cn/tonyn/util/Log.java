package cn.tonyn.util;

import cn.tonyn.coishi.Main;
import cn.tonyn.coishi.Swapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class Log {
    public StringBuilder sb;
    public static int MAXSIZE=512;
    public static void write(String event) {
        //日志
        event="[Info] "+event;
        Swapper.LOG=Swapper.LOG.substring(Swapper.LOG.length()-MAXSIZE);
        Swapper.LOG=Swapper.LOG+event+"\r\n";

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
        Swapper.LOG=Swapper.LOG.substring(Swapper.LOG.length()-MAXSIZE);
        Swapper.LOG=Swapper.LOG+event+"\r\n";

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
        File f=new File("data/msg/"+ Swapper.BotNumber+"/"+label+"("+number+").txt");
        Swapper.MSG=Swapper.MSG.substring(Swapper.LOG.length()-MAXSIZE);
        Swapper.MSG=Swapper.MSG+label+number+":"+msg+"\r\n";
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
