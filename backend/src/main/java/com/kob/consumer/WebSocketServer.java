package com.kob.consumer;

import com.alibaba.fastjson.JSONObject;
import com.kob.consumer.utils.Game;
import com.kob.consumer.utils.JwtAuthentication;
import com.kob.mapper.RecordMapper;
import com.kob.mapper.UserMapper;
import com.kob.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/websocket/{token}")  // 注意不要以'/'结尾，同样使用JWT验证
public class WebSocketServer {
    //每一个链接就是new一个WebSocketServer实例

    final public static ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>();
    //匹配池
    final private static CopyOnWriteArraySet<User> matchpool = new CopyOnWriteArraySet<>();
    private User user;
    private Session session = null;
    //每个链接由session维护

    private static UserMapper userMapper;
    public static RecordMapper recordMapper;
    private Game game = null;//存放地图（应该存在比赛双方的链接里）

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        WebSocketServer.userMapper = userMapper;
    }
    @Autowired
    public void setRecordMapper(RecordMapper recordMapper) {
        WebSocketServer.recordMapper = recordMapper;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        // 建立连接
        this.session = session;

        Integer userId = JwtAuthentication.getUserId(token);//判断是否合法
        this.user = userMapper.selectById(userId);

        if (this.user != null) {    //用户不为空
            users.put(userId, this);//添加用户
            System.out.println("connected!");
        } else {
            this.session.close();   //如果为空关闭session
        }

        System.out.println(users);

//        WebSocketServer client1 = new WebSocketServer();
//        WebSocketServer client2 = new WebSocketServer();
    }

    @OnClose
    public void onClose() {
        // 关闭链接
        System.out.println("disconnected!");
        if(this.user != null){
            users.remove(this.user.getId());
            matchpool.remove(this.user);
        }
    }

    //开始匹配
    private void startMatching(){
        System.out.println(user.getUsername()+" startMatching");
        matchpool.add(this.user);
        System.out.println(user.getUsername()+" are added to matchpool");
        while(matchpool.size() >= 2){   //开始
            System.out.println("匹配成功");
            Iterator<User> it = matchpool.iterator();
            User a = it.next();
            User b = it.next();
            matchpool.remove(a);
            matchpool.remove(b);

            Game game = new Game(13, 14, 20, a.getId(), b.getId());
            game.createMap();
            users.get(a.getId()).game = game;
            users.get(b.getId()).game = game;

            //TODO 开启线程
            game.start();

            JSONObject respGame = new JSONObject(); //封装json
            respGame.put("a_id",game.getPlayerA().getId());
            respGame.put("a_sx",game.getPlayerA().getSx());
            respGame.put("a_sy",game.getPlayerA().getSy());
            respGame.put("b_id",game.getPlayerB().getId());
            respGame.put("b_sx",game.getPlayerB().getSx());
            respGame.put("b_sy",game.getPlayerB().getSy());
            respGame.put("map",game.getG());



            JSONObject respA = new JSONObject();
            respA.put("event","start-matching");
            respA.put("opponent_username",b.getUsername());
            respA.put("opponent_photo", b.getPhoto());
            respA.put("game",respGame);
            users.get(a.getId()).sendMessage(respA.toJSONString());

            JSONObject respB = new JSONObject();
            respB.put("event","start-matching");
            respB.put("opponent_username",a.getUsername());
            respB.put("opponent_photo", a.getPhoto());
            respB.put("game",respGame);
            users.get(b.getId()).sendMessage(respB.toJSONString());
        }
    }

    //中止匹配
    private void stopMatching(){
        System.out.println(user.getUsername()+" stopMatching");
        matchpool.remove(this.user);
        System.out.println(user.getUsername()+" are removed to matchpool");
    }


    private void move(int direction){   //赋值偏移量
        System.out.println(game.getPlayerA().getId() + "发送消息");
        if(game.getPlayerA().getId().equals(user.getId())){
            System.out.println("获取到playerA的下一步方向：" + direction);
            game.setNextStepA(direction);
        }else if(game.getPlayerB().getId().equals(user.getId())){
            System.out.println("获取到playerB的下一步方向：" + direction);
            game.setNextStepB(direction);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // 从Client接收消息

        JSONObject data = JSONObject.parseObject(message);
        System.out.println(data.getString("username") + " receive message!");
        String event = data.getString("event");
        if("start-matching".equals(event)){
            startMatching();
        } else if("stop-matching".equals(event)){
            stopMatching();
        } else if("move".equals(event)){
            move(data.getInteger("direction"));
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    public void sendMessage(String message) {
        //后端服务器向client发信息
        synchronized (this.session){
            try{
                this.session.getBasicRemote().sendText(message);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
