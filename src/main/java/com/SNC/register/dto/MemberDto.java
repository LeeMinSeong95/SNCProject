package com.SNC.register.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class MemberDto {
    private String mbrCd;
    private String mbrId; //로그인ID
    private String mbrName; // 이름
    private String mbrNickName; // 닉네임
    private String mbrPass; // 비밀번호
    private String mbrEmail; // 이메일
    private String mbrImg; // 프로필 이미지
    private LocalDateTime regDt; // 가입일
    private LocalDateTime updateDt; // 수정일

}
