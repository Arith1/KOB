# KOB
springboot工程课
#4.1 配置Mysql与注册登录模块
    1.安装
        Spring Boot Starter JDBC
        Project Lombok
        MySQL Connector/J
        mybatis-plus-boot-starter
        mybatis-plus-generator
    2.创建user各种实现类
    3.安装spring-boot-starter-security依赖并
        3.1实现service.impl.UserDetailsServiceImpl类，继承自UserDetailsService接口，用来接入数据库信息
        3.2实现config.SecurityConfig类，用来实现用户密码的加密存储
#4.2 配置Mysql与注册登录模块
    1.添加
        jjwt-api
        jjwt-impl
        jjwt-jackson依赖
    2.更新SecurityConfig并且创建JwtAuthenticationTokenFilter以及JwtUtil类
    3.添加id自增,添加photo字段
    4.实现info,login,register类
    5.创建user.js以及实现登录和获取信息接口
#4.3
    1.前端页面的授权：未登录时所以页面会重定向到login
        更改router.index.js
    2.注册页面
    3.登录状态的持久化
#5.1 实现个人bot页面的后端接口
#5.2
    1.实现创建个人bot界面，
    2.add方法时使用modal悬浮模块
    3.更改时区 @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    4.实现删除按钮
    5.实现修改按钮:每个bot对应一个modal
    6.实现代码编辑器:安装vue3-ace-editer依赖以及ace-builds依赖
#6.1 匹配系统
    1.集成WebSocket:添加spring-boot-starter-websocket依赖       1.1创建WebSocketServer实例
        1.2创建config.WebSocketConfig类
    2.添加fastjson依赖前后端用json交流
    3.更改config.SecurityConfig类重载configure方法
    4.前端在pkindex页面提供链接后端的方法，并且添加pk.js存放pkindex页面数据以及方法
    5.创建consumer.utils.JwtAuthentication类用来处理连接时前端返回的token
    6.创建前端后端连接匹配界面
    7.将gamemap逻辑放到后端实现gamemap类
#6.2 
    1.实现player类
        1.1同步玩家位置：两个玩家的蛇都要在左下角
    2.多线程Game继承Thread类，实现多线程
    3.ReentrantLock对对战界面加锁
    4.将蛇的所有操作转移到后端判断
    5.添加结果页面
    6.数据库添加新表record，后端创建record相关类，实现比赛完成后存放比赛记录