package com.haoran.Brainstorming.directive;

import com.haoran.Brainstorming.service.ITopicService;
import com.haoran.Brainstorming.util.MyPage;
import freemarker.core.Environment;
import freemarker.template.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;


@Component
public class TopicListDirective implements TemplateDirectiveModel {

    @Resource
    private ITopicService topicService;

    @Override
    public void execute(Environment environment, Map map, TemplateModel[] templateModels,
                        TemplateDirectiveBody templateDirectiveBody) throws TemplateException, IOException {
        Integer pageNo = Integer.parseInt(map.get("pageNo").toString());
        String tab = map.get("tab").toString();
        MyPage<Map<String, Object>> page = topicService.selectAll(pageNo, tab);

        DefaultObjectWrapperBuilder builder = new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_28);
        environment.setVariable("page", builder.build().wrap(page));
        templateDirectiveBody.render(environment.getOut());
    }
}
