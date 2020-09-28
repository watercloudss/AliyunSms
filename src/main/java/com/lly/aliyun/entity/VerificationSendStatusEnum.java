package com.lly.aliyun.entity;

import lombok.*;

/**
 * @Author: lly
 * @Date: 2020/9/26
 * @Description:
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum VerificationSendStatusEnum {
    SEND_SUCCESS("请求发送验证码成功",1),
    SEND_FAIL("请求发送验证码发送失败",2),
    SEND_ISEXIT("验证码已发送,稍后再试",3);
    private String name;
    private Integer status;
}
