package com.SNC.board.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BoardDto {
    private String brdId; // board id (primary key)
    private String mbrCd; // (멤버 PK)_FK
    private String mbrNickname; // (멤버 PK)_FK 로 받아오는 닉네임
    private String brdType; // 검색 타입
    private String brdTitle; // 제목
    private String brdContent; // 글 내용
    private String brdDt; // 글 생성일 // TO_CHAR로 변환
    private String brdUpdt; // 글 수정일 // TO_CHAR로 변환
    private int cmtCnt;
    private int likeCnt;
}