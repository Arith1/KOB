package com.kob.backend.consumer.utils;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.Record;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

//TODO 多线程
public class Game extends Thread{
    private final Integer rows;
    private final Integer cols;
    private final Integer inner_walls_count;
    private final int[][] g;
    private final static int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};//偏移量

    private final Player playerA,playerB;
    private Integer nextStepA = null;   //下一步操作
    private Integer nextStepB = null;

    private String status = "playing"; //整个游戏的状态    playing->finished
    private String loser = ""; //all:平局 a:a输了 b:b输了

    private final static String addBotUrl = "http://127.0.0.1:3082/bot/add/";

    //TODO 加锁
    private ReentrantLock lock = new ReentrantLock();   //

    public Game(Integer rows,
                Integer cols,
                Integer inner_walls_count,
                Integer idA,
                Bot botA,
                Integer idB,
                Bot botB
                ) {
        this.rows = rows;
        this.cols = cols;
        this.inner_walls_count = inner_walls_count;
        this.g = new int[rows][cols];

        Integer botIdA = -1, botIdB = -1;
        String botCodeA = "", botCodeB = "";
        if (botA != null) {
            botIdA = botA.getId();
            botCodeA = botA.getContent();
        }
        if (botB != null) {
            botIdB = botB.getId();
            botCodeB = botB.getContent();
        }
        playerA = new Player(idA, botIdA, botCodeA, rows - 2, 1, new ArrayList<>());
        playerB = new Player(idB, botIdB, botCodeB, 1, cols - 2, new ArrayList<>());

    }




    public Player getPlayerA(){
        return playerA;
    }
    public Player getPlayerB(){
        return playerB;
    }

    public int[][] getG() {
        return g;
    }

    public void setNextStepA(Integer nextStepA) {
        lock.lock();
        try {
            this.nextStepA = nextStepA;
        }finally {
            lock.unlock();
        }

    }

    public void setNextStepB(Integer nextStepB) {
        lock.lock();
        try {
            this.nextStepB = nextStepB;
        }finally {
            lock.unlock();
        }
    }

    private boolean check_connectivity(int sx, int sy, int tx, int ty) {    //判断连通性
        if (sx == tx && sy == ty) return true;
        g[sx][sy] = 1;

        for (int i = 0; i < 4; i ++ ) {
            int x = sx + dx[i], y = sy + dy[i];
            if (x >= 0 && x < this.rows && y >= 0 && y < this.cols && g[x][y] == 0) {
                if (check_connectivity(x, y, tx, ty)) {
                    g[sx][sy] = 0;
                    return true;
                }
            }
        }

        g[sx][sy] = 0;
        return false;
    }

    private boolean draw() {  // 画地图
        for (int i = 0; i < this.rows; i ++ ) {
            for (int j = 0; j < this.cols; j ++ ) {
                g[i][j] = 0;
            }
        }

        for (int r = 0; r < this.rows; r ++ ) {
            g[r][0] = g[r][this.cols - 1] = 1;
        }
        for (int c = 0; c < this.cols; c ++ ) {
            g[0][c] = g[this.rows - 1][c] = 1;
        }

        Random random = new Random();
        for (int i = 0; i < this.inner_walls_count / 2; i ++ ) {    //生成地图
            for (int j = 0; j < 1000; j ++ ) {
                int r = random.nextInt(this.rows);
                int c = random.nextInt(this.cols);

                if (g[r][c] == 1 || g[this.rows - 1 - r][this.cols - 1 - c] == 1)
                    continue;
                if (r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2)
                    continue;

                g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = 1;
                break;
            }
        }

        return check_connectivity(this.rows - 2, 1, 1, this.cols - 2);
    }

    public void createMap() {   //画地图
        for (int i = 0; i < 1000; i ++ ) {
            if (draw())
                break;
        }
    }

    private String getInput(Player player){ //将当前的书面信息，编码成字符串
        Player me, you;
        if(playerA.getId().equals(player.getId())){
            me = playerA;
            you = playerB;
        } else {
            me = playerB;
            you = playerA;
        }

        return getMapString() + "#" +
                me.getSx() + "#" +
                me.getSy() + "#(" +
                me.getStepsString() + ")#" +
                you.getSx() + "#" +
                you.getSy() + "#(" +
                you.getStepsString() + ")";
    }

    private void sendBotCode(Player player) {
        if (player.getBotId().equals(-1)) return;  // 亲自出马，不需要执行代码
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", player.getId().toString());
        data.add("bot_code", player.getBotCode());
        data.add("input", getInput(player));
        WebSocketServer.restTemplate.postForObject(addBotUrl, data, String.class);
    }

    private boolean nextStep() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        sendBotCode(playerA);   //判断是否需要bot代码执行
        sendBotCode(playerB);

        for (int i = 0; i < 50; i ++ ) {
            try {
                Thread.sleep(100);
                lock.lock();
                try {
                    if (nextStepA != null && nextStepB != null) {
                        playerA.getSteps().add(nextStepA);
                        playerB.getSteps().add(nextStepB);
                        return true;
                    }
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return false;

    }

    private boolean check_valid(List<Cell> cellsA, List<Cell> cellsB) { // 检测目标位置是否合法：没有撞到两条蛇的身体和障碍物
        int n = cellsA.size();
        Cell cell = cellsA.get(n - 1);
        if (g[cell.x][cell.y] == 1) return false;   //撞到墙了

        for (int i = 0; i < n - 1; i ++ ) {         //A蛇是否重合
            if (cellsA.get(i).x == cell.x && cellsA.get(i).y == cell.y)
                return false;
        }

        for (int i = 0; i < n - 1; i ++ ) {         //B蛇是否重合
            if (cellsB.get(i).x == cell.x && cellsB.get(i).y == cell.y)
                return false;
        }

        return true;
    }


    private void judge(){   //判断两条蛇下一步是否合法
        List<Cell> cellsA = playerA.getCells();
        List<Cell> cellsB = playerB.getCells();

        boolean validA = check_valid(cellsA, cellsB);
        boolean validB = check_valid(cellsB, cellsA);
        if (!validA || !validB) {
            status = "finished";

            if (!validA && !validB) {   //都有非法操作全输了
                loser = "all";
            } else if (!validA) {
                loser = "A";
            } else {
                loser = "B";
            }
        }

    }
    private void sendAllMessage(String message){    //广播消息
        if(WebSocketServer.users.get(playerA.getId()) != null)
            WebSocketServer.users.get(playerA.getId()).sendMessage(message);
        if(WebSocketServer.users.get(playerB.getId()) != null)
            WebSocketServer.users.get(playerB.getId()).sendMessage(message);
    }
    private void sendMove(){    //向两个client转递移动操作
        System.out.println("移动操作");
        lock.lock();
        try{
            JSONObject resp = new JSONObject();
            resp.put("event", "move");
            resp.put("a_direction", nextStepA);
            resp.put("b_direction", nextStepB);
            sendAllMessage(resp.toJSONString());
            nextStepA = nextStepB = null;
        }finally {
            lock.unlock();
        }

    }
    private String getMapString() { //获取地图
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < rows; i ++ ) {
            for (int j = 0; j < cols; j ++ ) {
                res.append(g[i][j]);
            }
        }
        return res.toString();
    }

    private void saveToDatabase(){  //将对局保存到数据库中
        Record record = new Record(
                null,
                playerA.getId(),
                playerA.getSx(),
                playerA.getSy(),
                playerB.getId(),
                playerB.getSx(),
                playerB.getSy(),
                playerA.getStepsString(),
                playerB.getStepsString(),
                getMapString(),
                loser,
                new Date()
        );

        WebSocketServer.recordMapper.insert(record);

    }

    private void sendResult(){  //公布结果
        System.out.println("公布结果");
        JSONObject resp = new JSONObject();
        resp.put("event", "result");
        resp.put("loser", loser);
        saveToDatabase();
        sendAllMessage(resp.toJSONString());
    }

    //重写run函数
    @Override
    public void run() {
        System.out.println("run");
        for(int i = 0; i < 1000; i ++){
            if(nextStep()){ //是否获取下一步操作
                judge();
                System.out.println("status = " + status);
                if(status.equals("playing")){
                    sendMove();
                } else {
                    sendResult();
                    break;
                }
            }else{
                status = "finished";
                lock.lock();
                try {
                    if(nextStepA == null && nextStepB == null){
                        loser = "all";

                    }else if(nextStepA == null){
                        loser = "A";
                    }else{
                        loser = "B";
                    }
                }finally {
                    lock.unlock();
                }
                sendResult();
                break;//游戏结束
            }
        }
    }
}
