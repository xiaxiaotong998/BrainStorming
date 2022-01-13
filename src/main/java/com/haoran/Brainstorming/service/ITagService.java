package com.haoran.Brainstorming.service;

import com.haoran.Brainstorming.model.Tag;
import com.haoran.Brainstorming.util.MyPage;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;
import java.util.Map;


public interface ITagService {
    void selectTagsByTopicId(MyPage<Map<String, Object>> page);

    Tag selectById(Integer id);

    Tag selectByName(String name);

    List<Tag> selectByIds(List<Integer> ids);

    List<Tag> selectByTopicId(Integer topicId);
    List<Tag> insertTag(String newTags);

    void reduceTopicCount(Integer id);

    MyPage<Map<String, Object>> selectTopicByTagId(Integer tagId, Integer pageNo);

    IPage<Tag> selectAll(Integer pageNo, Integer pageSize, String name);

    void update(Tag tag);

    void delete(Integer id);

    void async();

    int countToday();
}
