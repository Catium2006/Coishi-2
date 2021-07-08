# Coishi 2
重新写的JVM QQ机器人  
## About
嘛 之前的安卓版时间太长就弃坑了,主要是运行效果不理想,现在转到普通的JVM平台了,也可以实现更多功能.  
咱的gradle有亿点问题,导致Mirai包拉不下来,只能加进libs目录了,下载尺寸大别怪我....  
由于某些原因,导入IDEA后的项目名是Coishi_with_utf-8,强迫症的看改一下  
 [备用仓库](http://www.tonyn.cn:10000/git/Coishi-2)(不定期更新)  
 [Jar下载](http://www.tonyn.cn:10000/download/Coishi_with_utf-8.jar)(不定期更新)  
以上两个链接不一定有效,试一下吧,毕竟姿势水平不高,开不起云服务器  
## 用法
1.克隆仓库  
2.导入IDEA gradle项目  
3.Build -> Build Artifics -> Coishi_with_utf-8.jar -> Build  
4.运行仓库根目录下的run.bat,自动复制出Jar文件并运行-----(~~不会真的有Java开发者不装Java运行环境吧~~)  
## 问题
暑假期间我会维护这个项目,有大问题请提交issues.  
如果不是大问题的话还是不要提交了,提升一下你们的姿势水平,不能听见风就是雨,报错了就不能运行咯?  
~~代码垃圾的不行,还是too young too simple!~~  
## 指令列表
\*随机图片  
\*图库添加\[图片]  
//设置处理级别  
\#set \[g/f] \[number] \[level]  
//关闭机器人  
\#botoff  
//开启或关闭debug  
\#debug  
//获取信息
\#info  
## 功能列表
1.下载所有图片  
2.记录上下文回复,并且能包括特定图片  
3.不太完善的日志  
## 鸣谢
使用了Mamoe开发Mirai框架  
空白姐姐提供了广泛指导  
