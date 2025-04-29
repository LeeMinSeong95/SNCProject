package com.SNC.board.dto;

import lombok.Data;

@Data
public class CommentsDto {
    private String cmtId; // 코멘트 아이디 pk
    private String mbrCd; // (멤버 PK)_FK
    private String mbrNickname; // 멤버 닉네임
    private String brdId; // (게시판 PK)_FK
    private String brdType;
    private String cmtContent; // 코멘트 내용
    private String cmtDt; // 댓글 작성 날짜 To char로 변환
    private String cmtUpDt; // 수정날짜
}
