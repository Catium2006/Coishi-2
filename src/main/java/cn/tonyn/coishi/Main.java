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
    /*������ǳ������Ҫ����
     *
     * */
    public static boolean debug=false;
    public static long StartTime=0;
    public static long BotNumber=0;
    public static void main(String[] args) {
        //��ʼ��
        StartUp();

        //��ȡ��¼��Ϣ
        long QQnum=Long.valueOf(TextFile.Read("QQnum.txt")).longValue();
        BotNumber=QQnum;
        String pwds=TextFile.Read("pwd.txt");
        Log.write("��ǰ��¼�˺�:"+QQnum+"����:"+pwds);
        (new File("data/msg/"+QQnum)).mkdirs();

        //��¼
        Bot bot = BotFactory.INSTANCE.newBot(QQnum, pwds, new BotConfiguration() {{
            // ����
            setProtocol(MiraiProtocol.ANDROID_PAD);
            fileBasedDeviceInfo("data/config/devices/"+QQnum+".json");
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

    public static void StartUp() {

        Date date = new Date();
        StartTime=date.getTime();
        //������Ҫ�ļ���Ŀ¼
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
            TextFile.Write(e, "�뽫���ļ����ݸ�ΪQQ��");
            Log.write("ȱ��QQ���ļ�(QQnum.txt)", "Error");
            System.exit(1);
        }
        if(!f.isFile()) {
            TextFile.Write(f, "�뽫���ļ����ݸ�Ϊ����");
            Log.write("ȱ�������ļ�(pwd.txt)", "Error");
            System.exit(1);
        }

    }

}