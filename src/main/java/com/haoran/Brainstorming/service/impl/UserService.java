package com.haoran.Brainstorming.service.impl;

import com.haoran.Brainstorming.mapper.UserMapper;
import com.haoran.Brainstorming.model.User;
import com.haoran.Brainstorming.service.*;
import com.haoran.Brainstorming.util.MyPage;
import com.haoran.Brainstorming.util.SpringContextUtil;
import com.haoran.Brainstorming.util.StringUtil;
import com.haoran.Brainstorming.util.bcrypt.BCryptPasswordEncoder;
import com.haoran.Brainstorming.util.identicon.Identicon;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserService implements IUserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    @Lazy
    private ICollectService collectService;
    @Resource
    @Lazy
    private ITopicService topicService;
    @Resource
    @Lazy
    private ICommentService commentService;
    @Resource
    private Identicon identicon;
    @Resource
    private INotificationService notificationService;
    @Resource
    private ISystemConfigService systemConfigService;
    @Resource
    private ICodeService codeService;

    @Override
    public User selectByUsername(String username) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(User::getUsername, username);
        return userMapper.selectOne(wrapper);
    }


    private String generateToken() {
        String token = UUID.randomUUID().toString();
        User user = this.selectByToken(token);
        if (user != null) {
            return this.generateToken();
        }
        return token;
    }

    /**
     *
     *
     * @param username
     * @param password
     * @param avatar
     * @param email
     * @param bio
     * @param website
     * @return
     */
    @Override
    public User addUser(String username, String password, String avatar, String email, String bio, String website,
                        boolean needActiveEmail) {
        String token = this.generateToken();
        User user = new User();
        user.setUsername(username);
        if (!StringUtils.isEmpty(password)) user.setPassword(new BCryptPasswordEncoder().encode(password));
        user.setToken(token);
        user.setInTime(new Date());
        if (avatar == null) avatar = identicon.generator(username);
        user.setAvatar(avatar);
        user.setEmail(email);
        user.setBio(bio);
        user.setWebsite(website);
        user.setActive(systemConfigService.selectAllConfig().get("user_need_active").equals("0"));
        userMapper.insert(user);
        return this.selectById(user.getId());
    }

    private String generateUsername() {
        String username = StringUtil.randomString(6);
        if (this.selectByUsername(username) != null) {
            return this.generateUsername();
        }
        return username;
    }


//    @Override
//    public User addUserWithMobile(String mobile) {
//        User user = selectByMobile(mobile);
//        if (user == null) {
//            String token = this.generateToken();
//            String username = generateUsername();
//            user = new User();
//            user.setUsername(username);
//            user.setToken(token);
//            user.setInTime(new Date());
//            user.setAvatar(identicon.generator(username));
//            user.setEmail(null);
//            user.setBio(null);
//            user.setWebsite(null);
//            user.setActive(true);
//            userMapper.insert(user);
//            return this.selectById(user.getId());
//        }
//        return user;
//    }

    @Override
    public User selectByToken(String token) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(User::getToken, token);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public User selectByMobile(String mobile) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(User::getMobile, mobile);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public User selectByEmail(String email) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(User::getEmail, email);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public User selectById(Integer id) {
        return userMapper.selectById(id);
    }

    @Override
    public List<User> selectTop(Integer limit) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("score").last("limit " + limit);
        return userMapper.selectList(wrapper);
    }

    @Override
    public void update(User user) {
        userMapper.updateById(user);
        SpringContextUtil.getBean(UserService.class).delRedisUser(user);
    }

    // ------------------------------- admin ------------------------------------------

    @Override
    public IPage<User> selectAll(Integer pageNo, String username) {
        MyPage<User> page = new MyPage<>(pageNo, Integer.parseInt((String) systemConfigService.selectAllConfig().get("page_size")));
        page.setDesc("in_time");
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(username)) {
            wrapper.lambda().eq(User::getUsername, username);
        }
        return userMapper.selectPage(page, wrapper);
    }

    public User selectByIdNoCatch(Integer id) {
        return userMapper.selectById(id);
    }

    @Override
    public int countToday() {
        return userMapper.countToday();
    }

    @Override
    public void deleteUser(Integer id) {
        notificationService.deleteByUserId(id);
        collectService.deleteByUserId(id);
        commentService.deleteByUserId(id);
        topicService.deleteByUserId(id);
        codeService.deleteByUserId(id);
        User user = this.selectById(id);
        SpringContextUtil.getBean(UserService.class).delRedisUser(user);
        userMapper.deleteById(id);
    }

    @Override
    public void delRedisUser(User user) {

    }
}
