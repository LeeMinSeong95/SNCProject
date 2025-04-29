package com.SNC.main.service;

import com.SNC.common.config.Webconfig;
import com.SNC.common.domain.MarketDataHandler;
import com.SNC.itemdetail.vo.DetailResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoldApiService {

    private final Webconfig webconfig;
    @Value("${gold.api}")
    private String goldApi;

    /**
     * @param
     * @return List<DetailResponse>
     * @Description 금, 은 시세조회를 위한 api
     */
    public List<DetailResponse> getData() {
        List<DetailResponse> resultList = new ArrayList<>();
        List<String> goldNsilver = List.of("/XAU/KRW", "/XAG/KRW");

        WebClient webClient = WebClient.builder()
                .baseUrl(webconfig.getGold())
                .defaultHeader("x-access-token", goldApi)
                .defaultHeader("Content-Type", "application/json")
                .build();

        for (String item : goldNsilver) {
            try {
                String responseBody = webClient.get()
                        .uri(item)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block(); // 동기화 처리

                if (responseBody != null) {
                    // JSON 파싱
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode root = mapper.readTree(responseBody);

                    String name = root.path("metal").asText(); // 금인지 은인지
                    double price = Double.parseDouble(root.path("price").asText()); // 현재가
                    double chgprice = Double.parseDouble(root.path("ch").asText());
                    String updown = root.path("ch").asText().startsWith("-") ? "0" : "1"; //상승,하락

                    // 객체 생성
                    DetailResponse detail = DetailResponse.builder()
                                            .name(name.equals("XAU") ? "골드" : "실버")
                                            .nowprice(calculateChangeRate(price,chgprice))
                                            .subprice(updown)
                                            .build();

                    resultList.add(detail);
                }

            } catch (Exception e) {
                log.error("goldNsilver 에러 : {}", e.getMessage());

            }
        }
        return resultList;

    }

    /**
     * @param nowPrice, changeAmount
     * @return String
     * @Description 변동률 계산
     */
    public String calculateChangeRate(double nowPrice, double changeAmount) {
        if (nowPrice - changeAmount == 0) {
            return "0";
        }
        double yesterdayPrice = nowPrice - changeAmount;
        return String.format("%.2f%%", (double) changeAmount / yesterdayPrice * 100);
    }
}
