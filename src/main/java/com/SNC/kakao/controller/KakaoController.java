package com.SNC.kakao.controller;

import com.SNC.common.config.KakaoConfig;
import com.SNC.kakao.dto.KakaoDto;
import com.SNC.kakao.service.KakaoAuthService;
import com.SNC.kakao.service.KakaoSendService;
import com.SNC.login.model.CustomUserDetails;
import com.SNC.register.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/kakao")
public class KakaoController {

    private final KakaoAuthService kakaoAuthService;
    private final KakaoSendService kakaoSendService;
    private final KakaoConfig kakaoConfig;
    private final MemberMapper memberMapper;

    /*
    * 회원들이 카카오 연동 버튼 누르면 맵핑 되는 url
    * 엑세스 토큰과, 리프레쉬 토큰을 받기위한 절차
    * 이곳에 들어오면 회원들이 카카오 로그인 후 바로 /kakao/oauth 로 리다이랙트
    **/
    @GetMapping("/login")
    public ResponseEntity<String> login() {
        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize?"
                + "client_id=" + kakaoConfig.getRestApiKey()
                + "&redirect_uri=" + kakaoConfig.getRedirectUri() // 이 함수가 작동하면 우리가 설정했던 이 url로 넘어가는 것이다.
                + "&response_type=code";
        return ResponseEntity.status(302).header("Location", kakaoAuthUrl).build();
    }

    /*
    * 카카오 로그인 시 받는 인증 코드를 받아서 getAccessToken 함수로 보낸다.
    **/
    @GetMapping("/oauth")
    public ResponseEntity<String> oauthRedirect(@RequestParam String code,
                                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        KakaoDto kakaoDto = kakaoAuthService.getAccessToken(code);
        if (kakaoDto == null || kakaoDto.getAccess_token() == null) {
            log.error("Access Token Fail : {}", kakaoDto);
            return ResponseEntity.badRequest().body("Access Token 실패");
        }

        String mbrCd = userDetails.getUserCd();  // 로그인한 회원
        if (mbrCd == null) {
            return ResponseEntity.badRequest().body("회원 인증 정보가 없습니다.");
        }

        // 1. 현재 시간 + expires_in 초를 계산해서 Date 객체로 변환
        Date now = new Date();
        long expiresInMillis = kakaoDto.getExpires_in() * 1000L;
        Date expireTime = new Date(now.getTime() + expiresInMillis);

        // 2. updateKakaoToken 호출
        memberMapper.updateKakaoToken(
                mbrCd,
                kakaoDto.getAccess_token(),
                kakaoDto.getRefresh_token(),
                expireTime // 형변환 Date 타입으로 전달
        );

        // 카카오 연동 메시지 전송
        String msg = "🎉 카카오톡 연동이 완료되었습니다!";
        kakaoSendService.sendToMe(kakaoDto.getAccess_token(), msg);

        return ResponseEntity.ok(
                "<html>" +
                        "<head><title>카카오 연동 완료</title></head>" +
                        "<body>" +
                        "<h2>카카오 연동 및 토큰 저장 완료!</h2>" +
                        "<a href='http://localhost:8090/favorite/top5'>즐겨찾기로 돌아가기</a>" +
                        "</body>" +
                        "</html>"
        );
    }

}
