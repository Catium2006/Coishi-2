package cn.tonyn.bot;


import java.io.File;

import cn.tonyn.util.Log;
import cn.tonyn.file.TextFile;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;

public class ProcessingLevel {

    /*��������¼�������(Ȩ��),���������¼��ķ�ʽ,��int��ʽ����,�߼�ӵ�еͼ�Ȩ��,������������
     * 0	Ĭ�� ������
     * 1	��ͨ˽/Ⱥ��Ϣ
     * 2	������Ⱥ
     * 20	��������Ա
     * */
    static public void set(Group group,int level) {
        long groupl=group.getId();
        TextFile.Empty("data/config/groups/"+groupl+".txt");
        TextFile.Write("data/config/groups/"+groupl+".txt", ""+level);
        Log.write("������Ⱥ"+groupl+"����Ϊ"+level);
    }
    static public void set(Friend friend,int level) {
        long friendl=friend.getId();
        TextFile.Empty("data/config/friends/"+friendl+".txt");
        TextFile.Write("data/config/friends/"+friendl+".txt", ""+level);
        Log.write("�����˺���"+friendl+"����Ϊ"+level);
    }


    static public int get(Group group) {
        long groupl=group.getId();
        File f=new File("data/config/groups/"+groupl+".txt");
        if(!f.isFile()) {
            set(group,0);
        }
        int i = Integer.valueOf(TextFile.Read("data/config/groups/"+groupl+".txt")).intValue();
        return i;
    }
    static public int get(Friend friend) {
        long friendl=friend.getId();
        File f=new File("data/config/friends/"+friendl+".txt");
        if(!f.isFile()) {
            set(friend,0);
        }
        int i = Integer.valueOf(TextFile.Read("data/config/friends/"+friendl+".txt")).intValue();
        return i;
    }
}
