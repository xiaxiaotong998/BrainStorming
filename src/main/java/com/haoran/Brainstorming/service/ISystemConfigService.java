package com.haoran.Brainstorming.service;

import com.haoran.Brainstorming.model.SystemConfig;

import java.util.List;
import java.util.Map;


public interface ISystemConfigService {
    Map<String, String> selectAllConfig();

    SystemConfig selectByKey(String key);

    Map<String, Object> selectAll();

    void update(List<Map<String, String>> list);

    void updateByKey(String key, SystemConfig systemConfig);
}
