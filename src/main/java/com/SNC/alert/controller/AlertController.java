package com.SNC.alert.controller;

import com.SNC.alert.dto.AlertDto;
import com.SNC.alert.service.AlertService;
import com.SNC.login.model.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/alert")
public class AlertController {

    private final AlertService alertService;

    // 알림 설정
    @PostMapping("/insert")
    public ResponseEntity<String> insertAlert(@AuthenticationPrincipal CustomUserDetails userDetails,
                                              @RequestBody AlertDto alertDto) {
        try {
            String mbrCd = userDetails.getUserCd();  // 현재 로그인한 사용자 (알람을 설정하는 특정 멤버)

            // insert 실행
            alertService.insertAlert(mbrCd, alertDto);

            log.info("alert on success :  {}, {}", mbrCd, alertDto);

            return ResponseEntity.ok("alert on success");
        } catch (Exception e) {
            log.error("alert on error : {}", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("alert on fail");
        }

    }

    // 알림 해제
    @PostMapping("/delete")
    public ResponseEntity<String> deleteAlert(@RequestBody AlertDto alertDto) {
        try {
            // delete 실행
            alertService.deleteAlert(alertDto);

            log.info("alert off success : {}", alertDto);

            return ResponseEntity.ok("alert off success");
        } catch (Exception e) {
            log.error("alert off error : {}", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("alert off fail");
        }
    }

    // 알림 수정
    @PostMapping("/update")
    public ResponseEntity<String> updateAlert(@RequestBody AlertDto alertDto) {
        try {
            // update 실행
            alertService.updateAlert(alertDto);

            log.info("alert update success :  {}", alertDto);

            return ResponseEntity.ok("alert update success");
        } catch (Exception e) {
            log.error("alert update error : {}", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("alert update fail");
        }
    }

}
