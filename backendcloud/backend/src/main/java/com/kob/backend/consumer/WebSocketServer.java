package com.kob.backend.consumer;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.consumer.utils.Game;
import com.kob.backend.consumer.utils.JwtAuthentication;
import com.kob.backend.mapper.BotMapper;
import com.kob.backend.mapper.RecordMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

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
    //匹配池转移到matchingsystem
    private User user;
    private Session session = null;
    //每个链接由session维护

    public static UserMapper userMapper;
    private static BotMapper botMapper;
    public static RecordMapper recordMapper;
    public static RestTemplate restTemplate;
    public Game game = null;//存放地图（应该存在比赛双方的链接里）
    private final static String addPlayerUrl = "http://127.0.0.1:3081/player/add/";  //静态常量
    private final static String removePlayerUrl = "http://127.0.0.1:3081/player/remove/";    ////静态常量
    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        WebSocketServer.userMapper = userMapper;
    }
    @Autowired
    public void setBotMapper(BotMapper botMapper) {
        WebSocketServer.botMapper = botMapper;
    }
    @Autowired
    public void setRecordMapper(RecordMapper recordMapper) {
        WebSocketServer.recordMapper = recordMapper;
    }
    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        WebSocketServer.restTemplate = restTemplate;
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

    }

    @OnClose
    public void onClose() {
        // 关闭链接
        System.out.println("disconnected!");
        if(this.user != null){
            users.remove(this.user.getId());
        }
        // 问题描述: 开始匹配后，刷新当前页面，断开连接，再次匹配，实现自己和自己匹配
        // 解决方案: 断开链接时，删除已经存在在匹配池里的player信息，避免自己和自己匹配
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", this.user.getId().toString());
        restTemplate.postForObject(removePlayerUrl, data, String.class);
    }

    public static void startGame(Integer aId, Integer aBotId, Integer bId, Integer bBotId){
        User a = userMapper.selectById(aId), b = userMapper.selectById(bId);
        Bot botA = botMapper.selectById(aBotId), botB = botMapper.selectById(bBotId);
        Game game = new Game(13,
                14,
                20,
                a.getId(),
                botA,
                b.getId(),
                botB);
        game.createMap();
        if(users.get(a.getId()) != null)
            users.get(a.getId()).game = game;
        if(users.get(b.getId()) != null)
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
        if(users.get(a.getId()) != null)
            users.get(a.getId()).sendMessage(respA.toJSONString());

        JSONObject respB = new JSONObject();
        respB.put("event","start-matching");
        respB.put("opponent_username",a.getUsername());
        respB.put("opponent_photo", a.getPhoto());
        respB.put("game",respGame);
        if(users.get(b.getId()) != null)
            users.get(b.getId()).sendMessage(respB.toJSONString());
        try {
            Thread.sleep(1000*5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //开始匹配
    private void startMatching(Integer botId){
        System.out.println(user.getUsername()+" startMatching");
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id",this.user.getId().toString());
        data.add("rating",this.user.getRating().toString());
        data.add("bot_id",botId.toString());
        //TODO restTemplate 开始匹配
        restTemplate.postForObject(addPlayerUrl,data,String.class); //通信


    }

    //中止匹配
    private void stopMatching(){
        System.out.println(user.getUsername()+" stopMatching");
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id",this.user.getId().toString());
        restTemplate.postForObject(removePlayerUrl,data,String.class); //通信
    }


    private void move(int direction){   //赋值偏移量
        System.out.println(game.getPlayerA().getId() + "发送消息");
        if(game.getPlayerA().getId().equals(user.getId())){
            if(game.getPlayerA().getBotId().equals(-1)){  //亲自
                System.out.println("获取到playerA的下一步方向：" + direction);
                game.setNextStepA(direction);
            }
        }else if(game.getPlayerB().getId().equals(user.getId())){
            if(game.getPlayerB().getBotId().equals(-1)){
                System.out.println("获取到playerB的下一步方向：" + direction);
                game.setNextStepB(direction);
            }

        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // 从Client接收消息

        JSONObject data = JSONObject.parseObject(message);
        System.out.println(data.getString("username") + " receive message!");
        String event = data.getString("event");
        if("start-matching".equals(event)){
            startMatching(data.getInteger("bot_id"));
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
