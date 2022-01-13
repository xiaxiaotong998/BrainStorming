package com.haoran.Brainstorming.controller.api;

import com.haoran.Brainstorming.controller.front.BaseController;
import com.haoran.Brainstorming.exception.ApiAssert;
import com.haoran.Brainstorming.model.User;
import com.haoran.Brainstorming.service.IUserService;
import com.haoran.Brainstorming.util.Result;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;


public class BaseApiController extends BaseController {

    @Resource
    private IUserService userService;

    protected Result success() {
        return success(null);
    }

    protected Result success(Object detail) {
        Result result = new Result();
        result.setCode(200);
        result.setDescription("SUCCESS");
        result.setDetail(detail);
        return result;
    }

    protected Result error(String description) {
        Result result = new Result();
        result.setCode(201);
        result.setDescription(description);
        return result;
    }


    protected User getApiUser() {
        return getApiUser(true);
    }

    protected User getApiUser(boolean required) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder
                .getRequestAttributes())).getRequest();
        String token = request.getHeader("token");
        if (required) {
            ApiAssert.notEmpty(token, "token ne peut pas etre vide");
            User user = userService.selectByToken(token);
            ApiAssert.notNull(user, "token incorrect");
            return user;
        } else {
            if (StringUtils.isEmpty(token)) return null;
            return userService.selectByToken(token);
        }
    }
}
