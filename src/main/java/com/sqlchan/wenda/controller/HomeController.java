package com.sqlchan.wenda.controller;

import com.sqlchan.wenda.model.EntityType;
import com.sqlchan.wenda.model.HostHolder;
import com.sqlchan.wenda.model.Question;
import com.sqlchan.wenda.model.ViewObject;
import com.sqlchan.wenda.service.LikeService;
import com.sqlchan.wenda.service.QuestionService;
import com.sqlchan.wenda.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/28.
 */
@Controller
public class HomeController {
    private static final Logger logger= LoggerFactory.getLogger(IndexController.class);

    @Autowired
    UserService userService;
    @Autowired
    QuestionService questionService;
    @Autowired
    LikeService likeService;
    @Autowired
    HostHolder hostHolder;

    @RequestMapping("/")
    public String index(Model model){
        model.addAttribute("vos",getQuestion(0,0,10));
        return "home";
    }

    @RequestMapping("/user/{userid}")
    public String userindex(Model model, @PathVariable("userid")int userid){
        model.addAttribute("vos",getQuestion(userid,0,10));
        return "home";
    }

    private List<ViewObject> getQuestion(int userid,int offset,int limit){
        List<Question> questionList=questionService.getLatestQuestions(userid,0,10);
        List<ViewObject> vos=new ArrayList<>();
        for(Question question:questionList){
            ViewObject vo=new ViewObject();
            vo.set("question",question);
            vo.set("user",userService.getUser(question.getUserId()));
            long likeCount=0;
            likeCount=likeService.getLikeCount(EntityType.ENTITY_QUESTION,question.getId());
            vo.set("likeCount",likeCount);
            int liked=0;
            if (hostHolder.getUser() != null) {
                liked=likeService.getLikeStatus(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, question.getId());
            }
            vo.set("liked",liked);
            vos.add(vo);
        }
        return vos;
    }
}
