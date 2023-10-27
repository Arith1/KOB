package com.kob.botrunningsystem.service.impl;

import com.kob.botrunningsystem.service.BotRunningService;
import com.kob.botrunningsystem.service.impl.utils.BotPool;
import org.springframework.stereotype.Service;

@Service
public class BotRunningServiceImpl implements BotRunningService {

    public final static BotPool botpool = new BotPool();

    @Override
    public String addBot(Integer userId, String betCode, String input) {
        System.out.println("userId = " + userId + ", betCode = " + betCode + ", input = " + input);
        botpool.addBot(userId, betCode, input);
        return "add bot success";
    }
}
