package com.sqlchan.wenda.controller;

import com.sqlchan.wenda.model.EntityType;
import com.sqlchan.wenda.model.HostHolder;
import com.sqlchan.wenda.model.Question;
import com.sqlchan.wenda.model.ViewObject;
import com.sqlchan.wenda.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/1.
 */
@Controller
public class SearchController {
    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);
    @Autowired
    SearchService searchService;

    @Autowired
    FollowService followService;
    @Autowired
    LikeService likeService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    @RequestMapping(path = {"/search"}, method = {RequestMethod.GET})
    public String search(Model model, @RequestParam("q") String keyword,
                         @RequestParam(value = "offset", defaultValue = "0") int offset,
                         @RequestParam(value = "count", defaultValue = "10") int count) {
        try {
            List<Question> questionList = searchService.searchQuestion(keyword, offset, count,
                    "<font color=\"red\">", "</font>");
            List<ViewObject> vos=new ArrayList<>();
            for(Question question:questionList){
                Question q=questionService.getById(question.getId());
                ViewObject vo=new ViewObject();
                if(question.getContent()!=null){
                    q.setContent(question.getContent());
                }
                if(question.getTitle()!=null){
                    q.setTitle(question.getTitle());
                }
                vo.set("question",q);
                vo.set("user",userService.getUser(q.getUserId()));
                long likeCount=0;
                likeCount=likeService.getLikeCount(EntityType.ENTITY_QUESTION,q.getId());
                vo.set("likeCount",likeCount);
                int liked=0;
                if (hostHolder.getUser() != null) {
                    liked=likeService.getLikeStatus(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, q.getId());
                }
                vo.set("liked",liked);
                vos.add(vo);
            }
            model.addAttribute("vos", vos);
            model.addAttribute("keyword", keyword);
        } catch (Exception e) {
            logger.error("搜索评论失败" + e.getMessage());
        }
        return "result";
    }
}

