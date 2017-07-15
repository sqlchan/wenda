package com.sqlchan.wenda.async.handler;

import com.sqlchan.wenda.async.EventHandle;
import com.sqlchan.wenda.async.EventModel;
import com.sqlchan.wenda.async.EventType;
import com.sqlchan.wenda.model.Message;
import com.sqlchan.wenda.model.User;
import com.sqlchan.wenda.service.MessageService;
import com.sqlchan.wenda.service.UserService;
import com.sqlchan.wenda.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/7/15.
 */
@Component
public class LikeHandler implements EventHandle{
    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;

    @Override
    public void doHandle(EventModel model) {
        Message message=new Message();
        message.setFromId(WendaUtil.SYSTEM_USERID);
        message.setToId(model.getEntityOwnerId());
        message.setCreatedDate(new Date());
        User user=userService.getUser(model.getActorId());
        message.setContent("用户 "+user.getName()+" 赞了你的评论, id"+model.getEntityId());



        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}
