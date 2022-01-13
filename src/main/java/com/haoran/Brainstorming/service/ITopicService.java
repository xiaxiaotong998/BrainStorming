package com.haoran.Brainstorming.service;

import com.haoran.Brainstorming.model.Topic;
import com.haoran.Brainstorming.model.User;
import com.haoran.Brainstorming.util.MyPage;

import java.util.List;
import java.util.Map;


public interface ITopicService {
    MyPage<Map<String, Object>> search(Integer pageNo, Integer pageSize, String keyword);

    MyPage<Map<String, Object>> selectAll(Integer pageNo, String tab);

    List<Topic> selectAuthorOtherTopic(Integer userId, Integer topicId, Integer limit);

    MyPage<Map<String, Object>> selectByUserId(Integer userId, Integer pageNo, Integer pageSize);
    Topic insert(String title, String content, String tags, User user);

    Topic selectById(Integer id);

    Topic selectByTitle(String title);

    Topic updateViewCount(Topic topic, String ip);

    void update(Topic topic, String tags);

    void delete(Topic topic);

    void deleteByUserId(Integer userId);

    MyPage<Map<String, Object>> selectAllForAdmin(Integer pageNo, String startDate, String endDate, String username);

    int countToday();

    int vote(Topic topic, User user);
}
