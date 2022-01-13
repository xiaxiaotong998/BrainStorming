package com.haoran.Brainstorming.service;

import com.haoran.Brainstorming.model.Collect;
import com.haoran.Brainstorming.model.User;
import com.haoran.Brainstorming.util.MyPage;

import java.util.List;
import java.util.Map;

public interface ICollectService {
    List<Collect> selectByTopicId(Integer topicId);

    Collect selectByTopicIdAndUserId(Integer topicId, Integer userId);

    Collect insert(Integer topicId, User user);

    void delete(Integer topicId, Integer userId);

    void deleteByTopicId(Integer topicId);

    void deleteByUserId(Integer userId);

    int countByUserId(Integer userId);

    MyPage<Map<String, Object>> selectByUserId(Integer userId, Integer pageNo, Integer pageSize);
}
