package com.sqlchan.wenda.controller;

import com.sqlchan.wenda.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/29.
 */
@Controller
public class LoginController {
    private static final Logger logger= LoggerFactory.getLogger(IndexController.class);

    @Autowired
    UserService userService;

    @RequestMapping(value = "/reg",method = {RequestMethod.POST})
    public String reg(Model model,
                      @RequestParam("username")String name,
                      @RequestParam("password")String password,
                      @RequestParam(value = "next", required = false) String next,
                      HttpServletResponse response){

        try {
            Map<String,String>map=userService.register(name, password);
            if(map.containsKey("ticket")){
                Cookie cookie=new Cookie("ticket",map.get("ticket").toString());
                cookie.setPath("/");
                response.addCookie(cookie);
                if (StringUtils.isNotBlank(next)) {
                    return "redirect:" + next;
                }
                return "redirect:/";
            }else {
                model.addAttribute("msg", map.get("msg"));
                return "regpage";
            }
        }catch (Exception e) {
            logger.error("zhuce yichang" + e.getMessage());
            return "regpage";
        }
    }

    @RequestMapping(value = "/login",method = {RequestMethod.POST})
    public String login(Model model,
                      @RequestParam("username")String name,
                      @RequestParam("password")String password,
                        @RequestParam(value = "next", required = false) String next,
                        HttpServletResponse response){
        try {
            Map<String,Object>map=userService.login(name, password);
            if(map.containsKey("ticket")){
                Cookie cookie=new Cookie("ticket",map.get("ticket").toString());
                cookie.setPath("/");
                response.addCookie(cookie);
                if (StringUtils.isNotBlank(next)) {
                    return "redirect:" + next;
                }
                return "redirect:/";
            }else {
                model.addAttribute("msg", map.get("msg"));
                return "redirect:/";
            }
        }catch (Exception e) {
            logger.error("登陆异常" + e.getMessage());
            return "redirect:/";
        }

    }

    @RequestMapping(path = {"/logout"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/";
    }

    @RequestMapping("/loginpage")
    public String loginpage(@RequestParam(value = "next",defaultValue = "")String next,Model model){
        model.addAttribute("next",next);
        return "loginpage";
    }

    @RequestMapping("/regpage")
    public String regpage(@RequestParam(value = "next",defaultValue = "")String next,Model model){
        model.addAttribute("next",next);
        return "regpage";
    }


}
