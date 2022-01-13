package com.haoran.Brainstorming.controller.api;

import com.haoran.Brainstorming.model.OAuthUser;
import com.haoran.Brainstorming.model.User;
import com.haoran.Brainstorming.service.*;
import com.haoran.Brainstorming.util.MyPage;
import com.haoran.Brainstorming.util.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserApiController extends BaseApiController {

    @Resource
    private IUserService userService;
    @Resource
    private ITopicService topicService;
    @Resource
    private ICommentService commentService;
    @Resource
    private ICollectService collectService;
    @Resource
    private IOAuthUserService oAuthUserService;

    @GetMapping("/{username}")
    public Result profile(@PathVariable String username) {
        User user = userService.selectByUsername(username);
        List<OAuthUser> oAuthUsers = oAuthUserService.selectByUserId(user.getId());
        MyPage<Map<String, Object>> topics = topicService.selectByUserId(user.getId(), 1, 10);
        MyPage<Map<String, Object>> comments = commentService.selectByUserId(user.getId(), 1, 10);
        Integer collectCount = collectService.countByUserId(user.getId());

        Map<String, Object> map = new HashMap<>();
        map.put("user", user);
        map.put("oAuthUsers", oAuthUsers);
        map.put("topics", topics);
        map.put("comments", comments);
        map.put("collectCount", collectCount);
        return success(map);
    }

    @GetMapping("/{username}/topics")
    public Result topics(@PathVariable String username, @RequestParam(defaultValue = "1") Integer pageNo) {
        User user = userService.selectByUsername(username);
        MyPage<Map<String, Object>> topics = topicService.selectByUserId(user.getId(), pageNo, null);
        Map<String, Object> map = new HashMap<>();
        map.put("user", user);
        map.put("topics", topics);
        return success(map);
    }


    @GetMapping("/{username}/comments")
    public Result comments(@PathVariable String username, @RequestParam(defaultValue = "1") Integer pageNo) {
        User user = userService.selectByUsername(username);
        MyPage<Map<String, Object>> comments = commentService.selectByUserId(user.getId(), pageNo, null);
        Map<String, Object> map = new HashMap<>();
        map.put("user", user);
        map.put("comments", comments);
        return success(map);
    }


    @GetMapping("/{username}/collects")
    public Result collects(@PathVariable String username, @RequestParam(defaultValue = "1") Integer pageNo) {
        User user = userService.selectByUsername(username);
        MyPage<Map<String, Object>> collects = collectService.selectByUserId(user.getId(), pageNo, null);
        Map<String, Object> map = new HashMap<>();
        map.put("user", user);
        map.put("collects", collects);
        return success(map);
    }
}
