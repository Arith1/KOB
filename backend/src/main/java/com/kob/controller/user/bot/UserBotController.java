package com.kob.controller.user.bot;

import com.kob.pojo.Bot;
import com.kob.service.user.bot.UserBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user/bot/")
public class UserBotController {

    @Autowired
    private UserBotService botService;

    @PostMapping("/add/")
    public Map<String, String> add(@RequestParam Map<String, String> data) {
        return botService.add(data);
    }

    @GetMapping("/getlist/")
    public List<Bot> getList() {
        return botService.getList();
    }

    @PostMapping("/remove/")
    public Map<String, String> remove(@RequestParam Map<String, String> data) {
        return botService.remove(data);
    }

    @PostMapping("/update/")
    public Map<String, String> update(@RequestParam Map<String, String> data) {
        return botService.update(data);
    }




}
