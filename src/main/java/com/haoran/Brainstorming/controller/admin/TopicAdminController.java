package com.haoran.Brainstorming.controller.admin;

import com.haoran.Brainstorming.model.Tag;
import com.haoran.Brainstorming.model.Topic;
import com.haoran.Brainstorming.service.IIndexedService;
import com.haoran.Brainstorming.service.ITagService;
import com.haoran.Brainstorming.service.ITopicService;
import com.haoran.Brainstorming.util.MyPage;
import com.haoran.Brainstorming.util.Result;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/admin/topic")
public class TopicAdminController extends BaseAdminController {

    @Resource
    private ITopicService topicService;
    @Resource
    private ITagService tagService;
    @Resource
    private IIndexedService indexedService;

    @RequiresPermissions("topic:list")
    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "1") Integer pageNo, String startDate, String endDate, String
            username, Model model) {
        if (StringUtils.isEmpty(startDate)) startDate = null;
        if (StringUtils.isEmpty(endDate)) endDate = null;
        if (StringUtils.isEmpty(username)) username = null;
        MyPage<Map<String, Object>> page = topicService.selectAllForAdmin(pageNo, startDate, endDate, username);
        model.addAttribute("page", page);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("username", username);
        return "admin/topic/list";
    }

    @RequiresPermissions("topic:edit")
    @GetMapping("/edit")
    public String edit(Integer id, Model model) {
        model.addAttribute("topic", topicService.selectById(id));
        List<Tag> tagList = tagService.selectByTopicId(id);
        String tags = StringUtils.collectionToCommaDelimitedString(tagList.stream().map(Tag::getName).collect(Collectors
                .toList()));
        model.addAttribute("tags", tags);
        return "admin/topic/edit";
    }

    @RequiresPermissions("topic:edit")
    @PostMapping("edit")
    @ResponseBody
    public Result update(Integer id, String title, String content, String tags) {
        Topic topic = topicService.selectById(id);
        topic.setTitle(title);
        topic.setContent(content);
        topic.setModifyTime(new Date());
        topicService.update(topic, tags);
        return success();
    }

    @RequiresPermissions("topic:good")
    @GetMapping("/good")
    @ResponseBody
    public Result good(Integer id) {
        Topic topic = topicService.selectById(id);
        topic.setGood(!topic.getGood());
        topicService.update(topic, null);
        return success();
    }

    @RequiresPermissions("topic:top")
    @GetMapping("/top")
    @ResponseBody
    public Result top(Integer id) {
        Topic topic = topicService.selectById(id);
        topic.setTop(!topic.getTop());
        topicService.update(topic, null);
        return success();
    }

    @RequiresPermissions("topic:delete")
    @GetMapping("/delete")
    @ResponseBody
    public Result delete(Integer id) {
        Topic topic = topicService.selectById(id);
        topicService.delete(topic);
        return success();
    }

    @RequiresPermissions("topic:index")
    @GetMapping("/index")
    @ResponseBody
    public Result index(Integer id) {
        Topic topic = topicService.selectById(id);
        indexedService.indexTopic(String.valueOf(topic.getId()), topic.getTitle(), topic.getContent());
        return success();
    }

    @RequiresPermissions("topic:index_all")
    @GetMapping("/index_all")
    @ResponseBody
    public Result index_all() {
        indexedService.indexAllTopic();
        return success();
    }

    @RequiresPermissions("topic:delete_index")
    @GetMapping("/delete_index")
    @ResponseBody
    public Result delete_index(String id) {
        indexedService.deleteTopicIndex(id);
        return success();
    }

    @RequiresPermissions("topic:delete_all_index")
    @GetMapping("/delete_all_index")
    @ResponseBody
    public Result delete_all_index() {
        indexedService.batchDeleteIndex();
        return success();
    }
}
