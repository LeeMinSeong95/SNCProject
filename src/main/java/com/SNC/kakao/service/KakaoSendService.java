package com.SNC.kakao.service;

import com.SNC.register.mapper.MemberMapper;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoSendService {

    private final MemberMapper memberMapper;

    // accessToken을 사용하여 연동 성공 시 본인에게 메시지 전송
    public void sendToMe(String token, String msg) {
        // 1. 템플릿 JSON 구성
        Map<String, Object> template = new HashMap<>();
        template.put("object_type", "text");
        template.put("text", msg);
        template.put("link", Map.of(
                "web_url", "http://localhost:8090/main/home",
                "mobile_web_url", "http://localhost:8090/main/home"
        ));
        template.put("button_title", "SNC 홈페이지 바로가기");

        // 2. JSON 문자열로 변환 (Kakao가 요구함)
        String templateObjectJson = new Gson().toJson(template);

        // 3. HTTP 요청 헤더 세팅
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED); // ❗ 중요
        headers.set("Authorization", "Bearer " + token);

        // 4. form-urlencoded 파라미터로 구성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("template_object", templateObjectJson);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

        String url = "https://kapi.kakao.com/v2/api/talk/memo/default/send";

        // 5. RestTemplate로 전송
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            log.info("✅ KakaoTalk send success: {}", response.getBody());
        } catch (Exception e) {
            log.error("❌ KakaoTalk send fail: {}", e.getMessage());
        }
    }

    public void sendKakaoAlert(String mbrCd, String message) {
        String accessToken = memberMapper.findAccessTokenByMbrCd(mbrCd);
        if (accessToken == null) {
            log.error("kakao_talk token null : {}", mbrCd);
            return;
        }

        // 1. template_object 구성 (JSON String)
        Map<String, Object> template = new HashMap<>();
        template.put("object_type", "text");
        template.put("text", message);
        template.put("link", Map.of(
                "web_url", "http://localhost:8090/main/home",
                "mobile_web_url", "http://localhost:8090/main/home"
        ));
        template.put("button_title", "SNC 홈페이지 바로가기");

        String templateJson = new Gson().toJson(template); // ⚠️ template_object 값!

        // 2. HTTP 요청 준비
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Bearer " + accessToken);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("template_object", templateJson); // JSON string

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        String url = "https://kapi.kakao.com/v2/api/talk/memo/default/send";

        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            log.info("✅ KakaoTalk alert send success : {}", response.getBody());
        } catch (Exception e) {
            log.error("❌ KakaoTalk alert send fail : {}", e.getMessage());
        }
    }


}
