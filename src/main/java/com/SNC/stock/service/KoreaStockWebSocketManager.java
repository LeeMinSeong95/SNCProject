package com.SNC.stock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KoreaStockWebSocketManager {

    private final KoreaStockWebSocket webSocketClient;
    private final RequestWebKey requestWebKey;

    public synchronized void connect() {
        try {
            if (webSocketClient != null && webSocketClient.isOpen()) {
                log.info("이미 연결되어 있음.");
                return;
            }

            webSocketClient.setApprovalKey(requestWebKey.getApprovalKey());
            webSocketClient.connect();

        } catch (Exception e) {
            log.error("KR웹소켓 연결 실패", e);
        }
    }
}