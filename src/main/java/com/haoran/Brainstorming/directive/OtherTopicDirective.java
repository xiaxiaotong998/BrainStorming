package com.haoran.Brainstorming.directive;

import com.haoran.Brainstorming.model.Topic;
import com.haoran.Brainstorming.service.ITopicService;
import freemarker.core.Environment;
import freemarker.template.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;


@Component
public class OtherTopicDirective implements TemplateDirectiveModel {

    @Resource
    private ITopicService topicService;

    @Override
    public void execute(Environment environment, Map map, TemplateModel[] templateModels, TemplateDirectiveBody
            templateDirectiveBody) throws TemplateException, IOException {
        Integer userId = Integer.parseInt(map.get("userId").toString());
        Integer topicId = Integer.parseInt(map.get("topicId").toString());
        Integer limit = Integer.parseInt(map.get("limit").toString());
        List<Topic> topics = topicService.selectAuthorOtherTopic(userId, topicId, limit);

        DefaultObjectWrapperBuilder builder = new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_28);
        environment.setVariable("topics", builder.build().wrap(topics));
        templateDirectiveBody.render(environment.getOut());
    }
}
