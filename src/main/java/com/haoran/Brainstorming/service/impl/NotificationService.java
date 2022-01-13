package com.haoran.Brainstorming.service.impl;

import com.haoran.Brainstorming.mapper.NotificationMapper;
import com.haoran.Brainstorming.model.Notification;
import com.haoran.Brainstorming.service.INotificationService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Service
@Transactional
public class NotificationService implements INotificationService {

    @Resource
    private NotificationMapper notificationMapper;


    @Override
    public List<Map<String, Object>> selectByUserId(Integer userId, Boolean read, Integer limit) {
        List<Map<String, Object>> notifications = notificationMapper.selectByUserId(userId, read, limit);
        return notifications;
    }

    @Override
    public void markRead(Integer userId) {
        notificationMapper.updateNotificationStatus(userId);
    }


    @Override
    public long countNotRead(Integer userId) {
        return notificationMapper.countNotRead(userId);
    }

    @Override
    public void deleteByTopicId(Integer topicId) {
        QueryWrapper<Notification> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Notification::getTopicId, topicId);
        notificationMapper.delete(wrapper);
    }

    @Override
    public void deleteByUserId(Integer userId) {
        QueryWrapper<Notification> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Notification::getTargetUserId, userId).or().eq(Notification::getUserId, userId);
        notificationMapper.delete(wrapper);
    }

    @Override
    public void insert(Integer userId, Integer targetUserId, Integer topicId, String action, String content) {
        Notification notification = new Notification();
        notification.setAction(action);
        notification.setContent(content);
        notification.setUserId(userId);
        notification.setTargetUserId(targetUserId);
        notification.setTopicId(topicId);
        notification.setInTime(new Date());
        notification.setRead(false);
        notificationMapper.insert(notification);
    }
}
