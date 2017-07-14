package com.sqlchan.wenda.controller;

import com.sqlchan.wenda.model.HostHolder;
import com.sqlchan.wenda.model.Message;
import com.sqlchan.wenda.model.User;
import com.sqlchan.wenda.model.ViewObject;
import com.sqlchan.wenda.service.MessageService;
import com.sqlchan.wenda.service.UserService;
import com.sqlchan.wenda.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/6/30.
 */
@Controller
public class MessageController {
    @Autowired
    HostHolder hostHolder;
    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;
    private static final Logger logger= LoggerFactory.getLogger(IndexController.class);

    @RequestMapping(value = "/msg/list")
    public String getConversation(Model model){
        try {
            int localUserId = hostHolder.getUser().getId();
            List<ViewObject> conversations = new ArrayList<ViewObject>();
            List<Message> conversationList = messageService.getConversationList(localUserId, 0, 10);
            for (Message msg : conversationList) {
                ViewObject vo = new ViewObject();
                vo.set("conversation", msg);
                int targetId = msg.getFromId() == localUserId ? msg.getToId() : msg.getFromId();
                User user = userService.getUser(targetId);
                vo.set("user", user);
                vo.set("unread", messageService.getConvesationUnreadCount(localUserId, msg.getConversationId()));
                conversations.add(vo);
            }
            model.addAttribute("conversations", conversations);

        }catch (Exception e){
            logger.error("msg list wrong");
        }
                return "letter";
    }

    @RequestMapping(value = "/msg/detail/{conversationId}")
    public String conversationDetail(Model model, @PathVariable("conversationId") String conversationId) {
        try {
            List<Message> conversationList = messageService.getConversationDetail(conversationId, 0, 10);
            List<ViewObject> messages = new ArrayList<>();
            for (Message msg : conversationList) {
                ViewObject vo = new ViewObject();
                vo.set("message", msg);
                User user = userService.getUser(msg.getFromId());
                if (user == null) {
                    continue;
                }
                vo.set("headUrl", user.getHeadUrl());
                vo.set("userId", user.getId());
                vo.set("user",user);
                messages.add(vo);
            }
            model.addAttribute("messages", messages);
        } catch (Exception e) {
            logger.error("获取详情消息失败" + e.getMessage());
        }
        return "letterDetail";
    }


    @RequestMapping(value = "/msg/addMessage",method = RequestMethod.POST)
    //@ResponseBody
    public String addmessage(@RequestParam("toName")String toName,
                             @RequestParam("content")String content){
        try {
            Message message=new Message();
            message.setCreatedDate(new Date());
            message.setFromId(hostHolder.getUser().getId());
            message.setContent(content);
            message.setToId(userService.selectByName(toName).getId());
            messageService.addMessage(message);
            return "redirect:/msg/list";
            //return WendaUtil.getJSONString(0);

        }catch (Exception e){
            logger.error("xiaoxi shibai");
            return WendaUtil.getJSONString(1,"wrong");
        }
    }

    @RequestMapping("/addmessagepage")
    public String addmessagepage(){
        return "addmessagepage";
    }

}
