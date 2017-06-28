package com.sqlchan.wenda.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Administrator on 2017/6/28.
 */
@Controller
public class IndexController {

    @ResponseBody
    @RequestMapping("/")
    public String index(){
        return "hello";
    }
}
