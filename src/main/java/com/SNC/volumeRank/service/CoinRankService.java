package com.SNC.volumeRank.service;

import com.SNC.coin.service.CoinApi;
import com.SNC.common.config.Webconfig;
import com.SNC.stock.vo.USAStockResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.SNC.coin.service.CoinApi.findCoinKrName;

@Service
@Slf4j
@RequiredArgsConstructor
public class CoinRankService {

    private final List<String> coinList = Arrays.asList(
            "KRW-BTC", "KRW-ETH", "KRW-USDT", "KRW-XRP", "BTC-BNB",
            "BTC-SOL", "KRW-USDC", "KRW-DOGE", "KRW-TRX", "KRW-ADA"
    );

    private final Webconfig webconfig;
    private final RestTemplate restTemplate = new RestTemplate();

    public List<USAStockResponse> getInfo() {

        List<USAStockResponse> resultList = new ArrayList<>();

        ObjectMapper mapper = new ObjectMapper();

        for (String coin : coinList) {
            try {
                String url = webconfig.getCoinTicker() + "?markets=" + coin;
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);

                JsonNode root = mapper.readTree(response.getBody());
                if (root.isArray() && root.size() > 0) {
                    JsonNode info = root.get(0);

                    USAStockResponse dto = new USAStockResponse();
                    dto.setStockName(findCoinKrName(coin));
                    dto.setStckprpr(info.get("trade_price").asText());
                    dto.setPrdyvrss(Double.parseDouble(info.get("signed_change_price").asText()));
                    dto.setPrdyctrt(Double.parseDouble(info.get("signed_change_rate").asText()));

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
     * @Description 시가 기준 코인 Top5
     * @return list
     */
    public List<USAStockResponse> getCoinTop5ByPrice(){
        List<USAStockResponse> coinList = this.getInfo().stream()
                .sorted(Comparator.comparingDouble(
                        ( USAStockResponse item )-> Double.parseDouble(item.getStckprpr())
                        ).reversed())
                        .limit(5)
                .collect(Collectors.toList());
        coinList.forEach(item->{
            item.setFormattedCoinField();
        });

        return coinList;
    }

    /**
     * @Description 변동금 기준 코인 Top5
     * @return list
     */
    public List<USAStockResponse> getCoinTop5ByVolume(){
        List<USAStockResponse> coinList = this.getInfo().stream()
                .sorted(Comparator.comparingDouble(
                        USAStockResponse::getPrdyvrss
                ).reversed())
                .limit(5)
                .collect(Collectors.toList());
        coinList.forEach(
                item-> item.setFormattedCoinField()
        );

        return coinList;

    }

    /**
     * @Description 변동률 기준 코인 Top5
     * @return list
     */
    public List<USAStockResponse> getCoinTop5ByIncrease(){
        List<USAStockResponse> coinList = this.getInfo().stream()
                .sorted(Comparator.comparingDouble(
                        USAStockResponse::getPrdyctrt
                ).reversed())
                .limit(5)
                .collect(Collectors.toList());
        coinList.forEach(
                item-> item.setFormattedCoinField()
        );

        return coinList;

    }

}
