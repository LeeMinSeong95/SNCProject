package com.SNC.alert.scheduler;

import com.SNC.alert.service.AlertCheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlertScheduler {

    private final AlertCheckService alertCheckService;

    @Scheduled(cron = "0 * * * * *") // 매 분마다 실행
    public void checkAlerts() {
        log.info("🔔 알림 조건 확인 스케줄러 실행");
        try {
            alertCheckService.checkAlertType();
            log.info("알림 타입 체크 완료");
        } catch (Exception e) {
            log.error("알림 체크 중 오류 발생: {}", e.getMessage());
        }
    }


}
