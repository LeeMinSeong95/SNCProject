package com.SNC.alert.mapper;

import com.SNC.alert.dto.AlertDto;
import com.SNC.alert.vo.AlertVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AlertMapper {

    // 알림 설정
    void insertAlert(String mbrCd,
                     String markId,
                     String itemId,
                     String itemType,
                     String alertPercent,
                     String alertType,
                     String alertCurrPrice);

    // 알림 해제
    void deleteAlert(String alertId);

    // 알림 수정
    void updateAlert(String alertId,
                     String alertPercent,
                     String alertType,
                     String alertCurrPrice);

    // 알림 타입 (국장, 미장, 코인) 조회
    List<String> findByItemType();

    // 알림 타입 별 조회
    List<AlertVo> findAlertsByItemType(@Param("itemType") String itemType);

    // 특정 회원 알림 상태
    List<AlertDto> findByAlertStatus(String mbrId);



    // 한국 주식 심볼 조회
    String getTickerByKRStockId(String itemId);

    // 미국 주식 심볼 조회
    String getTickerByUSStockId(String itemId);

    // 코인 심볼 조회
    String getTickerByCoinId(String itemId);
}
