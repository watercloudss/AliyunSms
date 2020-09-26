package com.lly.aliyun.utils;

import org.springframework.stereotype.Component;

/**
 * @Author: lly
 * @Date: 2020/9/26
 * @Description:
 */
@Component
public class CodeUtil {

    public String getCode(){
        String code =String.valueOf ((int)((Math.random()*9+1)*100000));
        return code;
    }
}
