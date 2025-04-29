package com.SNC.main.service;

import com.SNC.common.config.Webconfig;
import com.SNC.common.domain.MarketDataHandler;
import com.SNC.common.util.HeaderUtil;
import com.SNC.itemdetail.vo.DetailResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class KrMarketApiService implements MarketDataHandler {

    @Autowired
    private Webconfig webconfig;
    @Autowired
    private HeaderUtil headerUtil;

    /**
     * @param
     * @return List<DetailResponse>
     * @Description 지수를 확인하기위한 service
     */
    @Override
    public List<DetailResponse> getData() {
        List<DetailResponse> resultList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        List<String> piNdak = List.of("0001", "1001");

        WebClient webClient = WebClient.builder()
                .baseUrl(webconfig.getKrMarket())
                .defaultHeaders(httpHeaders -> httpHeaders.addAll(headerUtil.createDefaultHeaders("FHPUP02100000")))
                .build();
        for(String item : piNdak) {
            try {
                Mono<String> responseMono = webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .queryParam("FID_COND_MRKT_DIV_CODE", "U")
                                .queryParam("FID_INPUT_ISCD", item)
                                .build())
                        .retrieve()
                        .bodyToMono(String.class);

                String responseBody = responseMono.block(); // 동기화 처리

                if(responseBody != null) {
                    JsonNode output = mapper.readTree(responseBody).path("output");
                    String price = output.path("bstp_nmix_prdy_ctrt").asText()+"%";
                    String subprice = output.path("prdy_vrss_sign").asText().equals("2") ? "1" : "0";
                    DetailResponse detail = DetailResponse.builder()
                                            .name(item.equals("0001") ? "코스피" : "코스닥")
                                            .nowprice(price)
                                            .subprice(subprice)
                                            .build();
                    resultList.add(detail);
                }

            } catch (Exception e) {

            }
        }



        return resultList;
    }
}
