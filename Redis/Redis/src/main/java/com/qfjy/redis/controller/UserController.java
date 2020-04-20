package com.qfjy.redis.controller;

import com.qfjy.redis.bean.User;
import com.qfjy.redis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("user")
public class UserController {
    @Autowired
    private UserService userService;
    /**
     *
     * @param username 用户名
     * @param password 密码
     * @param valcode 验证码
     * @return
     */
    @RequestMapping("login")
    public String login(@RequestParam("username")final String username,
                        @RequestParam("password")final String password,
                        @RequestParam("valcode")final String valcode){

        /**
         * 1.验证码的比较
         */
        /**
         * 2.登录功能
         * 2.1先判断当前用户是被限制登录
         */
        //2.1先判断当前用户是被限制登录
        Map<String, Object> map = userService.loginUserLock(username);
        if ((boolean)map.get("flag")){
            //被限制
            return "登陆失败，因"+username+"用户名超过了登陆次数，已经被禁止登录。还剩："+map.get("lockTime");
        }else {
            //如果没有被限制
            //执行登录功能
            User user = userService.login(username, password);
            //判断是否登陆成功
            if (user != null){
                //登陆成功
                //清空对应的所有key
                return "/succ.jsp";
            }else {
                //登陆不成功
                String result = userService.loginValdate(username);
                return result;
            }
        }
    }
}
