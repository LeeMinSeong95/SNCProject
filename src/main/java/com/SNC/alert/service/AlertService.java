package com.SNC.alert.service;

import com.SNC.alert.dto.AlertDto;
import com.SNC.alert.mapper.AlertMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertService {

    private final AlertMapper alertMapper; // 알람 on&off 로직

    // 알람 설정
    @Transactional
    public void insertAlert(String mbrCd, AlertDto alertDto) {
        String markId = alertDto.getMarkId();
        String itemId = alertDto.getItemId();
        String itemType = alertDto.getItemType();
        String alertPercent = alertDto.getAlertPercent();
        String alertType = alertDto.getAlertType();
        String alertCurrPrice = alertDto.getAlertCurrPrice();

        alertMapper.insertAlert(mbrCd, markId, itemId, itemType, alertPercent, alertType, alertCurrPrice);
    }

    // 알림 해제
    @Transactional
    public void deleteAlert(AlertDto alertDto) {
        String alertId = alertDto.getAlertId();
        alertMapper.deleteAlert(alertId);
    }

    // 알림 수정
    @Transactional
    public void updateAlert(AlertDto alertDto) {
        String alertId = alertDto.getAlertId();
        String alertPercent = alertDto.getAlertPercent();
        String alertType = alertDto.getAlertType();
        String alertCurrPrice = alertDto.getAlertCurrPrice();

        alertMapper.updateAlert(alertId, alertPercent, alertType, alertCurrPrice);
    }

    // 특정 회원 알림 상태
    public List<AlertDto> findByAlertStatus(String mbrId){
        return alertMapper.findByAlertStatus(mbrId);
    }
}
