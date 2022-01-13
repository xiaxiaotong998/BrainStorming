package com.haoran.Brainstorming.service.impl;

import com.haoran.Brainstorming.config.websocket.MyWebSocket;
import com.haoran.Brainstorming.mapper.CommentMapper;
import com.haoran.Brainstorming.model.Comment;
import com.haoran.Brainstorming.model.Topic;
import com.haoran.Brainstorming.model.User;
import com.haoran.Brainstorming.model.vo.CommentsByTopic;
import com.haoran.Brainstorming.service.*;
import com.haoran.Brainstorming.util.Message;
import com.haoran.Brainstorming.util.MyPage;
import com.haoran.Brainstorming.util.SensitiveWordUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Service
@Transactional
public class CommentService implements ICommentService {

    private final Logger log = LoggerFactory.getLogger(CommentService.class);

    @Resource
    private CommentMapper commentMapper;
    @Resource
    @Lazy
    private ITopicService topicService;
    @Resource
    private ISystemConfigService systemConfigService;
    @Resource
    @Lazy
    private IUserService userService;
    @Resource
    private INotificationService notificationService;

    @Override
    public List<CommentsByTopic> selectByTopicId(Integer topicId) {
        List<CommentsByTopic> commentsByTopics = commentMapper.selectByTopicId(topicId);

        for (CommentsByTopic commentsByTopic : commentsByTopics) {
            commentsByTopic.setContent(SensitiveWordUtil.replaceSensitiveWord(commentsByTopic.getContent(), "*",
                    SensitiveWordUtil.MinMatchType));
        }
        return commentsByTopics;
    }


    @Override
    public void deleteByTopicId(Integer topicId) {
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Comment::getTopicId, topicId);
        commentMapper.delete(wrapper);
    }


    @Override
    public void deleteByUserId(Integer userId) {
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Comment::getUserId, userId);
        commentMapper.delete(wrapper);
    }


    @Override
    public Comment insert(Comment comment, Topic topic, User user) {
        commentMapper.insert(comment);

        topic.setCommentCount(topic.getCommentCount() + 1);
        topicService.update(topic, null);

        user.setScore(user.getScore() + Integer.parseInt(systemConfigService.selectAllConfig().get
                ("create_comment_score").toString()));
        userService.update(user);

        if (comment.getCommentId() != null) {
            Comment targetComment = this.selectById(comment.getCommentId());
            if (!user.getId().equals(targetComment.getUserId())) {
                notificationService.insert(user.getId(), targetComment.getUserId(), topic.getId(), "REPLY", comment
                        .getContent());

                User targetUser = userService.selectById(targetComment.getUserId());
            }
        }

        if (!user.getId().equals(topic.getUserId())) {
            notificationService.insert(user.getId(), topic.getUserId(), topic.getId(), "COMMENT", comment.getContent());
            User targetUser = userService.selectById(topic.getUserId());

        }

        return comment;
    }

    @Override
    public Comment selectById(Integer id) {
        return commentMapper.selectById(id);
    }


    @Override
    public void update(Comment comment) {
        commentMapper.updateById(comment);
    }


    @Override
    public int vote(Comment comment, User user) {
        String upIds = comment.getUpIds();
        Set<String> strings = StringUtils.commaDelimitedListToSet(upIds);
        Integer userScore = user.getScore();
        if (strings.contains(String.valueOf(user.getId()))) {
            strings.remove(String.valueOf(user.getId()));
            userScore -= Integer.parseInt(systemConfigService.selectAllConfig().get("up_comment_score").toString());
        } else {
            strings.add(String.valueOf(user.getId()));
            userScore += Integer.parseInt(systemConfigService.selectAllConfig().get("up_comment_score").toString());
        }

        comment.setUpIds(StringUtils.collectionToCommaDelimitedString(strings));
        this.update(comment);
        user.setScore(userScore);
        userService.update(user);
        return strings.size();
    }


    @Override
    public void delete(Comment comment) {
        if (comment != null) {

            Topic topic = topicService.selectById(comment.getTopicId());
            topic.setCommentCount(topic.getCommentCount() - 1);
            topicService.update(topic, null);
            User user = userService.selectById(comment.getUserId());
            user.setScore(user.getScore() - Integer.parseInt(systemConfigService.selectAllConfig().get
                    ("delete_comment_score").toString()));
            userService.update(user);
            commentMapper.deleteById(comment.getId());
        }
    }


    @Override
    public MyPage<Map<String, Object>> selectByUserId(Integer userId, Integer pageNo, Integer pageSize) {
        MyPage<Map<String, Object>> iPage = new MyPage<>(pageNo, pageSize == null ? Integer.parseInt(systemConfigService
                .selectAllConfig().get("page_size").toString()) : pageSize);
        MyPage<Map<String, Object>> page = commentMapper.selectByUserId(iPage, userId);
        for (Map<String, Object> map : page.getRecords()) {
            Object content = map.get("content");
            map.put("content", StringUtils.isEmpty(content) ? null : SensitiveWordUtil.replaceSensitiveWord(content
                    .toString(), "*", SensitiveWordUtil.MinMatchType));
        }
        return page;
    }

    // ---------------------------- admin ----------------------------

    @Override
    public MyPage<Map<String, Object>> selectAllForAdmin(Integer pageNo, String startDate, String endDate, String username) {
        MyPage<Map<String, Object>> iPage = new MyPage<>(pageNo, Integer.parseInt((String) systemConfigService.selectAllConfig().get("page_size")));
        return commentMapper.selectAllForAdmin(iPage, startDate, endDate, username);
    }

    @Override
    public int countToday() {
        return commentMapper.countToday();
    }
}
