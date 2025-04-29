package com.SNC.stock.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class RequestAccessToken {
    @Value("${token.uri}")
    private String API_URL;// API 엔드포인트

    @Value("${token.grant_type}")
    private String GRANT_TYPE;

    // ⚠️ 여기에 본인의 실제 앱 키와 시크릿 키를 입력하세요
    @Value("${koreastock.api.key}")
    private String APP_KEY;

    @Value("${koreastock.api.secret}")
    private String SECRET_KEY;

    private final RestTemplate restTemplate;

    public RequestAccessToken() {
        this.restTemplate = new RestTemplate();
    }

    public String getToken() {
        // 요청 본문 생성
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("grant_type", GRANT_TYPE);
        requestBody.put("appkey", APP_KEY);
        requestBody.put("appsecret", SECRET_KEY);

        // HTTP 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // HTTP 요청 엔티티 생성
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        // API 호출
        ResponseEntity<Map> response = restTemplate.exchange(API_URL, HttpMethod.POST, requestEntity, Map.class);
        // 응답에서 approval_key 추출
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            String token = response.getBody().get("access_token").toString();
            log.info("Token 발급 성공:{}",token);
            return token;
        } else {
            throw new RuntimeException("웹소켓 접속키 발급 실패: " + response.getStatusCode());
        }
    }
}