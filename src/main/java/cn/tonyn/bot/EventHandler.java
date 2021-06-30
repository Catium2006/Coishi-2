package cn.tonyn.bot;


import cn.hutool.core.util.StrUtil;
import cn.tonyn.coishi.Main;
import cn.tonyn.util.FakeAI;
import cn.tonyn.util.ImagesTool;
import cn.tonyn.util.Log;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Random;

import cn.tonyn.file.TextFile;
import cn.tonyn.net.Download;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MemberJoinEvent;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.ExternalResource;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;

import javax.swing.*;

public class EventHandler {
    Bot MyBot;
    long lastGroup;
    public EventHandler(Bot bot) {
        MyBot=bot;
    }
    String msg1="";
    String msg2="";
    public void GrpMsg(GroupMessageEvent event) {
        String msg=event.getMessage().contentToString();
        long FromGroup =event.getGroup().getId();
        long Sender=event.getSender().getId();
        Log.msg(event.getSender().getNameCard()+":"+msg, "Group", FromGroup);

        msg2=msg1;
        msg1=msg;
        Image img=null;
        if(msg.contains("[图片]")){
            //分解含图片句,超过1个则忽略
            String temp=msg;
            temp=temp.replace("[图片]","");
            if(!temp.contains("[图片]")){
                //不含第二个图片则处理,否则仍按照普通消息
                img=event.getMessage().get(Image.Key);
                Download.download(Image.queryUrl(img),"data/pictures/specifics/"+img.getImageId());
                msg1=msg1.replace(
                        "[图片]",
                        ("$"+
                                img.getImageId()+"$"
                        )
                );
            }

        }
        FakeAI.record(msg2,msg1,""+Sender);
        if(
                FakeAI.getAnswer(msg1)!=null && ProcessingLevel.get(event.getGroup())>0 && (!msg.contains("#"))&&(!msg.contains("*"))
        ){
            Log.write("Reading something from FakeAI...");
            //判断消息非空 处理级别 无特殊指令(非法字符)
            String s=FakeAI.getAnswer(msg1);
            System.out.println(s);
            if(s.contains("$")){
                //有没有图片
                Log.write("Msg Contains Image");
                //注意只有半个括号
                String id=s.substring(s.indexOf("{"),s.indexOf("}")+5);
                System.out.println(id);

                String _else=s.replace("$"+id,"").replace("$","");
                System.out.println(_else);

                Image img_s=buildImageById(id,event);

                MessageChainBuilder msb=new MessageChainBuilder();
                msb.add(_else);
                msb.add(img_s);
                event.getGroup().sendMessage(msb.asMessageChain());
                System.out.println("Send:"+s);

            }else{
                Log.write("Msg Not contains Image");
                event.getGroup().sendMessage(FakeAI.getAnswer(msg1));
            }



        }

        if(msg.contains("[图片]")){
            //保存所有图片
            Date d=new Date();
            String s="IMG"+d.getTime();
            String FilePath= "data/pictures/all/"+s+".jpg";
            Image image = event.getMessage().get(Image.Key);
            String ImageId= StrUtil.removeAll(StrUtil.removeAll(image.getImageId(), "}"), "{");
            String fileUrl = Image.queryUrl(image);
            System.out.println("下载:"+image+" ("+fileUrl+")");
            Download.download(fileUrl,FilePath);
        }
        System.out.println("Processing Level: "+ProcessingLevel.get(event.getGroup()));
        if(ProcessingLevel.get(event.getGroup())>0) {
            System.out.println("Handling this msg...");
            if(msg.startsWith("*")) {
                //命令
                msg=msg.replace("*", "");
                System.out.println("指令:"+msg);
                //随机图片
                if(msg.contains("随机图片")){
                    //先得到图库总数
                    File folder = new File("data/pictures/random");
                    File []list = folder.listFiles();
                    int fileCount = 0;
                    long length = 0;
                    for (File file : list){
                        if (file.isFile()){
                            fileCount++;
                            length += file.length();
                        }
                    }
                    if(fileCount!=0) {
                        //得到一张随机图片
                        Random r = new Random();
                        int i=r.nextInt(fileCount);
                        File file =new File("data/pictures/random/image"+i+".jpg");
                        // 上传一个图片并得到 Image 类型的 Message
                        Image image=event.getGroup().uploadImage(ExternalResource.create(file));
                        event.getGroup().sendMessage(image); // 发送图片
                    }else {
                        event.getGroup().sendMessage("无图片"); // 发送图片
                    }
                }
                if(msg.startsWith("图库添加 ")){
                    //获取图片数
                    File folder = new File("data/pictures/random");
                    File []list = folder.listFiles();
                    int fileCount = 0;
                    long length = 0;
                    for (File file : list){
                        if (file.isFile()){
                            fileCount++;
                            length += file.length();
                        }
                    }
                    int i=fileCount;
                    String FilePath= "data/pictures/random/image"+i+".jpg";
                    //经测试下载到的图片可能会出现“未经允许不得使用”的问题
                    Image image = event.getMessage().get(Image.Key);
                    String ImageId= StrUtil.removeAll(StrUtil.removeAll(image.getImageId(), "}"), "{");
                    String fileUrl = Image.queryUrl(image);
                    Log.write("下载:"+image+" ("+fileUrl+")");
                    Download.download(fileUrl,FilePath);
                    event.getGroup().sendMessage("保存成功");
                }

                if(msg.equals("地图")){
                    String host="http://mc.mcus.work:8123";
                    String[][] urls=new String[9][9];
                    BufferedImage[][] BI=new BufferedImage[9][9];

                    int x=-32;
                    int y=-32;
                    int lx=0;
                    int ly=0;

                    while(lx<9){
                        while(ly<9){
                            urls[lx][ly]=host+"/tiles/world/flat/-1_1/zzz_"+x+"_"+y+".png";
                            System.out.println(urls[lx][ly]);

                            BufferedImage bi=ImagesTool.getBufferedImageFromUrl(urls[lx][ly]);
                            BI[lx][ly]=bi;

                            if(y<=32){
                                y+=8;
                            }else{
                                y=0;
                            }
                            ly++;
                        }

                        x+=8;
                        y=-32;
                        lx++;
                        ly=0;
                    }

                    BufferedImage BIO=null;

                    int i=8;
                    while (i>=0){
                        BufferedImage BIO0=null;
                        try {
                            BIO0=ImagesTool.mergeImage(BI[0][i],BI[1][i],true);
                            BIO0=ImagesTool.mergeImage(BIO0,BI[2][i],true);
                            BIO0=ImagesTool.mergeImage(BIO0,BI[3][i],true);
                            BIO0=ImagesTool.mergeImage(BIO0,BI[4][i],true);
                            BIO0=ImagesTool.mergeImage(BIO0,BI[5][i],true);
                            BIO0=ImagesTool.mergeImage(BIO0,BI[6][i],true);
                            BIO0=ImagesTool.mergeImage(BIO0,BI[7][i],true);
                            BIO0=ImagesTool.mergeImage(BIO0,BI[8][i],true);

                            if(BIO!=null){
                                BIO=ImagesTool.mergeImage(BIO,BIO0,false);
                            }else{
                                BIO=BIO0;
                            }
                            i--;
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }

                    ImagesTool.SaveImageFile(BIO,"data/cache/pictures/maps/map.png");

                    File f=new File("data/cache/pictures/maps/map.png");
                    Image image=event.getGroup().uploadImage(ExternalResource.create(f));
                    event.getGroup().sendMessage(image); // 发送图片


                }
            }
        }


        //如果是超级管理员
        if(msg.startsWith("#")) {



            if((ProcessingLevel.get(MyBot.getFriend(event.getSender().getId()))==20)) {
                msg=msg.replace("#", "");
                if(msg.equals("botoff")) {
                    event.getGroup().sendMessage("收到关闭指令,1s后退出");
                    MyBot.getFriend(148125778).sendMessage(event.getSenderName()+"执行了关闭操作");
                    exit();
                }
                if(msg.equals("debug")) {
                    Main.debug=!Main.debug;
                    if(Main.debug) {
                        event.getGroup().sendMessage("Debug模式已开启");
                        MyBot.getFriend(148125778).sendMessage(event.getSenderName()+"开启了Debug模式");
                        Log.write("Debug模式已开启","Debug");
                    }else {
                        event.getGroup().sendMessage("Debug模式已关闭");
                        MyBot.getFriend(148125778).sendMessage(event.getSenderName()+"关闭了Debug模式");
                        Log.write("Debug模式已关闭","Debug");
                    }
                }
                if(msg.contains("set")) {
                    //第一段是set 第二段是type 第三段是Id 第四段是level
                    //比如: set f 1234567890 20;set g 1234567890 3
                    String[] SET=msg.split(" ",4);
                    String type=SET[1];
                    int level= Integer.valueOf(SET[3]).intValue();
                    if(type.equals("g")) {
                        Group group=MyBot.getGroupOrFail(Long.parseLong(SET[2]));
                        if(level==2) {
                            TextFile.Empty("data/config/MCGroup.txt");
                            TextFile.Write("data/config/MCGroup.txt", SET[2]);
                        }
                        ProcessingLevel.set(group, level);
                        event.getGroup().sendMessage("设置成功");
                    }
                    if(type.equals("f")) {
                        long friendId=Long.parseLong(SET[2]);
                        ProcessingLevel.set(MyBot.getFriend(friendId), level);
                        event.getGroup().sendMessage("设置成功");
                    }

                }
                if(msg.equals("info")||msg.equals("信息")){
                    String send;

                    SystemInfo si = new SystemInfo();
                    HardwareAbstractionLayer hal = si.getHardware();
                    OperatingSystem os = si.getOperatingSystem();

                    double Mt=hal.getMemory().getTotal()/(1024*1024*1024);
                    double Mu=(hal.getMemory().getAvailable()-Mt)/(1024*1024*1024);//Bytes to GBytes
                    long uptime=((new Date()).getTime()-Main.StartTime)/60000;//ms to seconds
                    send="Coishi running on:"+os+"\r\nCPU:"+hal.getProcessor()+"\r\nMemory:"+Mu+"GB/"+Mt+"GB\r\nUptime:"+uptime+"min";
                    event.getGroup().sendMessage(send) ;
                    System.out.println(send);
                }
            }else {
                event.getGroup().sendMessage(msg+":permission denied");
            }
        }


    }

    public void FrdMsg(FriendMessageEvent event) {
        String msg=event.getMessage().contentToString();
        long Sender = event.getFriend().getId();
        Log.msg(msg, "Friend", Sender);
        if(msg.contains("[图片]")){
            //获取图片数
            Date d=new Date();
            String s="IMG"+d.getTime();
            String FilePath= "data/pictures/all/"+s+".jpg";
            //经测试下载到的图片可能会出现“未经允许不得使用”的问题
            Image image = event.getMessage().get(Image.Key);
            String ImageId= StrUtil.removeAll(StrUtil.removeAll(image.getImageId(), "}"), "{");
            String fileUrl = Image.queryUrl(image);
            Log.write("下载:"+image+" ("+fileUrl+")");
            Download.download(fileUrl,FilePath);
        }
        if(Main.debug) {
            Log.write("收到好友消息:"+msg);
        }

        //发送者是管理员
        if(ProcessingLevel.get(event.getFriend())==20) {
            if(msg.equals("BotOff")) {
                event.getFriend().sendMessage("收到关闭指令,1s后执行退出进程");
                MyBot.getFriend(148125778).sendMessage(event.getSenderName()+"执行了关闭任务");
                exit();
            }
            if(msg.equals("Debug")) {
                Main.debug=!Main.debug;
                if(Main.debug) {
                    event.getFriend().sendMessage("Debug模式已开启");
                    MyBot.getFriend(148125778).sendMessage(event.getSenderName()+"开启了Debug模式");
                    Log.write("Debug模式已开启","Debug");
                }else {
                    event.getFriend().sendMessage("Debug模式已关闭");
                    MyBot.getFriend(148125778).sendMessage(event.getSenderName()+"关闭了Debug模式");
                    Log.write("Debug模式已关闭","Debug");
                }
            }
            if(msg.contains("set")) {
                //第一段是set 第二段是type 第三段是Id 第四段是level
                //比如: set f 1234567890 20;set g 1234567890 3
                String[] SET=msg.split(" ",4);
                String type=SET[1];
                int level= Integer.valueOf(SET[3]).intValue();
                if(type.equals("g")) {
                    if(level==2) {
                        TextFile.Empty("data/config/MCGroup.txt");
                        TextFile.Write("data/config/MCGroup.txt", SET[2]);
                    }
                    Group group=MyBot.getGroupOrFail(Long.parseLong(SET[2]));
                    ProcessingLevel.set(group, level);
                    event.getFriend().sendMessage("设置成功");
                }
                if(type.equals("f")) {
                    long friendId=Long.parseLong(SET[2]);
                    ProcessingLevel.set(MyBot.getFriend(friendId), level);
                    event.getFriend().sendMessage("设置成功");
                }

            }


        }



    }

    public void MemberJoin(MemberJoinEvent event) {
        long FromGroup=event.getGroup().getId();
        if(ProcessingLevel.get(event.getGroup())>0) {

        }
    }





    static void exit() {
        Log.write("收到关闭指令,执行退出方法","Debug");
        try {
            Thread.sleep(1000); //1000 毫秒，也就是1秒.
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        System.exit(0);
    }
    Image buildImageById(String id,GroupMessageEvent event){
        //通用png文件后缀
        File f=new File("data/pictures/specifics/"+id);
        if(f.isFile()){
            ExternalResource er=ExternalResource.create(f);
            Image img=event.getGroup().uploadImage(er);
            return img;
        }else{
            ExternalResource er=ExternalResource.create(new File("data/pictures/FileNotFound.png"));
            Image img=event.getGroup().uploadImage(er);
            return img;
        }

    }
}
