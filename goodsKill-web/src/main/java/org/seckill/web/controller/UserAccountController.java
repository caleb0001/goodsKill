package org.seckill.web.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.seckill.api.service.GoodsService;
import org.seckill.api.service.SeckillService;
import org.seckill.api.service.UserAccountService;
import org.seckill.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class UserAccountController {
    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private SeckillService seckillService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "/register/create", method = RequestMethod.POST)
    public String register(User user) {
        User userEncrypt = new User();
        BeanUtils.copyProperties(user,userEncrypt);
        userEncrypt.setPassword(new SimpleHash("MD5", user.getPassword(), ByteSource.Util.bytes(user.getAccount()), 2).toString());
        userAccountService.register(userEncrypt);
        // 注册成功后直接登录
        login(user);
        return "redirect:/seckill/list";
    }

    @RequestMapping(value = "/login")
    public String toLogin() {
        return "login";
    }

    @RequestMapping(value = "/login/session", method = RequestMethod.POST)
    public String login(User user) {
        UsernamePasswordToken token = new UsernamePasswordToken(user.getAccount(), user.getPassword());
        Subject subject = SecurityUtils.getSubject();

        try {
            subject.login(token);
            subject.getSession().setAttribute("user", user);
        } catch (Exception e) {
            subject.getSession().setAttribute("user", null);
            logger.error(e.getMessage(), e);
            return "redirect:/login";
        }
        return "redirect:/seckill/list";
    }

    @RequestMapping(value = "/register")
    public String register() {
        return "register";
    }
}