package com.SNC.coin.service;

import com.SNC.bookMark.dto.BookMarkDTO;
import com.SNC.chartNews.util.chart.CoinName;
import com.SNC.common.config.Webconfig;
import com.SNC.itemdetail.vo.DetailResponse;
import com.SNC.stock.vo.KRStockResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CoinApi{
    private final RestTemplate restTemplate = new RestTemplate();
    private final Webconfig webconfig;
    private final List<String> coinList = Arrays.asList(
            "KRW-BTC", "KRW-ETH", "KRW-XRP", "KRW-BCH", "KRW-TRX",
            "KRW-ADA", "KRW-EOS", "KRW-DOGE", "KRW-LTC", "KRW-XLM"
    );

    /**
     * @param
     * @return List<DetailResponse>
     * @Description 코인정보
     */
    public List<KRStockResponse> getInfo() {

        List<KRStockResponse> resultList = new ArrayList<>();

        ObjectMapper mapper = new ObjectMapper();

        for (String coin : coinList) {
            try {
                String url = webconfig.getCoinTicker() + "?markets=" + coin;
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);

                JsonNode root = mapper.readTree(response.getBody());
                if (root.isArray() && root.size() > 0) {
                    JsonNode info = root.get(0);

                    KRStockResponse dto = KRStockResponse.builder()
                            .stockName(findCoinKrName(coin))
                            .stckprpr(Integer.parseInt(info.get("trade_price").asText()))
                            .prdyvrss(Integer.parseInt(info.get("signed_change_price").asText()))
                            .prdyctrt(Double.parseDouble(info.get("signed_change_rate").asText()))
                            .build();

                    dto.formatPrices();
                    resultList.add(dto);
                }

            } catch (Exception e) {
                //System.err.println(infoDto.getItemMarkNm() + " 호출 실패");
                e.printStackTrace();
            }
        }
        return resultList;
    }

    /**
     * @param itemTickerList
     * @return DetailResponse
     * @Description 특정 코인 정보
     */
    public List<DetailResponse> getSimpleInfo(List<BookMarkDTO> itemTickerList) {

        List<DetailResponse> resultList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        for(BookMarkDTO item : itemTickerList) {
            try {
                String url = webconfig.getCoinTicker() + "?markets=" + item.getTicker().trim();
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);

                JsonNode root = mapper.readTree(response.getBody());
                if (root.isArray() && root.size() > 0) {
                    JsonNode info = root.get(0);
                    DetailResponse dto = DetailResponse.builder()
                            .name(item.getName())
                            .nowprice(info.get("trade_price").asText())
                            .subprice(info.get("signed_change_price").asText())
                            .subratio(info.get("signed_change_rate").asText())
                            .itemId(item.getItemId())
                            .markId(item.getMarkId())
                            .itemType("003")
                            .build();

                    resultList.add(dto);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return resultList;

    }

    /**
     * @param keyword
     * @return String
     * @Description 키워드를 사용한 코인 한글이름 검색
     */
    public static String findCoinKrName(String keyword) {
        for(CoinName coin : CoinName.values()) {
            if(coin.getMarket().equals(keyword)) {
                return coin.getKoreanName();
            }
        }

        throw new IllegalArgumentException("입력한 키워드에 해당하는 코인을 찾을 수 없습니다. 입력한 코인 : " + keyword);
    }

    /**
     * @param symbol
     * @return String
     * @Description
     */
    public static String getFormattedSymbol(String symbol) {
        String formattedSymbol = symbol.split("_")[0];
        return findCoinKrName("KRW-"+formattedSymbol);
    }

}
