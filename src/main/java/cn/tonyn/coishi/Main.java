package cn.tonyn.coishi;



import cn.tonyn.bot.ProcessingLevel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
    /*入口
     *
     * */

    public static void main(String[] args) {
        //初始化
        StartUp();

        //获取登录信息
        String json;
        File f=new File("login.json");
        if(f.isFile()){
            json=TextFile.Read("login.json");
        }else{
            Log.write("没有登录账户密码文件!(login.json)");
            json=null;
            TextFile.Write(f,"{\"QQNumber\": \"0000000000\", \"Password\": \"example*#*#\"}");
            System.exit(1);
        }

        //登录信息改用json储存
        JSONObject obj = JSON.parseObject(json);

        long QQnum=obj.getLong("QQNumber");
        Swapper.BotNumber=QQnum;
        String pwds=obj.getString("Password");
        Log.write("当前登录账号:"+QQnum+"密码:"+pwds);
        (new File("data/msg/"+QQnum)).mkdirs();

        //登录
        Bot bot = BotFactory.INSTANCE.newBot(QQnum, pwds, new BotConfiguration() {{
            // 配置
            setProtocol(MiraiProtocol.ANDROID_PAD);
            //device文件放在根目录
            fileBasedDeviceInfo(QQnum+".json");
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

    static void StartUp() {

        Date date = new Date();
        Swapper.StartTime=date.getTime();
        //创建必要文件和目录
        (new File("data/log")).mkdirs();
        (new File("data/config/friends")).mkdirs();
        (new File("data/config/groups")).mkdirs();
        (new File("data/msg")).mkdirs();
        (new File("data/pictures/random")).mkdirs();
        (new File("data/pictures/specifics")).mkdirs();
        (new File("data/pictures/all")).mkdirs();
        (new File("data/FakeAI/records")).mkdirs();
        (new File("data/cache/pictures/maps")).mkdirs();

        Log.write("==========START==========");

    }



}