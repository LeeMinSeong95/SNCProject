package com.SNC.itemdetail.service;

import com.SNC.common.config.Webconfig;
import com.SNC.common.domain.DetailInterface;
import com.SNC.bookMark.service.BookMarkService;
import com.SNC.itemdetail.mapper.ItemDetailMapper;
import com.SNC.itemdetail.vo.ChartDto;
import com.SNC.itemdetail.vo.DetailInfoDto;
import com.SNC.itemdetail.vo.DetailResponse;
import com.SNC.stock.vo.StockInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component("kr")
@Slf4j
@RequiredArgsConstructor
public class KrStockFetchService implements DetailInterface {
    private final StockInfo accessInfo;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ItemDetailMapper itemDetailMapper;
    private final Webconfig webconfig;
    private final BookMarkService bookMarkService;
    private boolean checkYN = false;

    /**
     * @param offset
     * @return List<detailInterface>
     * @Description offset만큼 한국주식정보
     */
    @Override
    public List<DetailResponse> fetch(int offset, String mbrId) {
        List<DetailResponse> resultList = new ArrayList<>();
        String tr_id = "FHKST01010100";
        String koreaStockRestUrl = webconfig.getKoreaStockRest();

        ObjectMapper mapper = new ObjectMapper();

        List<DetailInfoDto> detailInfo = itemDetailMapper.findAllKrStock(offset);

        // stockList에 있는 각 주식 코드마다 요청
        for (DetailInfoDto dto : detailInfo) {
            if(mbrId != null) {
                checkYN = bookMarkService.checkBookMark("001",dto.getItemId(),mbrId);
            }

            String totalUrl = koreaStockRestUrl.trim() + "?fid_cond_mrkt_div_code=J&fid_input_iscd=" + dto.getItemMarkNm();

            try {
                //String bookMarkId,String mbrId, String itemId
                ResponseEntity<String> responseEntity = restTemplate.exchange(totalUrl, HttpMethod.GET, buildHttpEntity(tr_id), String.class);

                // 응답에서 output 부분 추출 후 VO에 매핑
                JsonNode outputNode = mapper.readTree(responseEntity.getBody()).get("output");
                DetailResponse stockInfo = new DetailResponse();
                stockInfo.setName(dto.getItemKrNm());
                stockInfo.setNowprice(outputNode.get("stck_prpr").asText());
                stockInfo.setSubratio(outputNode.get("prdy_ctrt").asText());
                stockInfo.setSubprice(outputNode.get("prdy_vrss").asText());
                stockInfo.setItemId(dto.getItemId());
                stockInfo.setCheckBkMrk(checkYN);
                resultList.add(stockInfo);
            } catch (Exception e) {

            }
        }

        return resultList;
    }

    @Override
    public List<DetailResponse> searchByKeyword(String keyword, String mbrId) {
        List<DetailResponse> resultList = new ArrayList<>();
        String tr_id = "FHKST01010100";
        String koreaStockRestUrl = webconfig.getKoreaStockRest();

        ObjectMapper mapper = new ObjectMapper();


        //해당 keyword로 존재하는 주식 데이터 검색
        List<DetailInfoDto> detailInfo = itemDetailMapper.searchByKeyword(keyword);

        // stockList에 있는 각 주식 코드마다 요청
        for (DetailInfoDto dto : detailInfo) {
            if(mbrId != null) {
                checkYN = bookMarkService.checkBookMark("001",dto.getItemId(),mbrId);
            }
            String totalUrl = koreaStockRestUrl.trim() + "?fid_cond_mrkt_div_code=J&fid_input_iscd=" + dto.getItemMarkNm();
            try {
                ResponseEntity<String> responseEntity = restTemplate.exchange(totalUrl, HttpMethod.GET, buildHttpEntity(tr_id), String.class);


                // 응답에서 output 부분 추출 후 VO에 매핑
                JsonNode outputNode = mapper.readTree(responseEntity.getBody()).get("output");
                DetailResponse stockInfo = new DetailResponse();
                stockInfo.setName(dto.getItemKrNm());
                stockInfo.setNowprice(outputNode.get("stck_prpr").asText());
                stockInfo.setSubratio(outputNode.get("prdy_ctrt").asText());
                stockInfo.setSubprice(outputNode.get("prdy_vrss").asText());
                stockInfo.setItemId(dto.getItemId());
                stockInfo.setCheckBkMrk(checkYN);
                resultList.add(stockInfo);
            } catch (Exception e) {


            }
        }
        return resultList;
    }

    private HttpEntity<String> buildHttpEntity(String trId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("authorization", "Bearer " + accessInfo.getToken());
        headers.set("appKey", accessInfo.getKey());
        headers.set("appSecret", accessInfo.getSecret());
        headers.set("tr_id", trId); // 필요 시 파라미터로 바꿔도 가능
        headers.set("custtype", "P");

        return new HttpEntity<>(headers);
    }

    @Override
    public List<ChartDto> getCandlestickData(String stockName, String from, String to, String period) {

        String symbol = itemDetailMapper.findKrStockSymbol(stockName);
        String trId = "FHKST03010100";
        String baseUrl = webconfig.getKoreaStockCandle();

        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("FID_COND_MRKT_DIV_CODE", "J") // 코스피
                .queryParam("FID_INPUT_ISCD", symbol)
                .queryParam("FID_INPUT_DATE_1", from)
                .queryParam("FID_INPUT_DATE_2", to)
                .queryParam("FID_PERIOD_DIV_CODE", period)
                .queryParam("FID_ORG_ADJ_PRC", "0")
                .toUriString();


        try {
            Thread.sleep(500);
        } catch (Exception e) {
            e.getMessage();
        }
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, buildHttpEntity(trId), String.class);

        return parseChartApiResponse(response.getBody());
    }

    private List<ChartDto> parseChartApiResponse(String responseBody) {
        List<ChartDto> resultList = new ArrayList<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseBody);
            JsonNode output = root.path("output2");

            for (JsonNode node : output) {
                String rawDate = node.path("stck_bsop_date").asText(); // 예: 20240322
                String formattedDate = rawDate.length() == 8
                        ? rawDate.substring(0, 4) + "-" + rawDate.substring(4, 6) + "-" + rawDate.substring(6, 8)
                        : rawDate;

                ChartDto data = ChartDto.builder()
                        .time(formattedDate) // 날짜
                        .open(node.path("stck_oprc").asText())    // 시가
                        .high(node.path("stck_hgpr").asText())    // 고가
                        .low(node.path("stck_lwpr").asText())     // 저가
                        .close(node.path("stck_clpr").asText())   // 종가
                        .volume(node.path("acml_vol").asText())
                        .build();
                resultList.add(data);
            }
            resultList.sort(Comparator.comparing(dto -> LocalDate.parse(dto.getTime())));


        } catch (Exception e) {
            log.error("Failed to parse chart API response", e);
        }

        return resultList;
    }

}
