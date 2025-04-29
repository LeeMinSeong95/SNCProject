package com.SNC.register.mapper;

import com.SNC.register.dto.MemberDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

@Mapper
public interface MemberMapper {

    // 회원가입
    void memberInsert(MemberDto memberDto);
    //이메일로 회원정보 찾기
    MemberDto getByUserId(@Param("memberId") String memberId);
    //이메일 중복 검증
    String findByEmail(@Param("email") String email);
    //Id 중복 검증
    String findById(@Param("memberId") String memberId);

    String findByNickName(@Param("mbrNickName") String mbrNickName);


    // 알림 (회원 이메일 정보 조회) - EmailSendService
    String findEmailByMember(String mbrCd);
    // 알림 (회원 닉네임 정보 조회) - AlertCheckService
    String findNicknameByMbrCd(String mbrCd);
    // 알림 (회원 토큰 정보 조회) - KakaoSendService
    String findAccessTokenByMbrCd(String mbrCd);
    // 알림 (카카오 엑세스 토큰, 리프레쉬 토큰)
    void updateKakaoToken(String mbrCd, String accessToken, String refreshToken, Date tokenExpires);
}