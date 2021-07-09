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
    /*���
     *
     * */

    public static void main(String[] args) {
        //��ʼ��
        StartUp();

        //��ȡ��¼��Ϣ
        String json;
        File f=new File("login.json");
        if(f.isFile()){
            json=TextFile.Read("login.json");
        }else{
            Log.write("û�е�¼�˻������ļ�!(login.json)");
            json=null;
            TextFile.Write(f,"{\"QQNumber\": \"0000000000\", \"Password\": \"example*#*#\"}");
            System.exit(1);
        }

        //��¼��Ϣ����json����
        JSONObject obj = JSON.parseObject(json);

        long QQnum=obj.getLong("QQNumber");
        Swapper.BotNumber=QQnum;
        String pwds=obj.getString("Password");
        Log.write("��ǰ��¼�˺�:"+QQnum+"����:"+pwds);
        (new File("data/msg/"+QQnum)).mkdirs();

        //��¼
        Bot bot = BotFactory.INSTANCE.newBot(QQnum, pwds, new BotConfiguration() {{
            // ����
            setProtocol(MiraiProtocol.ANDROID_PAD);
            //device�ļ����ڸ�Ŀ¼
            fileBasedDeviceInfo(QQnum+".json");
        }});

        bot.login();
        Log.write("====��¼�ɹ�====");

        //��ú���
        bot.getFriends().forEach(friend -> {
            if(!new File("data/config/friends/"+friend.getId()+".txt").isFile()) {
                ProcessingLevel.set(friend, 0);
            }

        });

        //���Ⱥ
        bot.getGroups().forEach(group -> {
            if(!new File("data/config/groups/"+group.getId()+".txt").isFile()) {
                ProcessingLevel.set(group, 0);
            }

        });

        //EventHandler
        EventHandler evethandler=new EventHandler(bot);


        //Ⱥ��Ϣ�¼�
        Listener GroupMsg = GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, event -> {
            //����EventHandle
            evethandler.GrpMsg(event);

        });

        //������Ϣ�¼�
        Listener FriendMsg = GlobalEventChannel.INSTANCE.subscribeAlways(FriendMessageEvent.class, event->{
            //����EventHandle
            evethandler.FrdMsg(event);

        });
        //ȺԱ�����¼�
        Listener MemberJoin = GlobalEventChannel.INSTANCE.subscribeAlways(MemberJoinEvent.class, event ->{
            //����EventHandle
            evethandler.MemberJoin(event);
        });

        bot.join(); // ������ǰ�߳�ֱ�� bot ����
    }

    static void StartUp() {

        Date date = new Date();
        Swapper.StartTime=date.getTime();
        //������Ҫ�ļ���Ŀ¼
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