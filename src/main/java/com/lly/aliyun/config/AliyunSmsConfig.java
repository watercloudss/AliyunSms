package com.lly.aliyun.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: lly
 * @Date: 2020/9/26
 * @Description:
 */
@Component
@ConfigurationProperties("aliyun")
@Data
public class AliyunSmsConfig {
  private String accessKeyId;
  private String accessSecret;
}
