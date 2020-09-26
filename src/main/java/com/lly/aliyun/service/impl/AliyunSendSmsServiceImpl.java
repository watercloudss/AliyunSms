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
import java.util.concurrent.TimeUnit;

/**
 * @Author: lly
 * @Date: 2020/9/26
 * @Description:
 */
@Service
public class AliyunSendSmsServiceImpl implements AliyunSendSmsService {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private CodeUtil codeUtil;
    @Autowired
    private AliyunSmsConfig aliyunSmsConfig;

    @Override
    public VerificationSendStatusEnum sendVerificationCode(String phone) {
        String code = (String) redisUtil.get(phone);
        if(StringUtils.isEmpty(code)){
          String verificationCode =  codeUtil.getCode();
          boolean setStatus = redisUtil.set(phone,verificationCode,300);
          if(setStatus){
              Map<String, Object> templateParam = new HashMap<>();
              templateParam.put("code",verificationCode);
              boolean result = this.sendSms(phone,"SMS_203670789",templateParam);
              try {
                  //模拟异步，先发短信，睡10秒再返回值
                  TimeUnit.SECONDS.sleep(10);
              } catch (InterruptedException e) {
                  e.printStackTrace();
              }
              return VerificationSendStatusEnum.SEND_SUCCESS;
          }else{
              return VerificationSendStatusEnum.SEND_FAIL;
          }
        }else{
            return VerificationSendStatusEnum.SEND_ISEXIT;
        }
    }

    @Override
    public JSONArray getDateOfVerificationInfo(String CurrentPage, String PageSize, String SendDate, String PhoneNumber) {
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
            JSONObject jsonObject = JSON.parseObject(response.getData());
            String Code = jsonObject.getString("Code");
            if(Code.equals("OK")){
                Integer TotalCount = jsonObject.getInteger("TotalCount");
                JSONObject SmsSendDetailDTOs = jsonObject.getJSONObject("SmsSendDetailDTOs");
                JSONArray SmsSendDetailDTO = SmsSendDetailDTOs.getJSONArray("SmsSendDetailDTO");
                return SmsSendDetailDTO;
            }
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Async
    @Override
    public boolean sendSms(String phone, String templateCode, Map<String,Object> code){
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
        request.putQueryParameter("TemplateParam", JSON.toJSONString(code));
        try {
            CommonResponse response = client.getCommonResponse(request);
            return response.getHttpResponse().isSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
