package com.haoran.Brainstorming.service;

import com.haoran.Brainstorming.model.Comment;
import com.haoran.Brainstorming.model.Topic;
import com.haoran.Brainstorming.model.User;
import com.haoran.Brainstorming.model.vo.CommentsByTopic;
import com.haoran.Brainstorming.util.MyPage;

import java.util.List;
import java.util.Map;


public interface ICommentService {
    List<CommentsByTopic> selectByTopicId(Integer topicId);

    void deleteByTopicId(Integer topicId);

    void deleteByUserId(Integer userId);
    Comment insert(Comment comment, Topic topic, User user);

    Comment selectById(Integer id);

    void update(Comment comment);

    int vote(Comment comment, User user);

    void delete(Comment comment);

    MyPage<Map<String, Object>> selectByUserId(Integer userId, Integer pageNo, Integer pageSize);

    MyPage<Map<String, Object>> selectAllForAdmin(Integer pageNo, String startDate, String endDate, String username);

    int countToday();
}
