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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component("us")
@Slf4j
@RequiredArgsConstructor
public class UsStockFetchService implements DetailInterface {
    private final StockInfo accessInfo;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ItemDetailMapper itemDetailMapper;
    private final Webconfig webconfig;
    private final BookMarkService bookMarkService;
    private boolean checkYN = false;

    /**
     * @param offset
     * @return List<detailInterface>
     * @Description offset만큼 미국주식정보
     */
    @Override
    public List<DetailResponse> fetch(int offset, String mbrId) {
        List<DetailResponse> resultList = new ArrayList<>();
        String tr_id = "HHDFS00000300";


        ObjectMapper mapper = new ObjectMapper();

        List<DetailInfoDto> detailInfo = itemDetailMapper.findAllUsStock(offset);

        // stockList에 있는 각 주식 코드마다 요청
        for (DetailInfoDto dto : detailInfo) {
            if(mbrId != null) {
                checkYN = bookMarkService.checkBookMark("002",dto.getItemId(),mbrId);
            }

            List<String> tmp = Arrays.asList(
                    dto.getItemMarkNm().substring(0, 3),
                    dto.getItemMarkNm().substring(3)
            );
            String totalUrl = webconfig.getUsaStockRest().trim()
                    + "?AUTH= &EXCD=NAS&SYMB=" + tmp.get(1).trim();
            try {
                ResponseEntity<String> responseEntity = restTemplate.exchange(totalUrl, HttpMethod.GET, buildHttpEntity(tr_id), String.class);


                // 응답에서 output 부분 추출 후 VO에 매핑
                JsonNode outputNode = mapper.readTree(responseEntity.getBody()).get("output");
                DetailResponse stockInfo = new DetailResponse();
                stockInfo.setName(dto.getItemKrNm());
                stockInfo.setNowprice(outputNode.get("last").asText());
                stockInfo.setSubratio(outputNode.get("rate").asText());
                stockInfo.setItemId(dto.getItemId());
                stockInfo.setCheckBkMrk(checkYN);
                double tmprate = Double.parseDouble(outputNode.get("rate").asText());
                String diff = outputNode.get("diff").asText();
                stockInfo.setSubprice(tmprate < 0 ? "-" + diff : diff);


                resultList.add(stockInfo);
            } catch (Exception e) {

            }
        }

        return resultList;
    }

    @Override
    public List<DetailResponse> searchByKeyword(String keyword, String mbrId) {
        List<DetailResponse> resultList = new ArrayList<>();
        String tr_id = "HHDFS00000300";
        String koreaStockRestUrl = webconfig.getUsaStockRest();

        ObjectMapper mapper = new ObjectMapper();


        //해당 keyword로 존재하는 주식 데이터 검색
        List<DetailInfoDto> detailInfo = itemDetailMapper.searchByUsKeyword(keyword);

        // stockList에 있는 각 주식 코드마다 요청
        for (DetailInfoDto dto : detailInfo) {
            if(mbrId != null) {
                checkYN = bookMarkService.checkBookMark("002",dto.getItemId(),mbrId);
            }
            List<String> tmp = Arrays.asList(
                    dto.getItemMarkNm().substring(0, 3),
                    dto.getItemMarkNm().substring(3)
            );
            String totalUrl = webconfig.getUsaStockRest().trim()
                    + "?AUTH= &EXCD=NAS&SYMB=" + tmp.get(1).trim();
            try {
                ResponseEntity<String> responseEntity = restTemplate.exchange(totalUrl, HttpMethod.GET, buildHttpEntity(tr_id), String.class);


                // 응답에서 output 부분 추출 후 VO에 매핑
                JsonNode outputNode = mapper.readTree(responseEntity.getBody()).get("output");
                DetailResponse stockInfo = new DetailResponse();
                stockInfo.setName(dto.getItemKrNm());
                stockInfo.setNowprice(outputNode.get("last").asText());
                stockInfo.setSubratio(outputNode.get("rate").asText());
                stockInfo.setItemId(dto.getItemId());
                stockInfo.setCheckBkMrk(checkYN);
                double tmprate = Double.parseDouble(outputNode.get("rate").asText());
                String diff = outputNode.get("diff").asText();
                stockInfo.setSubprice(tmprate < 0 ? "-" + diff : diff);

                resultList.add(stockInfo);
            } catch (Exception e) {


            }
        }
        return resultList;
    }

    @Override
    public List<ChartDto> getCandlestickData(String stockName, String from, String to, String period) {

        String symbol = itemDetailMapper.findUsaStockSymbol(stockName);
        String trId = "HHDFS76240000";
        String baseUrl = webconfig.getUsaStockCandle();
        List<String> tmp = Arrays.asList(
                symbol.substring(0, 3),
                symbol.substring(3)
        );

        String totalUrl = baseUrl + "?AUTH= &EXCD=NAS&SYMB=" + tmp.get(1).trim() + "&GUBN=0" + "&BYMD="+to+ "&MODP=0";
        ResponseEntity<String> response = restTemplate.exchange(totalUrl, HttpMethod.GET, buildHttpEntity(trId), String.class);



        try {
            Thread.sleep(500);
        } catch (Exception e) {
            e.getMessage();
        }
        return parseChartApiResponse(response.getBody(),from);
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

    private List<ChartDto> parseChartApiResponse(String responseBody,String from) {
        List<ChartDto> resultList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate fromdate = LocalDate.parse(from, formatter);
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseBody);
            JsonNode output = root.path("output2");

            for (JsonNode node : output) {
                String rawDate = node.path("xymd").asText(); // 예: 20240322
                LocalDate nodedate = LocalDate.parse(rawDate, formatter);
                if (nodedate.isBefore(fromdate)) { break; }

                String formattedDate = rawDate.length() == 8
                        ? rawDate.substring(0, 4) + "-" + rawDate.substring(4, 6) + "-" + rawDate.substring(6, 8)
                        : rawDate;

                ChartDto data = ChartDto.builder()
                        .time(formattedDate) // 날짜
                        .open(node.path("open").asText())    // 시가
                        .high(node.path("high").asText())    // 고가
                        .low(node.path("low").asText())     // 저가
                        .close(node.path("clos").asText())   // 종가
                        .volume(node.path("tvol").asText())
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
