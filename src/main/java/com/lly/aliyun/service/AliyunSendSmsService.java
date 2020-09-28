package com.lly.aliyun.service;

import com.alibaba.fastjson.JSONArray;
import com.lly.aliyun.entity.VerificationSendStatusEnum;
import com.sun.org.apache.bcel.internal.classfile.Code;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @Author: lly
 * @Date: 2020/9/26
 * @Description:
 */
public interface AliyunSendSmsService {
     VerificationSendStatusEnum sendVerificationCode(String phone) throws ExecutionException, InterruptedException;
     String getDateOfVerificationInfo(String CurrentPage, String PageSize, String SendDate, String PhoneNumber) throws InterruptedException, ExecutionException;
}
