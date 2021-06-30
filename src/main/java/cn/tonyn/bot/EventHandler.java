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
        if(msg.contains("[ͼƬ]")){
            //�ֽ⺬ͼƬ��,����1�������
            String temp=msg;
            temp=temp.replace("[ͼƬ]","");
            if(!temp.contains("[ͼƬ]")){
                //�����ڶ���ͼƬ����,�����԰�����ͨ��Ϣ
                img=event.getMessage().get(Image.Key);
                Download.download(Image.queryUrl(img),"data/pictures/specifics/"+img.getImageId());
                msg1=msg1.replace(
                        "[ͼƬ]",
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
            //�ж���Ϣ�ǿ� ������ ������ָ��(�Ƿ��ַ�)
            String s=FakeAI.getAnswer(msg1);
            System.out.println(s);
            if(s.contains("$")){
                //��û��ͼƬ
                Log.write("Msg Contains Image");
                //ע��ֻ�а������
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

        if(msg.contains("[ͼƬ]")){
            //��������ͼƬ
            Date d=new Date();
            String s="IMG"+d.getTime();
            String FilePath= "data/pictures/all/"+s+".jpg";
            Image image = event.getMessage().get(Image.Key);
            String ImageId= StrUtil.removeAll(StrUtil.removeAll(image.getImageId(), "}"), "{");
            String fileUrl = Image.queryUrl(image);
            System.out.println("����:"+image+" ("+fileUrl+")");
            Download.download(fileUrl,FilePath);
        }
        System.out.println("Processing Level: "+ProcessingLevel.get(event.getGroup()));
        if(ProcessingLevel.get(event.getGroup())>0) {
            System.out.println("Handling this msg...");
            if(msg.startsWith("*")) {
                //����
                msg=msg.replace("*", "");
                System.out.println("ָ��:"+msg);
                //���ͼƬ
                if(msg.contains("���ͼƬ")){
                    //�ȵõ�ͼ������
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
                        //�õ�һ�����ͼƬ
                        Random r = new Random();
                        int i=r.nextInt(fileCount);
                        File file =new File("data/pictures/random/image"+i+".jpg");
                        // �ϴ�һ��ͼƬ���õ� Image ���͵� Message
                        Image image=event.getGroup().uploadImage(ExternalResource.create(file));
                        event.getGroup().sendMessage(image); // ����ͼƬ
                    }else {
                        event.getGroup().sendMessage("��ͼƬ"); // ����ͼƬ
                    }
                }
                if(msg.startsWith("ͼ����� ")){
                    //��ȡͼƬ��
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
                    //���������ص���ͼƬ���ܻ���֡�δ��������ʹ�á�������
                    Image image = event.getMessage().get(Image.Key);
                    String ImageId= StrUtil.removeAll(StrUtil.removeAll(image.getImageId(), "}"), "{");
                    String fileUrl = Image.queryUrl(image);
                    Log.write("����:"+image+" ("+fileUrl+")");
                    Download.download(fileUrl,FilePath);
                    event.getGroup().sendMessage("����ɹ�");
                }

                if(msg.equals("��ͼ")){
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
                    event.getGroup().sendMessage(image); // ����ͼƬ


                }
            }
        }


        //����ǳ�������Ա
        if(msg.startsWith("#")) {



            if((ProcessingLevel.get(MyBot.getFriend(event.getSender().getId()))==20)) {
                msg=msg.replace("#", "");
                if(msg.equals("botoff")) {
                    event.getGroup().sendMessage("�յ��ر�ָ��,1s���˳�");
                    MyBot.getFriend(148125778).sendMessage(event.getSenderName()+"ִ���˹رղ���");
                    exit();
                }
                if(msg.equals("debug")) {
                    Main.debug=!Main.debug;
                    if(Main.debug) {
                        event.getGroup().sendMessage("Debugģʽ�ѿ���");
                        MyBot.getFriend(148125778).sendMessage(event.getSenderName()+"������Debugģʽ");
                        Log.write("Debugģʽ�ѿ���","Debug");
                    }else {
                        event.getGroup().sendMessage("Debugģʽ�ѹر�");
                        MyBot.getFriend(148125778).sendMessage(event.getSenderName()+"�ر���Debugģʽ");
                        Log.write("Debugģʽ�ѹر�","Debug");
                    }
                }
                if(msg.contains("set")) {
                    //��һ����set �ڶ�����type ��������Id ���Ķ���level
                    //����: set f 1234567890 20;set g 1234567890 3
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
                        event.getGroup().sendMessage("���óɹ�");
                    }
                    if(type.equals("f")) {
                        long friendId=Long.parseLong(SET[2]);
                        ProcessingLevel.set(MyBot.getFriend(friendId), level);
                        event.getGroup().sendMessage("���óɹ�");
                    }

                }
                if(msg.equals("info")||msg.equals("��Ϣ")){
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
        if(msg.contains("[ͼƬ]")){
            //��ȡͼƬ��
            Date d=new Date();
            String s="IMG"+d.getTime();
            String FilePath= "data/pictures/all/"+s+".jpg";
            //���������ص���ͼƬ���ܻ���֡�δ��������ʹ�á�������
            Image image = event.getMessage().get(Image.Key);
            String ImageId= StrUtil.removeAll(StrUtil.removeAll(image.getImageId(), "}"), "{");
            String fileUrl = Image.queryUrl(image);
            Log.write("����:"+image+" ("+fileUrl+")");
            Download.download(fileUrl,FilePath);
        }
        if(Main.debug) {
            Log.write("�յ�������Ϣ:"+msg);
        }

        //�������ǹ���Ա
        if(ProcessingLevel.get(event.getFriend())==20) {
            if(msg.equals("BotOff")) {
                event.getFriend().sendMessage("�յ��ر�ָ��,1s��ִ���˳�����");
                MyBot.getFriend(148125778).sendMessage(event.getSenderName()+"ִ���˹ر�����");
                exit();
            }
            if(msg.equals("Debug")) {
                Main.debug=!Main.debug;
                if(Main.debug) {
                    event.getFriend().sendMessage("Debugģʽ�ѿ���");
                    MyBot.getFriend(148125778).sendMessage(event.getSenderName()+"������Debugģʽ");
                    Log.write("Debugģʽ�ѿ���","Debug");
                }else {
                    event.getFriend().sendMessage("Debugģʽ�ѹر�");
                    MyBot.getFriend(148125778).sendMessage(event.getSenderName()+"�ر���Debugģʽ");
                    Log.write("Debugģʽ�ѹر�","Debug");
                }
            }
            if(msg.contains("set")) {
                //��һ����set �ڶ�����type ��������Id ���Ķ���level
                //����: set f 1234567890 20;set g 1234567890 3
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
                    event.getFriend().sendMessage("���óɹ�");
                }
                if(type.equals("f")) {
                    long friendId=Long.parseLong(SET[2]);
                    ProcessingLevel.set(MyBot.getFriend(friendId), level);
                    event.getFriend().sendMessage("���óɹ�");
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
        Log.write("�յ��ر�ָ��,ִ���˳�����","Debug");
        try {
            Thread.sleep(1000); //1000 ���룬Ҳ����1��.
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        System.exit(0);
    }
    Image buildImageById(String id,GroupMessageEvent event){
        //ͨ��png�ļ���׺
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
