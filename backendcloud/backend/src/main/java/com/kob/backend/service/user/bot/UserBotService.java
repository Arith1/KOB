package com.kob.backend.service.user.bot;

import com.kob.backend.pojo.Bot;

import java.util.List;
import java.util.Map;

public interface UserBotService {
    Map<String, String> add(Map<String, String> data);
    List<Bot> getList();
    Map<String, String> remove(Map<String, String> data);
    Map<String, String> update(Map<String, String> data);
}
