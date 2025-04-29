package com.SNC.stock.service;

import com.SNC.common.config.Webconfig;
import com.SNC.common.enums.KoreaStockEnums;
import com.SNC.stock.vo.KRStockResponse;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;


@Service
@Slf4j
public class KoreaStockWebSocket extends WebSocketClient {

    @Setter
    private String approvalKey;
    private final SimpMessagingTemplate messagingTemplate;
    private final List<String> stockList = Arrays.asList(
            "005930", "000660", "051910", "207940", "005380",
            "035420", "035720", "005490", "105560", "068270"
    );

    public KoreaStockWebSocket(Webconfig webconfig, SimpMessagingTemplate messagingTemplate) {
        super(webconfig.getStock());
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * @param
     * @return
     * @Description 웹소켓 연결시 최초 실행
     */
    @Override
    public void onOpen(ServerHandshake handshakedata) {
        log.info("웹소켓 연결 성공!");
        for(String stock : stockList) {
            setRequest(stock);
        }

    }
    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.info("웹소켓 연결 종료:{} ", reason);
    }

    @Override
    public void onError(Exception ex) {
        log.info("웹소켓 오류 발생:{} ", ex.getMessage());
    }

    /**
     * @param
     * @return
     * @Description 응답이 왔을때 실행
     */
    @Override
    public void onMessage(String message) {
        try{
            String[] parts = message.split("\\|");
            if (parts.length < 4) {
                throw new RuntimeException("응답 데이터 오류");
            }
            String dataPart = parts[3];
            String[] fields = dataPart.split("\\^");

            KRStockResponse stockInfo = KRStockResponse.builder()
                    .stockName(KoreaStockEnums.getKoreanNameByCode(fields[0]))
                    .stckprpr(Integer.parseInt(fields[2]))
                    .prdyvrss(Integer.parseInt(fields[4]))
                    .prdyctrt(Double.parseDouble(fields[5]))
                    .build();

            stockInfo.formatPrices();

            messagingTemplate.convertAndSend("/topic/KRData", stockInfo);
        } catch(Exception e) {

        }
    }

    public void setRequest(String stock) {
        try {
            JSONObject request = new JSONObject();
            request.put("header", new JSONObject()
                    .put("approval_key", approvalKey) // approvalKey 추가
                    .put("tr_type", "1")
                    .put("custtype", "P")
                    .put("content-type", "utf-8"));

            request.put("body", new JSONObject()
                    .put("input", new JSONObject()
                            .put("tr_id", "H0STCNT0")
                            .put("tr_key", stock)));

            this.send(request.toString());

        } catch (JSONException e) {
            log.info("JSON 생성 오류:{}", e.getMessage());
        }
    }
}



