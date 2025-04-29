package com.SNC.kakao.service;

import com.SNC.common.config.KakaoConfig;
import com.SNC.kakao.dto.KakaoDto;
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

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class KakaoAuthService {
    private final KakaoConfig kakaoConfig;
    RestTemplate restTemplate = new RestTemplate();

    private static final String TOKEN_URL = "https://kauth.kakao.com/oauth/token";

    // 발급 받은 토큰들로 자동화
    private KakaoDto requestToken(String grantType, MultiValueMap<String, String> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    TOKEN_URL,
                    request,
                    Map.class
            );

            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null) {
                KakaoDto kakaoDto = new KakaoDto();
                kakaoDto.setAccess_token((String) responseBody.get("access_token"));
                kakaoDto.setRefresh_token((String) responseBody.get("refresh_token"));
                // ⭐ expires_in은 Integer일 수도 있으므로 Number로 받고 longValue() 사용
                Number expiresInNumber = (Number) responseBody.get("expires_in");
                kakaoDto.setExpires_in(expiresInNumber.longValue());
                return kakaoDto;
            }
        } catch (Exception e) {
            log.error("Error occurred during token request: {}", e.getMessage());
        }

        return null;
    }

    // Access Token 발급 메서드
    public KakaoDto getAccessToken(String code) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoConfig.getRestApiKey());
        body.add("redirect_uri", kakaoConfig.getRedirectUri());
        body.add("code", code);
        body.add("client_secret", kakaoConfig.getClientSecret());

        return requestToken("authorization_code", body);
    }

    // Refresh Token 발급 메서드
    public String refreshAccessToken(String refreshToken) {
        if (refreshToken == null) return null;

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", kakaoConfig.getRestApiKey());
        body.add("refresh_token", refreshToken);

        KakaoDto kakaoDto = requestToken("refresh_token", body);
        return kakaoDto != null ? kakaoDto.getAccess_token() : null;
    }
}
