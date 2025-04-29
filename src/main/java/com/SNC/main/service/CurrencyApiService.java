package com.SNC.main.service;

import com.SNC.common.config.Webconfig;
import com.SNC.common.domain.MarketDataHandler;
import com.SNC.itemdetail.vo.DetailResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CurrencyApiService implements MarketDataHandler {

    @Autowired
    private Webconfig webconfig;
    @Value("${currency.api}")
    private String currencyApi;

    /**
     * @param
     * @return List<DetailResponse>
     * @Description 환율조회를 위한 api
     */
    @Override
    public List<DetailResponse> getData() {
        List<DetailResponse> resultList = new ArrayList<>();

        WebClient webClient = WebClient.builder()
                .baseUrl(webconfig.getCurrency())
                .defaultHeader("Content-Type", "application/json")
                .build();

        try {
            String responseBody = webClient.get()
                                .uri(uriBuilder -> uriBuilder
                                        .path("/latest")
                                        .queryParam("apikey", currencyApi)
                                        .queryParam("base_currency", "USD")
                                        .build())
                                .retrieve()
                                .bodyToMono(String.class)
                                .block();

            if(responseBody != null) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(responseBody);
                double price = Double.parseDouble(root.path("data").path("KRW").asText());
                String currency = String.format("%.2f원", price);

                DetailResponse detail = DetailResponse.builder()
                                        .name("한/미 환율")
                                        .nowprice(currency)
                                        .subprice("2")
                                        .build();

                resultList.add(detail);
            }

        } catch (Exception e) {
            log.error("Currency 에러 : {}", e.getMessage());
        }

        return resultList;
    }
}
