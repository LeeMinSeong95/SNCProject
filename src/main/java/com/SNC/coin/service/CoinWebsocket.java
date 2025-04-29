package com.SNC.coin.service;

import com.SNC.coin.vo.CoinData;
import com.SNC.common.config.Webconfig;
import com.SNC.stock.vo.KRStockResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@Scope("prototype")
public class CoinWebsocket extends WebSocketClient {

    private final SimpMessagingTemplate messagingTemplate;

    
    public CoinWebsocket(Webconfig webconfig, SimpMessagingTemplate messagingTemplate) {
        super(webconfig.getCoin());
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * @param
     * @return
     * @Description 웹소켓 연결시 최초 실행
     */
    @Override
    public void onOpen(ServerHandshake handshakedata) {
        List<String> coinSymbols = Arrays.asList("BTC_KRW", "ETH_KRW", "XRP_KRW", "BCH_KRW", "TRX_KRW", "ADA_KRW", "EOS_KRW", "DOGE_KRW", "LTC_KRW", "XLM_KRW");

        String symbolsJsonArray = coinSymbols.stream()
                .map(symbol -> "\"" + symbol + "\"")
                .collect(Collectors.joining(","));

        // 구독 메시지 생성
        String subscribeMessage = "{\"type\":\"ticker\",\"symbols\":[" + symbolsJsonArray + "],\"tickTypes\":[\"24H\"]}";

        send(subscribeMessage);
    }

    /**
     * @param
     * @return
     * @Description 응답이 왔을때 실행
     */
    @Override
    public void onMessage(String message) {
        try {
            //JSON 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(message);
            JsonNode content = root.get("content");

            if (content != null) {
                //추출
                String symbol = content.get("symbol").asText();
                CoinData coinData = CoinData.builder()
                        .coinCode(symbol)
                        .coinName(CoinApi.getFormattedSymbol(symbol))
                        .rawchgAmt(Integer.parseInt(content.get("chgAmt").asText()))
                        .chgRate(content.get("chgRate").asText())
                        .volume(content.get("volume").asText())
                        .rawclosePrice(Integer.parseInt(content.get("closePrice").asText()))
                        .build();

                coinData.formatPrices();
                messagingTemplate.convertAndSend("/topic/coinData", coinData); //Endpoint에 객체 저장

            }
        } catch (Exception e) {
            log.error("JSON 파싱 중 오류 발생", e);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.info("연결이 종료되었습니다. 이유: {}", reason);
    }

    @Override
    public void onError(Exception ex) {
        log.error("오류 발생:", ex);
    }

}