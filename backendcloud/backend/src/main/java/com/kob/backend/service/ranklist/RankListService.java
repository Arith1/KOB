package com.kob.backend.service.ranklist;

import com.alibaba.fastjson.JSONObject;

public interface RankListService {
    public JSONObject getList(Integer page);
}
