package com.lly.aliyun;

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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

@SpringBootTest
class AliyunSmsApplicationTests {
    @Autowired
    private AliyunSmsConfig aliyunSmsConfig;

    @Test
    void contextLoads() {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", aliyunSmsConfig.getAccessKeyId(), aliyunSmsConfig.getAccessSecret());
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers","15613432435");
        request.putQueryParameter("SignName","轻云商城");
        request.putQueryParameter("TemplateCode","SMS_203670789");
        request.putQueryParameter("TemplateParam","{\"code\":\"1111\"}");
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void test(){
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", aliyunSmsConfig.getAccessKeyId(), aliyunSmsConfig.getAccessSecret());
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("QuerySendDetails");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumber","15618745435");
        request.putQueryParameter("SendDate","20200926");
        request.putQueryParameter("CurrentPage","1");
        request.putQueryParameter("PageSize","10");
        try {
            CommonResponse response = client.getCommonResponse(request);
            JSONObject jsonObject = JSON.parseObject(response.getData());
            String Code = jsonObject.getString("Code");
            if(Code.equals("OK")){
                Integer TotalCount = jsonObject.getInteger("TotalCount");
                JSONObject SmsSendDetailDTOs = jsonObject.getJSONObject("SmsSendDetailDTOs");
                JSONArray SmsSendDetailDTO = SmsSendDetailDTOs.getJSONArray("SmsSendDetailDTO");
                System.out.println(SmsSendDetailDTO);
            }
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

}
