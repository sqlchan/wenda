package com.sqlchan.wenda.service;

import com.sqlchan.wenda.dao.UserDAO;
import com.sqlchan.wenda.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2017/6/28.
 */
@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    UserDAO userDAO;

    public User selectByName(String name) {
        return userDAO.selectByName(name);
    }

    public User getUser(int id) {
        return userDAO.selectById(id);
    }

}
