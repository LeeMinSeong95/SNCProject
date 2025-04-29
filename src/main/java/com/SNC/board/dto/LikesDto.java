package com.SNC.board.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LikesDto {
    private String likeId; // 좋아요 PK
    private String likeYn;
    private String likeDt; // 좋아요 날짜
    private String brdId; // (게시판 PK)_FK
    private String mbrCd; // (멤버 PK)_FK
    private String mbrNickname;
}
