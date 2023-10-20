package com.kob.controller.pk;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pk/")
public class BotController {

    @GetMapping("/getbotinfo")
    public Map<String, String> getBotInfo() {
        Map<String, String> botInfo = new HashMap<>();
        botInfo.put("bot_name", "bot1");
        botInfo.put("bot_rating", "1");
        return botInfo;
    }
}
