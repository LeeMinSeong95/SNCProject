package com.SNC.kakao.dto;

import lombok.Data;

@Data
public class KakaoDto {
    private String access_token;
    private String refresh_token;
    private long expires_in;
}
