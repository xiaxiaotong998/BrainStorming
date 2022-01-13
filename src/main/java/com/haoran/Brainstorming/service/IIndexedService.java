package com.haoran.Brainstorming.service;


public interface IIndexedService {

    void indexAllTopic();

    void indexTopic(String id, String title, String content);

    void deleteTopicIndex(String id);

    void batchDeleteIndex();

}
