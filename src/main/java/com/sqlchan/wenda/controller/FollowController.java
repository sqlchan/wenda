package com.sqlchan.wenda.controller;

import com.sqlchan.wenda.async.EventModel;
import com.sqlchan.wenda.async.EventProducer;
import com.sqlchan.wenda.async.EventType;
import com.sqlchan.wenda.model.*;
import com.sqlchan.wenda.service.CommentService;
import com.sqlchan.wenda.service.FollowService;
import com.sqlchan.wenda.service.QuestionService;
import com.sqlchan.wenda.service.UserService;
import com.sqlchan.wenda.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/15.
 */
@Controller
public class FollowController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    FollowService followService;

    @Autowired
    CommentService commentService;

    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping("/follower")
    public String follower(Model model){
        if (hostHolder.getUser() == null) {
            return "redirect:loginpage";
        }
        int   countfollow= (int) followService.getFolloweeCount(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION);

        List<Integer> followees=followService.getFollowees(hostHolder.getUser().getId(),EntityType.ENTITY_QUESTION,10);
        List<ViewObject> vos = new ArrayList<>();
        for(Integer followee:followees){
            ViewObject vo = new ViewObject();
            vo.set("questionId",followee);
            vo.set("question",questionService.getById(followee));
            vo.set("user",userService.getUser(questionService.getById(followee).getUserId()));
            vos.add(vo);
        }

        model.addAttribute("vos",vos);
        model.addAttribute("countfollow",countfollow);
        return "follower";
    }

    //    @RequestMapping("/followers")
//    public String followers(Model model){
//        if (hostHolder.getUser() == null) {
//            return "redirect:loginpage";
//        }
//        int   countfollow= (int) followService.getFollowerCount(EntityType.ENTITY_USER,hostHolder.getUser().getId());
//
//        List<Integer> followees=followService.getFollowers(EntityType.ENTITY_USER,hostHolder.getUser().getId(),10);
//        List<ViewObject> vos = new ArrayList<>();
//        for(Integer followee:followees){
//            ViewObject vo = new ViewObject();
//            vo.set("userId",followee);
//            vo.set("user",userService.getUser(followee));
//            vos.add(vo);
//        }
//
//        model.addAttribute("vos",vos);
//        model.addAttribute("countfollow",countfollow);
//        return "followers";
//    }
//
    @RequestMapping("/followee")
    public String followee(Model model){
        if (hostHolder.getUser() == null) {
            return "redirect:loginpage";
        }
        int   countfollow= (int) followService.getFolloweeCount(hostHolder.getUser().getId(), EntityType.ENTITY_USER);

        List<Integer> followees=followService.getFollowees(hostHolder.getUser().getId(),EntityType.ENTITY_USER,10);
        List<ViewObject> vos = new ArrayList<>();
        for(Integer followee:followees){
            ViewObject vo = new ViewObject();
            vo.set("userId",followee);
            vo.set("user",userService.getUser(followee));
            vos.add(vo);
        }

        model.addAttribute("vos",vos);
        model.addAttribute("countfollow",countfollow);
        return "followee";
    }

    @RequestMapping("/followees")
    public String followees(Model model){
        if (hostHolder.getUser() == null) {
            return "redirect:loginpage";
        }
        int   countfollow= (int) followService.getFollowerCount(EntityType.ENTITY_USER,hostHolder.getUser().getId());

        List<Integer> followees=followService.getFollowers(EntityType.ENTITY_USER,hostHolder.getUser().getId(),10);
        List<ViewObject> vos = new ArrayList<>();
        for(Integer followee:followees){
            ViewObject vo = new ViewObject();
            vo.set("userId",followee);
            vo.set("user",userService.getUser(followee));
            vos.add(vo);
        }

        model.addAttribute("vos",vos);
        model.addAttribute("countfollow",countfollow);
        return "followees";
    }


    @RequestMapping(path = {"/followUser/{userId}"}, method = {RequestMethod.POST, RequestMethod.GET})
    //@ResponseBody
    public String followUser(@PathVariable("userId") int userId) {
        if (hostHolder.getUser() == null) {
            return "redirect:loginpage";
        }

        boolean ret = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId);

        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                .setActorId(hostHolder.getUser().getId()).setEntityId(userId)
                .setEntityType(EntityType.ENTITY_USER).setEntityOwnerId(userId));

        boolean isfollower =followService.isFollower(hostHolder.getUser().getId(),EntityType.ENTITY_USER,userId);
        return "redirect:/";
        // 返回关注的人数
        //return WendaUtil.getJSONString(ret ? 0 : 1, String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(), EntityType.ENTITY_USER)));
    }

    @RequestMapping(path = {"/unfollowUser/{userId}"})
    //@ResponseBody
    public String unfollowUser(@PathVariable("userId") int userId) {
        if (hostHolder.getUser() == null) {
            return WendaUtil.getJSONString(999);
        }

        boolean ret = followService.unfollow(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId);

        eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW)
                .setActorId(hostHolder.getUser().getId()).setEntityId(userId)
                .setEntityType(EntityType.ENTITY_USER).setEntityOwnerId(userId));

        return "redirect:/";
        // 返回关注的人数
        //return WendaUtil.getJSONString(ret ? 0 : 1, String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(), EntityType.ENTITY_USER)));
    }

    @RequestMapping(path = {"/followQuestion/{questionId}"})
    //@ResponseBody
    public String followQuestion(@PathVariable("questionId") int questionId) {
        if (hostHolder.getUser() == null) {
            return "redirect:/loginpage";
        }

        Question q = questionService.getById(questionId);
        if (q == null) {
            return "redirect:/";
        }

        boolean ret = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, questionId);

        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                .setActorId(hostHolder.getUser().getId()).setEntityId(questionId)
                .setEntityType(EntityType.ENTITY_QUESTION).setEntityOwnerId(q.getUserId()));

//        Map<String, Object> info = new HashMap<>();
//        info.put("headUrl", hostHolder.getUser().getHeadUrl());
//        info.put("name", hostHolder.getUser().getName());
//        info.put("id", hostHolder.getUser().getId());
//        info.put("count", followService.getFollowerCount(EntityType.ENTITY_QUESTION, questionId));
        return "redirect:/";
        //return WendaUtil.getJSONString(ret ? 0 : 1, info);
    }

    @RequestMapping(path = {"/unfollowQuestion/{questionId}"})
    //@ResponseBody
    public String unfollowQuestion(@PathVariable("questionId") int questionId) {
        if (hostHolder.getUser() == null) {
            return "redirect:/loginpage";
            //return WendaUtil.getJSONString(999);
        }

        Question q = questionService.getById(questionId);
        if (q == null) {
            return "redirect:/";
            //return WendaUtil.getJSONString(1, "问题不存在");
        }

        boolean ret = followService.unfollow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, questionId);

        eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW)
                .setActorId(hostHolder.getUser().getId()).setEntityId(questionId)
                .setEntityType(EntityType.ENTITY_QUESTION).setEntityOwnerId(q.getUserId()));

//        Map<String, Object> info = new HashMap<>();
//        info.put("id", hostHolder.getUser().getId());
//        info.put("count", followService.getFollowerCount(EntityType.ENTITY_QUESTION, questionId));
        return "redirect:/";
        //return WendaUtil.getJSONString(ret ? 0 : 1, info);
    }

    @RequestMapping(path = {"/user/{uid}/followers"}, method = {RequestMethod.GET})
    public String followers(Model model, @PathVariable("uid") int userId) {
        List<Integer> followerIds = followService.getFollowers(EntityType.ENTITY_USER, userId, 0, 10);
        if (hostHolder.getUser() != null) {
            model.addAttribute("followers", getUsersInfo(hostHolder.getUser().getId(), followerIds));
        } else {
            model.addAttribute("followers", getUsersInfo(0, followerIds));
        }
        model.addAttribute("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
        model.addAttribute("curUser", userService.getUser(userId));
        return "followers";
    }

    @RequestMapping(path = {"/user/{uid}/followees"}, method = {RequestMethod.GET})
    public String followees(Model model, @PathVariable("uid") int userId) {
        List<Integer> followeeIds = followService.getFollowees(userId, EntityType.ENTITY_USER, 0, 10);

        if (hostHolder.getUser() != null) {
            model.addAttribute("followees", getUsersInfo(hostHolder.getUser().getId(), followeeIds));
        } else {
            model.addAttribute("followees", getUsersInfo(0, followeeIds));
        }
        model.addAttribute("followeeCount", followService.getFolloweeCount(userId, EntityType.ENTITY_USER));
        model.addAttribute("curUser", userService.getUser(userId));
        return "followees";
    }

    private List<ViewObject> getUsersInfo(int localUserId, List<Integer> userIds) {
        List<ViewObject> userInfos = new ArrayList<ViewObject>();
        for (Integer uid : userIds) {
            User user = userService.getUser(uid);
            if (user == null) {
                continue;
            }
            ViewObject vo = new ViewObject();
            vo.set("user", user);
            vo.set("commentCount", commentService.getUserCommentCount(uid));
            vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, uid));
            vo.set("followeeCount", followService.getFolloweeCount(uid, EntityType.ENTITY_USER));
            if (localUserId != 0) {
                vo.set("followed", followService.isFollower(localUserId, EntityType.ENTITY_USER, uid));
            } else {
                vo.set("followed", false);
            }
            userInfos.add(vo);
        }
        return userInfos;
    }


}