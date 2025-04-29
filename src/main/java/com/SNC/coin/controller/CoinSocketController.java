package com.SNC.coin.controller;

import com.SNC.coin.service.CoinSocketManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequiredArgsConstructor
@RestController
@RequestMapping("/coin")
@Slf4j
public class CoinSocketController {

    private final CoinSocketManager coinSocketManager; // 우베소켓 중복 연결 방지를 위한 매니저

    /**
     * @param
     * @return ResponseEntity<String>
     * @Description 코인 웹소켓 연결
     */
    @GetMapping("/start")
    public ResponseEntity<String> start() {
        try {
            coinSocketManager.connect();
            return ResponseEntity.ok("WebSocket 연결 시작됨");
        } catch (Exception e) {
            log.error("예외 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("WebSocket 연결 실패");
        }
    }
}
