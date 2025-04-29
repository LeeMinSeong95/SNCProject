package com.SNC.coin.service;

import com.SNC.common.config.Webconfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CoinSocketManager {

    private final CoinWebsocket webSocketClient;

    /**
     * @param
     * @return
     * @Description 코인 웹소켓 연결
     */
    public synchronized void connect() {
        try {
            if (webSocketClient != null && webSocketClient.isOpen()) {
                log.info("이미 연결되어 있음.");
                return;

            }
            log.info("코인 웹소켓 최초 연결 시도");

            webSocketClient.connect();

        } catch (Exception e) {
            log.error("코인 웹소켓 연결 실패", e);

        }
    }
}