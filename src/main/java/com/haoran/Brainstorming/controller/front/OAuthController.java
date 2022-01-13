package com.haoran.Brainstorming.controller.front;

import com.haoran.Brainstorming.model.OAuthUser;
import com.haoran.Brainstorming.model.User;
import com.haoran.Brainstorming.plugin.SocialPlugin;
import com.haoran.Brainstorming.service.IOAuthUserService;
import com.haoran.Brainstorming.service.ISystemConfigService;
import com.haoran.Brainstorming.service.IUserService;
import com.haoran.Brainstorming.util.CookieUtil;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/oauth")
public class OAuthController extends BaseController {

    @Resource
    private ISystemConfigService systemConfigService;
    @Resource
    private IUserService userService;
    @Resource
    private IOAuthUserService oAuthUserService;
    @Resource
    private CookieUtil cookieUtil;
    @Resource
    private SocialPlugin socialPlugin;

    @GetMapping("/redirect/{type}")
    public String github(@PathVariable("type") String type, HttpSession session) {

        AuthRequest request = socialPlugin.getRequest(type);

        return redirect(request.authorize(AuthStateUtils.createState()));
    }

    @GetMapping("/{type}/callback")
    public String callback(@PathVariable("type") String type, AuthCallback callback, HttpSession session) {

        AuthRequest request = socialPlugin.getRequest(type);

        AuthResponse<AuthUser> response = request.login(callback);
        if (!response.ok()) {
            throw new IllegalArgumentException(response.getMsg());
        }
        AuthUser authUser = response.getData();

        String username = authUser.getUsername();
        String githubId = authUser.getUuid();

        String avatarUrl = authUser.getAvatar();
        String bio = authUser.getRemark();
        String email = authUser.getEmail();
        String blog = authUser.getBlog();
        String accessToken = authUser.getToken().getAccessToken();

        OAuthUser oAuthUser = oAuthUserService.selectByTypeAndLogin(type.toUpperCase(), authUser.getUsername());
        User user;
        if (oAuthUser == null) {
            if (userService.selectByUsername(username) != null) {
                username = username + githubId;
            }
            user = userService.addUser(username, null, avatarUrl, email, bio, blog, StringUtils.isEmpty(email));
            oAuthUserService.addOAuthUser(Integer.parseInt(githubId), type.toUpperCase(), authUser.getUsername(), accessToken, bio, email, user.getId
                    (), null, null, null);
        } else {
            user = userService.selectById(oAuthUser.getUserId());
            oAuthUser.setEmail(email);
            oAuthUser.setBio(bio);
            oAuthUser.setAccessToken(accessToken);
            oAuthUserService.update(oAuthUser);
        }

        session.setAttribute("_user", user);
        cookieUtil.setCookie(systemConfigService.selectAllConfig().get("cookie_name").toString(), user.getToken());

        return redirect("/");
    }
}
