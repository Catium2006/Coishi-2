package cn.tonyn.bot;


import java.io.File;

import cn.tonyn.util.Log;
import cn.tonyn.file.TextFile;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;

public class ProcessingLevel {

    /*这个类是事件处理级别(权限),决定处理事件的方式,以int形式返回,高级拥有低级权限,负数单独处理
     * 0	默认 不处理
     * 1	普通私/群消息
     * 2	服务器群
     * 20	超级管理员
     * */
    static public void set(Group group,int level) {
        long groupl=group.getId();
        TextFile.Empty("data/config/groups/"+groupl+".txt");
        TextFile.Write("data/config/groups/"+groupl+".txt", ""+level);
        Log.write("设置了群"+groupl+"处理级为"+level);
    }
    static public void set(Friend friend,int level) {
        long friendl=friend.getId();
        TextFile.Empty("data/config/friends/"+friendl+".txt");
        TextFile.Write("data/config/friends/"+friendl+".txt", ""+level);
        Log.write("设置了好友"+friendl+"处理级为"+level);
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
