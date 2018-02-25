package com.frank.simpleframework.test.controller;

import com.alibaba.fastjson.JSON;
import com.frank.simpleframework.annotation.*;
import com.frank.simpleframework.request.RequestMethod;
import com.frank.simpleframework.test.entity.Person;
import com.frank.simpleframework.test.service.TestService;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Frank （wx:F451209123） on 2017/12/17.
 */
@RequestController(path = "/test/")
public class TestController {
    @Inject
    private TestService testService;

    @RequestAction(value = "test",method = RequestMethod.GET)
    public void test(@RequestParameter(name = "msg",isRequired = true) String msg){
        System.out.println("msg参数是否为空："+testService.checkMsg(msg));
        System.out.println(msg);
    }

    @RequestAction(value = "test2",method = RequestMethod.GET)
    public String test2(@RequestParameter(name = "msg",isRequired = true) String msg){
        System.out.println(msg);
        return msg;
    }

    @RequestAction(value = "test3",method = RequestMethod.GET)
    public Map test3(@RequestParameter(name = "msg",isRequired = true) String msg) {
        System.out.println(msg);
        Map<String, Object> result = new HashMap<>();
        result.put("msg", msg);
        result.put("code", "1000");
        return result;
    }

    @RequestAction(value = "test4",method = RequestMethod.GET)
    public Map test4(@RequestParameter(name = "person",isRequired = true) Person person) {
        Map<String, Object> result = new HashMap<>();
        testService.checkMsg("aaaa");
        if(null == person){
            System.out.println("参数为null");
            result.put("msg", "参数为null");
            result.put("code", "1001");
        }else{
            result.put("msg", JSON.toJSONString(person));
            result.put("code", "1000");
        }
        return result;
    }
}
