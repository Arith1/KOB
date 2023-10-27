package com.kob.botrunningsystem.service.impl.utils;

import com.kob.botrunningsystem.service.impl.utils.Bot;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BotPool extends Thread {

    private static ReentrantLock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();      //条件变量

    private Queue<Bot> bots = new LinkedList<>();

    public void addBot(Integer userId, String botCode, String input){
        lock.lock();
        try {
            bots.add(new Bot(userId, botCode, input));
            condition.signalAll();  //唤起线程
        } finally {
            lock.unlock();
        }
    }

    private void consume(Bot bot) { //动态编译代码
        Consumer consumer = new Consumer();
        consumer.startTimeout(2000, bot); //最多两秒

    }


    @Override
    public void run() {
        while(true){
            lock.lock();
            if(bots.isEmpty()){
                try {
                    condition.await();  //让当前线程阻塞，再手动唤醒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    lock.unlock();
                    break;
                }
            } else {
                //队列不为空，取出队头
                Bot bot = bots.remove();
                lock.unlock();
                consume(bot);   //比较耗时，几秒钟
            }

        }
    }
}
