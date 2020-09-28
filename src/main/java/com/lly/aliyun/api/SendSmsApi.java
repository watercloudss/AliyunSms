package com.lly.aliyun.api;

import com.lly.aliyun.service.AliyunSendSmsService;
import com.lly.aliyun.utils.NumberUtil;
import com.lly.aliyun.utils.Result;
import com.lly.aliyun.utils.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;


/**
 * @Author: lly
 * @Date: 2020/9/26
 * @Description:
 */
@RestController
@RequestMapping("/api/v1")
public class SendSmsApi {
    @Autowired
    private NumberUtil numberUtil;
    @Autowired
    private AliyunSendSmsService aliyunSendSmsService;

    @GetMapping("/sms/verification/{phone}")
    public Result  sendVerificationSms(@PathVariable String phone) throws ExecutionException, InterruptedException {
        if(!numberUtil.isPhone(phone)){
            return ResultGenerator.genFailResult("手机号码有误！");
        }
        return ResultGenerator.genSuccessResult(aliyunSendSmsService.sendVerificationCode(phone).getName());
    }

    @GetMapping("/sms/getDateOfVerificationInfo")
    public Result  getDateOfVerificationInfo(@RequestParam(defaultValue = "1") String CurrentPage,@RequestParam(defaultValue = "10") String PageSize,
                                             @RequestParam  String SendDate,@RequestParam String PhoneNumber) throws ExecutionException, InterruptedException {
        return ResultGenerator.genSuccessResult(aliyunSendSmsService.getDateOfVerificationInfo(CurrentPage,PageSize,SendDate,PhoneNumber));
    }
}

