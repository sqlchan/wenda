package com.sqlchan.wenda.aspect;

import com.sqlchan.wenda.controller.IndexController;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by Administrator on 2017/6/28.
 */
@Aspect
@Component
public class LogAspect {
    private static final Logger logger= LoggerFactory.getLogger(IndexController.class);

    @Before("execution(* com.sqlchan.wenda.controller.IndexController.*(..))")
    public void beforeMethod(){
        logger.info("before method"+new Date());
    }

    @After("execution(* com.sqlchan.wenda.controller.IndexController.*(..))")
    public void afterMethod(){
        logger.info("after method"+new Date());
    }

}
