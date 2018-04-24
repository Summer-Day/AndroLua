## Android 平台执行 Lua 脚本实践

本文是基于[https://github.com/lendylongli/AndroLua](https://github.com/lendylongli/AndroLua) 进行进一步封装和探索

### 解决的问题
--------
1. Android 应用程序可以执行 Lua 脚本
2. Lua 脚本中可以调用 Java 方法


### 使用介绍
--------

#### 编译 Lua 使用的 so 库文件
git clone https://github.com/sunnybird/AndroLua.git  
cd AndroLua/LibAndroLua/src/main/  
ndk-build  

编译完成之后, 会在 libs 目录生成 so 文件  

LibAndroLua 可以自行编译成  aar 文件, 供其他项目使用  

 Demo 应用提供了简单的使用实例








