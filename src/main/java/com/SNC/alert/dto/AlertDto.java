package com.SNC.alert.dto;

import lombok.Data;

@Data
public class AlertDto {
    private String alertId;        // 알람 PK 시퀀스
    private String mbrCd;          // 알람 설정한 특정 멤버
    private String markId;         // 북마크 ID (FK X)
    private String itemId;         // 종목 코드 (주식 or 코인)
    private String itemType;       // "KR : 001", "US : 002", "COIN : 003"
    private String alertPercent;   // 목표 퍼센트 (-30% ~ 30%)
    private String alertType;      // "KAKAO" OR "EMAIL"
    private String alertCurrPrice; // 알림 당시 시세
    private String alertStatus;    // INSERT 1 OR DELETE 0
    private String alertDt;        // 알림 설정 일자
    private String alertUpDt;      // 알림 수정 일자
}
