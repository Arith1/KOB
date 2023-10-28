package com.kob.backend.service.record;

import com.alibaba.fastjson.JSONObject;

public interface RecordService {
    JSONObject getList(Integer page);
}
