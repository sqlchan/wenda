package com.sqlchan.wenda.controller;

import com.sqlchan.wenda.model.EntityType;
import com.sqlchan.wenda.model.HostHolder;
import com.sqlchan.wenda.service.LikeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Administrator on 2017/7/14.
 */
@Controller
public class LikeController {
    private static final Logger logger= LoggerFactory.getLogger(IndexController.class);

    @Autowired
    HostHolder hostHolder;
    @Autowired
    LikeService likeService;

    @RequestMapping(path = "/like/{questionid}")
    //@ResponseBody
    public String like(@PathVariable("questionid") int questionid){
        long likecount=likeService.like(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, questionid);
        //return WendaUtil.getJSONString(0, String.valueOf(likecount));
        return "redirect:/question/"+questionid;
    }

    @RequestMapping(path = "/dislike/{questionid}")
    //@ResponseBody
    public String dislike(@PathVariable("questionid") int questionid){

        long likecount=likeService.disLike(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, questionid);
        //return WendaUtil.getJSONString(0, String.valueOf(likecount));
        return "redirect:/question/"+questionid;
    }

}