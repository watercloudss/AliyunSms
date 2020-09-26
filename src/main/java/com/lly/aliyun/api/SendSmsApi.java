package com.lly.aliyun.api;

import com.lly.aliyun.service.AliyunSendSmsService;
import com.lly.aliyun.utils.Result;
import com.lly.aliyun.utils.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @Author: lly
 * @Date: 2020/9/26
 * @Description:
 */
@RestController
@RequestMapping("/api/v1")
public class SendSmsApi {
    @Autowired
    private AliyunSendSmsService aliyunSendSmsService;

    @GetMapping("/sms/verification/{phone}")
    public Result  sendVerificationSms(@PathVariable String phone){
        return ResultGenerator.genSuccessResult(aliyunSendSmsService.sendVerificationCode(phone).getName());
    }

    @GetMapping("/sms/getDateOfVerificationInfo")
    public Result  getDateOfVerificationInfo(@RequestParam(defaultValue = "1") String CurrentPage,@RequestParam(defaultValue = "10") String PageSize,
                                             @RequestParam  String SendDate,@RequestParam String PhoneNumber){
        return ResultGenerator.genSuccessResult(aliyunSendSmsService.getDateOfVerificationInfo(CurrentPage,PageSize,SendDate,PhoneNumber));
    }
}
