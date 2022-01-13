package com.haoran.Brainstorming.service;

import java.util.List;
import java.util.Map;


public interface INotificationService {
    List<Map<String, Object>> selectByUserId(Integer userId, Boolean read, Integer limit);

    void markRead(Integer userId);

    long countNotRead(Integer userId);

    void deleteByTopicId(Integer topicId);

    void deleteByUserId(Integer userId);

    void insert(Integer userId, Integer targetUserId, Integer topicId, String action, String content);
}
