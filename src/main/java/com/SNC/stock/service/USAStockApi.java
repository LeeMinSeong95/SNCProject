package com.SNC.stock.service;

import com.SNC.bookMark.dto.BookMarkDTO;
import com.SNC.common.config.Webconfig;
import com.SNC.common.enums.USAStockEnums;
import com.SNC.common.util.HeaderUtil;
import com.SNC.itemdetail.vo.DetailResponse;
import com.SNC.stock.vo.USAStockResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class USAStockApi {

    @Autowired
    private Webconfig webconfig;
    private final String usaStockRestUrl;
    private final RestTemplate restTemplate;
    private final HeaderUtil headerUtil;
    private final List<String> stockList = Arrays.asList(
            "AAPL", "MSFT", "AMZN", "GOOGL", "META", "TSLA", "NVDA", "NFLX", "ADBE", "INTC"
    );

    public USAStockApi(Webconfig webconfig, HeaderUtil headerUtil) {
        this.restTemplate = new RestTemplate();
        this.usaStockRestUrl = webconfig.getUsaStockRest();
        this.headerUtil = headerUtil;
    }

    public List<USAStockResponse> httpGetConnectionForStocks() {
        List<USAStockResponse> resultList = new ArrayList<>();
        String tr_id = "HHDFS00000300";

        // 헤더 설정
        HttpHeaders headers = headerUtil.createDefaultHeaders(tr_id);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ObjectMapper mapper = new ObjectMapper();
        // stockList에 있는 각 주식 코드마다 요청
        for (String stockCode : stockList) {
            String totalUrl = usaStockRestUrl.trim()
                    + "?AUTH= &EXCD=NAS&SYMB=" + stockCode;
            try {
                ResponseEntity<String> responseEntity = restTemplate.exchange(totalUrl, HttpMethod.GET, requestEntity, String.class);


                // 응답에서 output 부분 추출 후 VO에 매핑
                JsonNode outputNode = mapper.readTree(responseEntity.getBody()).get("output");
                USAStockResponse stockInfo = new USAStockResponse();
                stockInfo.setStockName(USAStockEnums.getKoreanNameBySymbol(stockCode));
                stockInfo.setStckprpr(outputNode.get("base").asText());
                stockInfo.setPrdyctrt(Double.parseDouble(outputNode.get("rate").asText()));

                if (stockInfo.getPrdyctrt() < 0) {
                    stockInfo.setPrdyvrss(-1 * Double.parseDouble(outputNode.get("diff").asText()));
                } else {
                    stockInfo.setPrdyvrss(Double.parseDouble(outputNode.get("diff").asText()));
                }

                resultList.add(stockInfo);
            } catch (Exception e) {
                // 개별 주식 호출에서 오류가 발생해도 나머지는 계속 처리
                System.err.println("주식 코드 " + stockCode + " 처리 중 오류 발생: " + e.getMessage());
            }
        }
        System.out.println(resultList);
        return resultList;
    }

    /**
     * @param itemTickerList
     * @return List<DetailResponse>
     * @Description 특정 미국주식 정보
     */
    public List<DetailResponse> getSimpleInfo(List<BookMarkDTO> itemTickerList) {
        List<DetailResponse> resultList = new ArrayList<>();
        String tr_id = "HHDFS00000300";

        // 헤더 설정
        HttpHeaders headers = headerUtil.createDefaultHeaders(tr_id);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ObjectMapper mapper = new ObjectMapper();

        for(BookMarkDTO item : itemTickerList){
            String totalUrl = usaStockRestUrl.trim() + "?AUTH= &EXCD=NAS&SYMB=" + item.getTicker().trim().substring(3);

            try {
                ResponseEntity<String> responseEntity = restTemplate.exchange(totalUrl, HttpMethod.GET, requestEntity, String.class);

                // 응답에서 output 부분 추출 후 VO에 매핑
                JsonNode outputNode = mapper.readTree(responseEntity.getBody()).get("output");
                String subratio = outputNode.get("rate").asText();
                String subprice = outputNode.get("diff").asText();

                DetailResponse dto = DetailResponse.builder()
                        .name(item.getName())
                        .nowprice(outputNode.get("base").asText())
                        .subratio(subratio)
                        .subprice(subratio.startsWith("-") ? "-"+subprice : subprice)
                        .itemId(item.getItemId())
                        .markId(item.getMarkId())
                        .itemType("002")
                        .build();


                resultList.add(dto);

            } catch (Exception e) {
                // 개별 주식 호출에서 오류가 발생해도 나머지는 계속 처리
                System.err.println("주식 코드 " + item.getTicker() + " 처리 중 오류 발생: " + e.getMessage());
            }
        }

        return resultList;
    }

}


