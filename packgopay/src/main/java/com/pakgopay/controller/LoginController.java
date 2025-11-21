package com.pakgopay.controller;

import com.pakgopay.entity.TestMessage;
import com.pakgopay.entity.User;
import com.pakgopay.service.TestMq;
import com.pakgopay.service.impl.UserService;
import com.pakgopay.thirdUtil.RedisUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pakGoPay/server/Login")
public class TestController {

    private static Logger logger = LogManager.getLogger("RollingFile");

    @Autowired
    private TestMq testMq;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserService userService;


    @RequestMapping(value = "/hello")
    public String test(){
        System.out.println("this is hello test");
        TestMessage testMessage = new TestMessage();
        testMessage.setContent("hello world");
        testMq.send("test", testMessage);
        testMessage.setContent("这是一个延迟消息");
        testMq.sendDelay("delay-test", testMessage);
        return "{'zf':'test'}";
    }

    @GetMapping(value = "/login")
    public String test2(){
        System.out.println("test2");
        redisUtil.setKey("test", "this is a test message from redis");
        Object test = redisUtil.getValue("test");
        System.out.println(test);
        return "test2";
    }

    @RequestMapping(value = "db")
    public String test3(){
        User user = userService.selectAllUser();
        System.out.println(user);
        return "test3";
    }
}
