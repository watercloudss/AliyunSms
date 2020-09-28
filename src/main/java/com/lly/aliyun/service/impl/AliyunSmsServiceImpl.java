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
import com.lly.aliyun.entity.VerificationSendStatusEnum;
import com.lly.aliyun.service.AliyunSendSmsService;
import com.lly.aliyun.utils.CodeUtil;
import com.lly.aliyun.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @Author: lly
 * @Date: 2020/9/26
 * @Description:
 */
@Service
public class AliyunSmsServiceImpl implements AliyunSendSmsService {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private AsyncSendAliyunSmsService asyncSendAliyunSmsService;


    @Override
    public VerificationSendStatusEnum sendVerificationCode(String phone) throws ExecutionException, InterruptedException {
        String code = (String) redisUtil.get(phone);
        if(StringUtils.isEmpty(code)){
              CompletableFuture<Boolean> result = asyncSendAliyunSmsService.sendSms(phone,"SMS_203670789");
            return VerificationSendStatusEnum.SEND_SUCCESS;
        }else{
            return VerificationSendStatusEnum.SEND_ISEXIT;
        }
    }

    @Override
    public JSONArray getDateOfVerificationInfo(String CurrentPage, String PageSize, String SendDate, String PhoneNumber) throws InterruptedException, ExecutionException {
        CompletableFuture<JSONArray> result = asyncSendAliyunSmsService.getDateOfVerificationInfo(CurrentPage,PageSize,SendDate,PhoneNumber);
        CompletableFuture<String> message = asyncSendAliyunSmsService.doSomething("oh shit!");
        CompletableFuture<String> message1 = asyncSendAliyunSmsService.doSomething1("oh shit!");
        CompletableFuture.allOf(result,message,message1).join();// 等待所有任务都执行完
        return result.get();
    }

}
