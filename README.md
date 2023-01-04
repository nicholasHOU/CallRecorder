# 国美APP & 帮帮APP & 极简APP

国美APP、帮帮APP、极简APP工作空间，采用组件化方式，通过module、AAR进行项目构建

## 娱乐化项目开发说明

一、更新wordspace，本地配置还原！还原！

二、apkfly.py serv-update -a -g liveSDk0930

确保没报错！执行下一步

三、apkfly.py deploy -app

确保没报错！执行下一步

四、编译出包成功

> support by zhaolei，maxinliang

## 工程化管理工具使用说明

Android 开发团队以国美、来购App经验，整理一套相对标准化的App开发流程，目的为简化开发人员的检码及项目管理，只需命令即可管理后期所有模块代码，简单，方便。

#### 正文

##### 一. 命令行 获取 ***GWorkspace***

GWorkspace为GomePlus的工作空间，包含工程依赖组件的版本，及其项目管理工具

~~~bash
~ git clone git@code.gome.inc:mobile-android/GWorkspace.git -b [分支名称] [检出路径]
~~~

> -b 可选参数后面跟 ***分支名称***
>
> [检出路径] 可选，默认为 GWorkspace


###### 二. 工程管理工具 ***apkfly.py***

项目管理工具由两个文件构成 apkfly.py + projects.xml

> * apkfly.py 项目管理工具脚本文件
> * projects.xml 定义项目结构

##### 1. projects.xml

xml 内容如下

~~~xml
<?xml version="1.0" encoding="UTF-8"?>
<manifest host="git@xxx.com.cn:" branch="xxbranch">
    <!--私有工程-->
    <project url="git@xxx.com.cn:xxx/xxx1.git" branch="master" groups="private"/>
    <project url="git@xxx.com.cn:xxx/xxx2.git" groups="private"/>
    <project url="git@xxx.com.cn:xxx/xxx3.git" groups="private"/>
    ......
    <!--G工程-->
    <project url="git@xxx.com.cn:xxx1/xxx3.git"/>
    ......
</manifest>
~~~

manifest 节点

* host: 基础git地址 host
* branch: 项目默认分支

project 子节点

* url: git地址【如果配置了manifest节点的host属性，地址可以忽略host】
* branch: 项目分支
* group: 所属组
* path: 检出项目保存本地路径【暂只支持一级目录】
* app: 是否为application项目


##### 2. apkfly.py 命令行的使用

apkfly.py 脚本执行

~~~bash
# window
~ python apkfly.py
~~~

~~~bash
# Mac / Linux
~ ./apyfly.py    // sudo chmod +x apkfly.py
# or
~ python apkfly.py
~~~

查看apkfly.py帮助信息

~~~bash
➜  GWorkPR git:(mergeDev) ✗ ./apkfly.py -h
usage: apkfly [-h]
              {setting,pushprop,version,ar,upload,deps,pull,reset,clone,branch,tag,serv-update}
              ...

workspace帮助工具

optional arguments:
  -h, --help            show this help message and exit

可用命令:
  {setting,pushprop,version,ar,upload,deps,pull,reset,clone,branch,tag,serv-update}
    setting             把workspace内所有的module配置到settings.gradle
    pushprop            提交gradle.properties到git服务器
    version             自增gradle.properties内的 aar 配置版本
    ar                  依次 编译 所有module
    upload              按module名称 数字排列顺序 依次 执行gradle uploadArchives
    deps                项目依赖关系分析
    pull                更新 项目代码
    reset               重置 项目代码
    clone               克隆子工程
    branch              创建分支
    tag                 打tag
    serv-update         打包for jenkins

make it easy!
~~~

查看apkfly.py 子命令 帮助信息

~~~bash
➜  GWorkPR git:(mergeDev) ✗ ./apkfly.py upload -h
usage: apkfly upload [-h] [-s START] [-o]

optional arguments:
  -h, --help            show this help message and exit
  -s START, --start START
                        执行起始点【项目名前三位，例：027】
  -o, --only            只执行一个
~~~

##### 3. 获取子项目源码

获取子项目源码的正确姿势 ***apkfly.py clone***

~~~bash
➜  GWorkPR git:(mergeDev) ✗ ./apkfly.py upload -h
usage: apkfly upload [-h] [-s START] [-o]

optional arguments:
  -h, --help            show this help message and exit
  -s START, --start START
                        执行起始点【项目名前三位，例：027】
  -o, --only            只执行一个
➜  GWorkPR git:(mergeDev) ✗ ./apkfly.py clone -h
usage: apkfly clone [-h] [-o] [-a] [-g BY_GROUP] [-p BY_PROJECT] [-i]

optional arguments:
  -h, --help            show this help message and exit
  -o, --order           对子项目进行排序
  -a, --allow_private   包含私有项目
  -g BY_GROUP, --by_group BY_GROUP
                        根据组进行克隆
  -p BY_PROJECT, --by_project BY_PROJECT
                        根据项目名进行克隆
  -i, --ignore_app      忽略App
~~~

场景一：批量打AAR，获取项目源码

~~~bash
~ ./apyfly.py clone -o -i
~~~

> -o:克隆出来的子项目是按序号排序的
>
> -i:批量打AAR，需要过滤GomePlus

场景二：获取一个或者多个子项目

~~~bash
# 获取一个子项目
~ ./apkfly.py clone -p GomeApp
# 获取多个子项目
~ ./apkfly.py clone -p GomeApp -p MApp
~~~

> -p:根据项目名称获取子项目，多个的话，重复添加 -p 即可

场景三：获取一组子项目

~~~bash
# 获取所有私有项目
~ ./apyfly.py clone -g private
~~~

> 组可以自己定义添加，多个组以***逗号***分割


##### 4.创建分支、tag

创建分支

~~~bash
➜  GWorkPR git:(mergeDev) ✗ ./apkfly.py branch -h
usage: apkfly branch [-h] [-p] [-d] name

positional arguments:
  name          分支名称

optional arguments:
  -h, --help    show this help message and exit
  -p, --push    是否推送到服务器
  -d, --delete  删除分支
~~~

场景一：创建分支

~~~bash
~ ./apkfly.py branch NewBranchName   # 默认不推送到服务器
# 创建分支并推送到服务器
~ ./apkfly.py branch -p NewBranchName
~~~

场景二：删除分支

~~~bash
~ ./apkfly.py branch -d BranchName  # 同时会删除远程分支
~~~

创建tag

~~~bash
➜  GWorkPR git:(mergeDev) ✗ ./apkfly.py tag -h
usage: apkfly tag [-h] [-m MESSAGE] [-d] name

positional arguments:
  name                  tag名称

optional arguments:
  -h, --help            show this help message and exit
  -m MESSAGE, --message MESSAGE
                        评论信息
  -d, --delete          删除分支
~~~

场景一：创建tag

~~~bash
~ ./apkfly.py tag -m "tag描述信息" NewTagName  # -m 为可选参数
~~~

场景二：删除tag

~~~bash
~ ./apkfly.py tag -d TagName
~~~

##### 5.批量打AAR

~~~bash
➜  GWorkPR git:(mergeDev) ✗ ./apkfly.py upload -h
usage: apkfly upload [-h] [-s START] [-o]

optional arguments:
  -h, --help            show this help message and exit
  -s START, --start START
                        执行起始点【项目名前三位，例：027】
  -o, --only            只执行一个
~~~

打包流程

~~~bash
# 获取工作空间
~ git clone git@....:xxxx/GWorkspace.git -b [分支名称]
# 进入工作空间
~ cd GWorkspace
# 获取子项目
~ ./apyfly.py clone -o -i
# 升级版本号
~ ./apyfly.py version
# 打包并提交到maven私服
~ ./apyfly.py upload
# push属性文件
~ ./apyfly.py pushprop
~~~

##### 三. 工程依赖管理

1.查看依赖关系

~~~bash
# 项目默认查看依赖 task ，***需要在子项目内执行***
~ gradle dependencies
~~~

~~~bash
# 封装的 依赖分析工具，***在GWorkspace内执行***
~ ./apkfly.py deps GomePlus
~~~

2.分析结果

~~~bash
➜  GWorkPR git:(mergeDev) ✗ ./apkfly.py deps GomePlus

------------------------------------------------------------
Project :GomePlus
------------------------------------------------------------

compile - Classpath for compiling the main sources.
+--- com.gome.ecmall:frame:1.164.23-plus
|    +--- com.gome.ecmall:buildconfig:0.0.4
|    +--- com.gome.ecmall:secret:1.0.4
|    +--- com.gome.ecmall:patch:1.0.0
|    +--- com.android.support:recyclerview-v7:23.2.1
|    |    +--- com.android.support:support-v4:23.2.1 -> 23.4.0
|    |    |    \--- com.android.support:support-annotations:23.4.0
|    |    \--- com.android.support:support-annotations:23.2.1 -> 23.4.0
|    +--- com.android.support:appcompat-v7:23.2.1 -> 23.4.0
|    |    +--- com.android.support:animated-vector-drawable:23.4.0
|    |    |    \--- com.android.support:support-vector-drawable:23.4.0
|    |    |         \--- com.android.support:support-v4:23.4.0 (*)
|    |    +--- com.android.support:support-v4:23.4.0 (*)
|    |    \--- com.android.support:support-vector-drawable:23.4.0 (*)
|    +--- com.android.support:design:23.2.1
|    |    +--- com.android.support:support-v4:23.2.1 -> 23.4.0 (*)
|    |    +--- com.android.support:appcompat-v7:23.2.1 -> 23.4.0 (*)
|    |    \--- com.android.support:recyclerview-v7:23.2.1 (*)
|    +--- com.android.support:percent:23.0.1
|    |    \--- com.android.support:support-v4:23.0.1 -> 23.4.0 (*)
|    +--- de.greenrobot:eventbus:2.4.1
|    +--- com.alibaba:fastjson:1.1.54.android
|    +--- com.squareup.okhttp3:okhttp:3.4.1
|    |    \--- com.squareup.okio:okio:1.9.0
|    +--- com.facebook.fresco:fresco:1.1.0
|    |    +--- com.facebook.fresco:drawee:1.1.0
|    |    |    \--- com.facebook.fresco:fbcore:1.1.0
|    |    +--- com.facebook.fresco:fbcore:1.1.0
|    |    \--- com.facebook.fresco:imagepipeline:1.1.0
|    |         +--- com.parse.bolts:bolts-tasks:1.4.0
|    |         +--- com.facebook.fresco:fbcore:1.1.0
|    |         \--- com.facebook.fresco:imagepipeline-base:1.1.0
|    |              +--- com.parse.bolts:bolts-tasks:1.4.0
|    |              \--- com.facebook.fresco:fbcore:1.1.0
|    +--- com.facebook.fresco:animated-gif:1.1.0
|    |    +--- com.parse.bolts:bolts-tasks:1.4.0
|    |    +--- com.facebook.fresco:fbcore:1.1.0
|    |    \--- com.facebook.fresco:animated-base:1.1.0
|    |         +--- com.parse.bolts:bolts-tasks:1.4.0
|    |         +--- com.facebook.fresco:fbcore:1.1.0
|    |         +--- com.facebook.fresco:imagepipeline-base:1.1.0 (*)
|    |         \--- com.facebook.fresco:imagepipeline:1.1.0 (*)
|    +--- com.facebook.fresco:animated-webp:1.1.0
|    |    +--- com.parse.bolts:bolts-tasks:1.4.0
|    |    +--- com.facebook.fresco:webpsupport:1.1.0
|    |    |    +--- com.parse.bolts:bolts-tasks:1.4.0
|    |    |    +--- com.facebook.fresco:fbcore:1.1.0
|    |    |    \--- com.facebook.fresco:imagepipeline-base:1.1.0 (*)
|    |    \--- com.facebook.fresco:animated-base:1.1.0 (*)
|    +--- com.facebook.fresco:webpsupport:1.1.0 (*)
|    +--- com.facebook.fresco:imagepipeline-okhttp3:1.1.0
|    |    +--- com.facebook.fresco:fbcore:1.1.0
|    |    \--- com.facebook.fresco:imagepipeline:1.1.0 (*)
|    +--- com.nineoldandroids:library:2.4.0
|    +--- com.squareup.okhttp3:logging-interceptor:3.4.1
|    |    \--- com.squareup.okhttp3:okhttp:3.4.1 (*)
|    +--- com.thirdparty:safe_android_js_webview:1
|    +--- com.android.support:multidex:1.0.1
|    +--- com.gome.mobile:retrofit:2.1.0-g5
|    +--- com.squareup.retrofit2:adapter-rxjava:2.1.0
|    +--- com.squareup.retrofit2:converter-gson:2.1.0
|    |    \--- com.google.code.gson:gson:2.7
|    +--- io.reactivex:rxandroid:1.2.1
|    \--- io.reactivex:rxjava:1.1.6
+--- com.gome.ecmall:business:1.164.36-plus
|    +--- com.gome.ecmall:core:1.164.24-plus
|    |    +--- com.bangcle:safeKeyboard:1.0.6
|    |    +--- com.gome.ecmall:theme:1.27.22-plus
|    |    +--- com.gome.ecmall:frame:1.164.23-plus (*)
|    |    +--- com.thirdparty:alipaySDK:20160909
|    |    +--- com.google.code.gson:gson:2.6.2 -> 2.7
|    |    +--- com.thirdparty:passguard:1
|    |    +--- com.thirdparty:bestpaysdk:3.0.2
|    |    +--- com.thirdparty.unionpay:UPPayAssistEx:3.3.3
|    |    +--- com.thirdparty.unionpay:UPPayPluginExPro:3.3.3
|    |    +--- com.thirdparty:libammsdk:1
|    |    +--- com.thirdparty.tencent:open_sdk_r5509_lite:2
|    |    +--- com.thirdparty.sina:weiboSDKCore_3.1.4:2
|    |    +--- com.thirdparty.baidu:baidumapapi:3.3.0
|    |    +--- com.thirdparty.baidu:locSDK:6.23
|    |    +--- com.thirdparty:easemobchat:2.3.1
|    |    +--- com.thirdparty:mina_core:2.0.8
|    |    +--- com.google.protobuf:protobuf-java:2.6.1
|    |    \--- org.greenrobot:eventbus:3.0.0
|    +--- com.gome.ecmall:widget:1.164.21-plus
|    |    \--- com.gome.ecmall:frame:1.164.23-plus (*)
|    +--- com.gome.ecmall:push:1.164.23-plus
|    |    +--- com.gome.ecmall:core:1.164.24-plus (*)
|    |    \--- com.thirdparty:gome_im_client:1
|    +--- com.gome.ecmall:login:1.164.23-plus
|    +--- com.gome.ecmall:pullrefresh:1.164.23-plus
|    +--- com.gome.ecmall:update:1.164.23-plus
|    +--- com.gome.cmbpay:cmbpay:1.164.23-plus
|    \--- com.gome.ecmall:gomepay:1.164.23-plus
+--- com.gome.ecmall:finance:1.164.23-plus
|    \--- com.gome.ecmall:financecommon:1.164.23-plus
+--- com.gome.ecmall.home:im:1.164.23-plus
+--- com.gome.ecmall:gh5:1.164.26-plus
+--- com.gome.ecmall:meiyingbao:1.164.23-plus
+--- com.gome.ecmall:phonerecharge:1.164.23-plusap
+--- com.gome.ecmall:movie:1.164.23-plus
+--- com.gome.ecmall:storesearch:1.164.24-plus
+--- com.gome.ecmall:greturn:1.164.23-plus
+--- com.gome.ecmall:product:1.164.24-plus
+--- com.gome.ecmall:materialorder:1.164.28-plus
+--- com.gome.ecmall:ggomecurrency:1.164.15-plus
|    +--- com.android.support:appcompat-v7:23.4.0 (*)
|    \--- com.gome.ecmall.third:passguard:0.0.2
+--- com.gome.ecmall:tqdetail:1.164.23-plus
+--- com.gome.ecmall:gzxing:1.164.23-plus
|    \--- com.thirdparty:zxing:3.3.0
+--- project :MApp
|    +--- com.gome.ecmall:msc:1.0.0
|    +--- com.gome.third:sessionsdk:12
|    +--- com.gome.ecmall:mcommon:1.164.40-plus
|    |    +--- com.gome.ecmall:mselectphoto:1.160.23-plus
|    |    |    +--- com.android.support:recyclerview-v7:23.2.1 (*)
|    |    |    \--- com.android.support:appcompat-v7:23.2.1 -> 23.4.0 (*)
|    |    +--- com.bangcle:safeKeyboard:0.0.3 -> 1.0.6
|    |    +--- com.gome.ecmall:pullrefresh:1.164.23-plus
|    |    +--- com.gome.third:realm-android:0.87.1-5
|    |    +--- me.kareluo.intensify:image:1.1.0
|    |    +--- com.github.w446108264:XhsEmoticonsKeyboard:2.0.3
|    |    |    \--- com.android.support:support-v4:22.1.1 -> 23.4.0 (*)
|    |    +--- com.github.w446108264:AndroidEmoji:1.3-withsource
|    |    |    \--- com.android.support:support-v4:22.1.1 -> 23.4.0 (*)
|    |    +--- com.github.bumptech.glide:glide:3.6.0
|    |    +--- com.thirdparty:zxing:3.3.0
|    |    +--- com.android.databinding:library:1.3.1
|    |    |    +--- com.android.support:support-v4:21.0.3 -> 23.4.0 (*)
|    |    |    \--- com.android.databinding:baseLibrary:2.3.0-dev -> 2.3.0
|    |    +--- com.android.databinding:baseLibrary:2.3.0
|    |    +--- com.android.databinding:adapters:1.3.1
|    |    |    +--- com.android.databinding:library:1.3 -> 1.3.1 (*)
|    |    |    \--- com.android.databinding:baseLibrary:2.3.0-dev -> 2.3.0
|    |    +--- com.gome.ecmall:mim:1.29.54-plus
|    |    |    \--- net.zetetic:android-database-sqlcipher:3.5.4
|    |    \--- com.gome.ecmall:core:1.164.24-plus (*)
|    +--- com.gome.ecmall:mshare:1.164.40-plus
|    |    +--- com.gome.ecmall:mwidget:1.164.40-plus
|    |    |    \--- com.gome.ecmall:mcommon:1.164.40-plus (*)
|    |    \--- com.gome.ecmall:business:1.164.36-plus (*)
|    +--- com.gome.ecmall:mplayer:1.164.40-plus
|    |    +--- com.gome.ecmall:mcommon:1.164.40-plus (*)
|    |    \--- cn.com.gomeplus.player:gomeplus-player:1.0.5
|    +--- com.gome.ecmall:mim:1.29.54-plus (*)
|    +--- com.gome.ecmall:business:1.164.36-plus (*)
|    +--- cn.com.gomeplus.ad:gomeplus-ad-SDK:1.1.6
|    |    \--- com.android.support:appcompat-v7:23.0.1 -> 23.4.0 (*)
|    +--- com.gome.ecmall:gzxing:1.164.23-plus (*)
|    +--- com.android.databinding:library:1.3.1 (*)
|    +--- com.android.databinding:baseLibrary:2.3.0
|    \--- com.android.databinding:adapters:1.3.1 (*)
+--- com.android.databinding:library:1.3.1 (*)
+--- com.android.databinding:baseLibrary:2.3.0
\--- com.android.databinding:adapters:1.3.1 (*)

(*) - dependencies omitted (listed previously)
~~~

3.依赖配置

~~~groovy
dependencies {
    compile fileTree(dir: 'libs', include: '*.jar')
    compile(deps.GFrame) {
        transitive = true
        exclude group: 'com.android.support', module: 'support-core-utils'
    }

    compile(deps.GBusiness) {
        transitive = true
        exclude group: 'com.android.support', module: 'support-core-utils'
    }
    compile(deps.GFinance) {
        transitive = true
        exclude group: 'com.android.support', module: 'support-core-utils'
        exclude group: 'com.gome.ecmall', module: 'business'
    }
    compile deps.GIm
}
~~~

> transitive : 引入aar组件的依赖树【默认aar不引入】
>
> exclude : 解除依赖  group-依赖组件的groupId || module-依赖组件的artifactId