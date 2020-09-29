package com.lly.aliyun.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.lly.aliyun.config.AliyunSmsConfig;
import com.lly.aliyun.utils.CodeUtil;
import com.lly.aliyun.utils.RedisUtil;
import com.sun.el.parser.AstMinus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @Author: lly
 * @Date: 2020/9/29
 * @Description:
 */
@Slf4j
@Service
public class AsyncSendAliyunSmsService {
    @Autowired
    private CodeUtil codeUtil;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private AliyunSmsConfig aliyunSmsConfig;

//    @Async注解会在以下几个场景失效，也就是说明明使用了@Async注解，但就没有走多线程。
//
//    异步方法使用static关键词修饰；
//    异步类不是一个Spring容器的bean（一般使用注解@Component和@Service，并且能被Spring扫描到）；
//    SpringBoot应用中没有添加@EnableAsync注解；
//    在同一个类中，一个方法调用另外一个有@Async注解的方法，注解不会生效。原因是@Async注解的方法，是在代理类中执行的。

    @Async("io")
    public CompletableFuture<Boolean> sendSms(String phone, String templateCode){
        String verificationCode =  codeUtil.getCode();
        Map<String, Object> templateParam = new HashMap<>();
        templateParam.put("code",verificationCode);

        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", aliyunSmsConfig.getAccessKeyId(), aliyunSmsConfig.getAccessSecret());
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers",phone);
        request.putQueryParameter("SignName","轻云商城");
        request.putQueryParameter("TemplateCode",templateCode);
        request.putQueryParameter("TemplateParam", JSON.toJSONString(templateParam));
        try {
            TimeUnit.SECONDS.sleep(10);//模拟耗时操作
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
            System.out.println(response.getHttpResponse().isSuccess());
            if(response.getHttpResponse().isSuccess()){
                boolean setStatus = redisUtil.set(phone,verificationCode,300);
            }
            return  CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            e.printStackTrace();
            return CompletableFuture.completedFuture(false);
        }
    }

    @Async("io")
    public CompletableFuture<JSONArray> getDateOfVerificationInfo(String CurrentPage, String PageSize, String SendDate, String PhoneNumber) {
        log.info("do findsms: {}", new Date());
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", aliyunSmsConfig.getAccessKeyId(), aliyunSmsConfig.getAccessSecret());
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("QuerySendDetails");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumber",PhoneNumber);
        request.putQueryParameter("SendDate",SendDate);
        request.putQueryParameter("CurrentPage",CurrentPage);
        request.putQueryParameter("PageSize",PageSize);
        try {
            CommonResponse response = client.getCommonResponse(request);
            if(response.getHttpResponse().isSuccess()){
                JSONObject jsonObject = JSON.parseObject(response.getData());
                JSONObject SmsSendDetailDTOs = jsonObject.getJSONObject("SmsSendDetailDTOs");
                JSONArray SmsSendDetailDTO = SmsSendDetailDTOs.getJSONArray("SmsSendDetailDTO");
                log.info("信息查询完毕！{}"+new Date());
                return CompletableFuture.completedFuture(SmsSendDetailDTO);
            }else{
                return CompletableFuture.completedFuture(null);
            }
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return null;
    }

    //模拟一个耗时操作
    @Async("cpu")
    public CompletableFuture<String> doSomething(String message) throws InterruptedException {
        log.info("do something: {}", new Date());
        TimeUnit.SECONDS.sleep(3);
        log.info("doSomething完毕！{}"+new Date());
        return CompletableFuture.completedFuture("do something: " + message);
    }

    //模拟一个耗时操作
    @Async("cpu")
    public CompletableFuture<String> doSomething1(String message) throws InterruptedException {
        log.info("do something1: {}", new Date());
        TimeUnit.SECONDS.sleep(5);
        log.info("doSomething1完毕！{}"+new Date());
        return CompletableFuture.completedFuture("do something: " + message);
    }

}
