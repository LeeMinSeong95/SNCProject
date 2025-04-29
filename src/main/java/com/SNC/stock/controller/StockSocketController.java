package com.SNC.stock.controller;

import com.SNC.stock.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/stock")
@Slf4j
public class StockSocketController {
    private final KoreaStockWebSocketManager krManager;
    private final USAStockWebSocketManager usaManager;

    /**
     * @param
     * @return
     * @Description 미국주식 웹소켓 연결
     */
    @GetMapping("/usawebsocket")
    public void connectUSAWebsocket() {
        try {
            usaManager.connect();

        } catch (Exception e) {
            log.error("예외 발생", e);

        }
    }

    /**
     * @param
     * @return
     * @Description 한국주식 웹소켓 연결
     */
    @GetMapping("/krwebsocket")
    public void connectKoreaWebsocket() {
        try {
            krManager.connect();

        } catch (Exception e) {
            log.error("예외 발생", e);

        }
    }
}
