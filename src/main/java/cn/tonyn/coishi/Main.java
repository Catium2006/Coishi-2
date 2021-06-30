package cn.tonyn.coishi;



import cn.tonyn.bot.ProcessingLevel;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.Listener;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MemberJoinEvent;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.utils.BotConfiguration;
import java.io.File;
import java.util.Date;
import cn.tonyn.bot.EventHandler;
import cn.tonyn.util.Log;
import cn.tonyn.file.TextFile;

public class Main {
    /*这个类是程序的主要部分
     *
     * */
    public static boolean debug=false;
    public static long StartTime=0;
    public static long BotNumber=0;
    public static void main(String[] args) {
        //初始化
        StartUp();

        //获取登录信息
        long QQnum=Long.valueOf(TextFile.Read("QQnum.txt")).longValue();
        BotNumber=QQnum;
        String pwds=TextFile.Read("pwd.txt");
        Log.write("当前登录账号:"+QQnum+"密码:"+pwds);
        (new File("data/msg/"+QQnum)).mkdirs();

        //登录
        Bot bot = BotFactory.INSTANCE.newBot(QQnum, pwds, new BotConfiguration() {{
            // 配置
            setProtocol(MiraiProtocol.ANDROID_PAD);
            fileBasedDeviceInfo("data/config/devices/"+QQnum+".json");
        }});

        bot.login();
        Log.write("====登录成功====");

        //获得好友
        bot.getFriends().forEach(friend -> {
            if(!new File("data/config/friends/"+friend.getId()+".txt").isFile()) {
                ProcessingLevel.set(friend, 0);
            }

        });

        //获得群
        bot.getGroups().forEach(group -> {
            if(!new File("data/config/groups/"+group.getId()+".txt").isFile()) {
                ProcessingLevel.set(group, 0);
            }

        });

        //EventHandler
        EventHandler evethandler=new EventHandler(bot);


        //群消息事件
        Listener GroupMsg = GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, event -> {
            //交给EventHandle
            evethandler.GrpMsg(event);

        });

        //好友消息事件
        Listener FriendMsg = GlobalEventChannel.INSTANCE.subscribeAlways(FriendMessageEvent.class, event->{
            //交给EventHandle
            evethandler.FrdMsg(event);

        });
        //群员加入事件
        Listener MemberJoin = GlobalEventChannel.INSTANCE.subscribeAlways(MemberJoinEvent.class, event ->{
            //交给EventHandle
            evethandler.MemberJoin(event);
        });

        bot.join(); // 阻塞当前线程直到 bot 离线
    }

    public static void StartUp() {

        Date date = new Date();
        StartTime=date.getTime();
        //创建必要文件和目录
        (new File("data/log")).mkdirs();
        (new File("data/config/friends")).mkdirs();
        (new File("data/config/groups")).mkdirs();
        (new File("data/config/devices")).mkdirs();
        (new File("data/msg")).mkdirs();
        (new File("data/pictures/random")).mkdirs();
        (new File("data/pictures/specifics")).mkdirs();
        (new File("data/pictures/all")).mkdirs();
        (new File("data/FakeAI/records")).mkdirs();
        (new File("data/cache/pictures/maps")).mkdirs();

        Log.write("==========START==========");
        File e=new File("QQnum.txt");
        File f=new File("pwd.txt");
        if(!e.isFile()) {
            TextFile.Write(e, "请将此文件内容改为QQ号");
            Log.write("缺少QQ号文件(QQnum.txt)", "Error");
            System.exit(1);
        }
        if(!f.isFile()) {
            TextFile.Write(f, "请将此文件内容改为密码");
            Log.write("缺少密码文件(pwd.txt)", "Error");
            System.exit(1);
        }

    }

}