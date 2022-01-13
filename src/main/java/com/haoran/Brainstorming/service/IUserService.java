package com.haoran.Brainstorming.service;

import com.haoran.Brainstorming.model.User;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

public interface IUserService {
    User selectByUsername(String username);

    User addUser(String username, String password, String avatar, String email, String bio, String website,
                 boolean needActiveEmail);

    User selectByToken(String token);

    User selectByMobile(String mobile);

    User selectByEmail(String email);

    User selectById(Integer id);

    List<User> selectTop(Integer limit);

    void update(User user);

    IPage<User> selectAll(Integer pageNo, String username);

    User selectByIdNoCatch(Integer id);

    int countToday();

    void deleteUser(Integer id);

    void delRedisUser(User user);
}
