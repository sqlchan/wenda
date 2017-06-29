package com.sqlchan.wenda.service;

import com.sqlchan.wenda.dao.LoginTicketDAO;
import com.sqlchan.wenda.dao.UserDAO;
import com.sqlchan.wenda.model.LoginTicket;
import com.sqlchan.wenda.model.User;
import com.sqlchan.wenda.util.WendaUtil;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Created by Administrator on 2017/6/28.
 */
@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    UserDAO userDAO;
    @Autowired
    LoginTicketDAO loginTicketDAO;

    public Map<String,String> register(String name,String password){
        Map<String,String> map=new HashedMap();
        if(StringUtils.isBlank(name)){
            map.put("msg","yong hu ming kong");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("msg","mima kong");
            return map;
        }
        User user=userDAO.selectByName(name);
        if(user!=null){
            map.put("msg","yi zhu ce");
            return map;
        }
        user=new User();
        user.setName(name);
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        String head = String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000));
        user.setHeadUrl(head);
        user.setPassword(WendaUtil.MD5(password+user.getSalt()));
        userDAO.addUser(user);

        String ticket = addloginticket(user.getId());
        map.put("ticket", ticket);
        return map;
    }


    public Map<String,Object> login(String name,String password){
        Map<String,Object> map=new HashedMap();
        if(StringUtils.isBlank(name)){
            map.put("msg","yong hu ming kong");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("msg","mima kong");
            return map;
        }
        User user=userDAO.selectByName(name);
        if(user==null){
            map.put("msg","bu cun zai");
            return map;
        }
        if(!WendaUtil.MD5(password+user.getSalt()).equals(user.getPassword())){
            map.put("msg","mima cun wu");
            return map;
        }

        String ticket=addloginticket(user.getId());
        map.put("ticket",ticket);

        return map;
    }

    private String addloginticket(int userid){
        LoginTicket ticket=new LoginTicket();
        ticket.setUserId(userid);
        Date date=new Date();
        date.setTime(date.getTime()+1000*3600*24);
        ticket.setExpired(date);
        ticket.setStatus(0);
        ticket.setTicket(UUID.randomUUID().toString().replaceAll("-",""));
        loginTicketDAO.addTicket(ticket);
        return ticket.getTicket();
    }

    public User selectByName(String name) {
        return userDAO.selectByName(name);
    }

    public void logout(String ticket) {
        loginTicketDAO.updateStatus(ticket, 1);
    }

    public User getUser(int id) {
        return userDAO.selectById(id);
    }

}
