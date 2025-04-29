package com.SNC.alert.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlertVo {
    private long alertId;               // 알림 테이블 시퀀스 PK
    private long mbrCd;                 // 멤버 테이블 시퀀스 PK(FK)
    private String markId;              // 북마크 테이블 ID
    private String itemId;              // 종목 코드
    private String itemType;            // 종목 타입
    private double alertPercent;        // 목표 퍼센트
    private String alertType;           // 알림 타입
    private double alertCurrPrice;      // 알림 당시 시세
    private String alertStatus;         // INSERT 1 OR DELETE 0
    private LocalDateTime alertDt;      // 알림 설정 일자
    private LocalDateTime alertUpDt;    // 알림 수정 일자
}
