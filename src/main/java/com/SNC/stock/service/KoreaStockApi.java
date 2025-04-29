package com.SNC.stock.service;

import com.SNC.bookMark.dto.BookMarkDTO;
import com.SNC.common.config.Webconfig;
import com.SNC.common.enums.KoreaStockEnums;
import com.SNC.common.util.HeaderUtil;
import com.SNC.itemdetail.vo.DetailResponse;
import com.SNC.stock.vo.KRStockResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Service
@Slf4j
public class KoreaStockApi {

    @Autowired
    private Webconfig webconfig;
    private final String koreaStockRestUrl;
    private final RestTemplate restTemplate;
    private final HeaderUtil headerUtil;
    private final List<String> stockList = Arrays.asList(
            "005930", "000660", "051910", "207940", "005380",
            "035420", "035720", "005490", "105560", "068270"
    );

    public KoreaStockApi(Webconfig webconfig, HeaderUtil headerUtil) {
        this.restTemplate = new RestTemplate();
        this.koreaStockRestUrl = webconfig.getKoreaStockRest();
        this.headerUtil = headerUtil;
    }

    /**
     * @param
     * @return List<KRStockResponse>
     * @Description api를 통해 stockList의 현재 정보 가져오기 
     */
    public List<KRStockResponse> httpGetConnectionForStocks() {
        List<KRStockResponse> resultList = new ArrayList<>();
        String tr_id = "FHKST01010100";

        // 헤더 설정
        HttpHeaders headers = headerUtil.createDefaultHeaders(tr_id);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ObjectMapper mapper = new ObjectMapper();

        // stockList에 있는 각 주식 코드마다 요청
        for(String stockCode : stockList) {
            String totalUrl = koreaStockRestUrl.trim()
                    + "?fid_cond_mrkt_div_code=J&fid_input_iscd=" + stockCode;
            try {
                ResponseEntity<String> responseEntity = restTemplate.exchange(totalUrl, HttpMethod.GET, requestEntity, String.class);


                // 응답에서 output 부분 추출 후 VO에 매핑
                JsonNode outputNode = mapper.readTree(responseEntity.getBody()).get("output");
                KRStockResponse stockInfo = KRStockResponse.builder()
                        .stockName(KoreaStockEnums.getKoreanNameByCode(stockCode))
                        .stckprpr(Integer.parseInt(outputNode.get("stck_prpr").asText()))
                        .prdyctrt(Double.parseDouble(outputNode.get("prdy_ctrt").asText()))
                        .prdyvrss(Integer.parseInt(outputNode.get("prdy_vrss").asText()))
                        .build();

                stockInfo.formatPrices();
                resultList.add(stockInfo);

            } catch (Exception e) {
                // 개별 주식 호출에서 오류가 발생해도 나머지는 계속 처리
                System.err.println("주식 코드 " + stockCode + " 처리 중 오류 발생: " + e.getMessage());
            }
        }
        return resultList;
    }

    /**
     * @param itemTickerList
     * @return List<DetailResponse>
     * @Description 특정 한국주식 정보
     */
    public List<DetailResponse> getSimpleInfo(List<BookMarkDTO> itemTickerList) {
        List<DetailResponse> resultList = new ArrayList<>();
        String tr_id = "FHKST01010100";

        // 헤더 설정
        HttpHeaders headers = headerUtil.createDefaultHeaders(tr_id);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ObjectMapper mapper = new ObjectMapper();

        for(BookMarkDTO item : itemTickerList){
            String totalUrl = koreaStockRestUrl.trim() + "?fid_cond_mrkt_div_code=J&fid_input_iscd=" + item.getTicker().trim();

            try {
                ResponseEntity<String> responseEntity = restTemplate.exchange(totalUrl, HttpMethod.GET, requestEntity, String.class);

                // 응답에서 output 부분 추출 후 VO에 매핑
                JsonNode outputNode = mapper.readTree(responseEntity.getBody()).get("output");
                DetailResponse dto = DetailResponse.builder()
                        .name(item.getName())
                        .nowprice(outputNode.get("stck_prpr").asText())
                        .subratio(outputNode.get("prdy_ctrt").asText())
                        .subprice(outputNode.get("prdy_vrss").asText())
                        .itemId(item.getItemId())
                        .markId(item.getMarkId())
                        .itemType("001")
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


