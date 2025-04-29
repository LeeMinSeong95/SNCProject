package com.SNC.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "kakao")
public class KakaoConfig {
    private String restApiKey; // Rest API APP KEY
    private String redirectUri; // API 페이지에서 생성한 redirectUri (인증 코드 받는 곳)
    private String clientSecret; //
}
