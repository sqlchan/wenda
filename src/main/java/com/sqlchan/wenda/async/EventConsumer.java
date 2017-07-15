package com.sqlchan.wenda.async;

import com.alibaba.fastjson.JSON;
import com.sqlchan.wenda.controller.IndexController;
import com.sqlchan.wenda.util.JedisAdapter;
import com.sqlchan.wenda.util.RedisKeyUtil;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/15.
 */
@Service
public class EventConsumer implements InitializingBean,ApplicationContextAware{
    private static final Logger logger= LoggerFactory.getLogger(IndexController.class);

    @Autowired
    JedisAdapter jedisAdapter;

    private Map<EventType,List<EventHandle>> config=new HashedMap();
    private ApplicationContext applicationContext;
    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String ,EventHandle> beans=applicationContext.getBeansOfType(EventHandle.class);
        if(beans!=null){
            for(Map.Entry<String,EventHandle> entry:beans.entrySet()){
                List<EventType> eventTypes=entry.getValue().getSupportEventTypes();

                for(EventType type:eventTypes){
                    if(!config.containsKey(type)){
                        config.put(type,new ArrayList<>());
                    }
                    config.get(type).add(entry.getValue());
                }
            }
        }
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    String key= RedisKeyUtil.getEventQueueKey();
                    List<String> events=jedisAdapter.brpop(0,key);
                    for(String message:events){
                        if(message.equals(key)){
                            continue;
                        }
                        //eventModel是从文本反序列化得到的，需要一个默认构造函数,通过反射得到的，先通过默认构造函数创建出对象，然后再将一些属性都设置好
                        EventModel eventModel= JSON.parseObject(message,EventModel.class);
                        if(!config.containsKey(eventModel.getType())){
                            logger.error("bu neng shi bie shijian");
                            continue;
                        }
                        for(EventHandle handle:config.get(eventModel.getType())){
                            handle.doHandle(eventModel);
                        }

                    }
                }
            }
        });
        thread.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }
}
