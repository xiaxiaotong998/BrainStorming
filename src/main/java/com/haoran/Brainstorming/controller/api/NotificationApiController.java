package com.haoran.Brainstorming.controller.api;

import com.haoran.Brainstorming.model.User;
import com.haoran.Brainstorming.service.INotificationService;
import com.haoran.Brainstorming.util.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/notification")
public class NotificationApiController extends BaseApiController {

    @Resource
    private INotificationService notificationService;

    @GetMapping("/notRead")
    public Result notRead() {
        User user = getApiUser();
        return success(notificationService.countNotRead(user.getId()));
    }

    @GetMapping("/markRead")
    public Result markRead() {
        User user = getApiUser();
        notificationService.markRead(user.getId());
        return success();
    }

    @GetMapping("/list")
    public Result list() {
        User user = getApiUser();
        List<Map<String, Object>> notReadNotifications = notificationService.selectByUserId(user.getId(), false, 20);
        List<Map<String, Object>> readNotifications = notificationService.selectByUserId(user.getId(), true, 20);
        Map<String, Object> map = new HashMap<>();
        map.put("notRead", notReadNotifications);
        map.put("read", readNotifications);
        return success(map);
    }
}
