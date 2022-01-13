package com.haoran.Brainstorming.service;

import com.haoran.Brainstorming.model.Tag;
import com.haoran.Brainstorming.model.TopicTag;

import java.util.List;


public interface ITopicTagService {
    List<TopicTag> selectByTopicId(Integer topicId);

    List<TopicTag> selectByTagId(Integer tagId);

    void insertTopicTag(Integer topicId, List<Tag> tagList);

    void deleteByTopicId(Integer id);
}
