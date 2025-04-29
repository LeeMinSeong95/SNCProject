package com.SNC.itemdetail.service;

import com.SNC.common.config.Webconfig;
import com.SNC.common.domain.DetailInterface;
import com.SNC.bookMark.service.BookMarkService;
import com.SNC.itemdetail.mapper.ItemDetailMapper;
import com.SNC.itemdetail.vo.ChartDto;
import com.SNC.itemdetail.vo.DetailInfoDto;
import com.SNC.itemdetail.vo.DetailResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component("coin")
@Slf4j
@RequiredArgsConstructor
public class CoinFetchService implements DetailInterface {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ItemDetailMapper itemDetailMapper;
    private final Webconfig webconfig;
    private final BookMarkService bookMarkService;
    private boolean checkYN = false;

    /**
     * @param offset
     * @return List<DetailResponse>
     * @Description offset만큼 코인정보
     */
    @Override
    public List<DetailResponse> fetch(int offset, String mbrId) {
        List<DetailInfoDto> detailInfo = itemDetailMapper.findAllCoins(offset);
        List<DetailResponse> resultList = new ArrayList<>();

        ObjectMapper mapper = new ObjectMapper();

        for (DetailInfoDto infoDto : detailInfo) {
            if(mbrId != null) {
                checkYN = bookMarkService.checkBookMark("003",infoDto.getItemId(),mbrId);
            }
            try {
                String url = webconfig.getCoinTicker() + "?markets=" + infoDto.getItemMarkNm();
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);

                JsonNode root = mapper.readTree(response.getBody());
                if (root.isArray() && root.size() > 0) {
                    JsonNode coin = root.get(0);

                    DetailResponse dto = new DetailResponse();
                    dto.setName(infoDto.getItemKrNm());  // 종목 이름 (한글명)
                    dto.setNowprice(coin.get("trade_price").asText());
                    dto.setSubprice(coin.get("signed_change_price").asText());
                    dto.setSubratio(coin.get("signed_change_rate").asText());
                    dto.setItemId(infoDto.getItemId());
                    dto.setCheckBkMrk(checkYN);
                    resultList.add(dto);
                }

            } catch (Exception e) {
                System.err.println(infoDto.getItemMarkNm() + " 호출 실패");
                e.printStackTrace();
            }
        }
        return resultList;
    }

    @Override
    public List<DetailResponse> searchByKeyword(String keyword, String mbrId) {
        List<DetailInfoDto> detailInfo = itemDetailMapper.searchByCoinKeyword(keyword);
        List<DetailResponse> resultList = new ArrayList<>();

        ObjectMapper mapper = new ObjectMapper();

        for (DetailInfoDto infoDto : detailInfo) {
            if(mbrId != null) {
                checkYN = bookMarkService.checkBookMark("003",infoDto.getItemId(),mbrId);
            }
            try {
                String url = webconfig.getCoinTicker() + "?markets=" + infoDto.getItemMarkNm();
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);

                JsonNode root = mapper.readTree(response.getBody());
                if (root.isArray() && root.size() > 0) {
                    JsonNode coin = root.get(0);

                    DetailResponse dto = new DetailResponse();
                    dto.setName(infoDto.getItemKrNm());  // 종목 이름 (한글명)
                    dto.setNowprice(coin.get("trade_price").asText());
                    dto.setSubprice(coin.get("signed_change_price").asText());
                    dto.setSubratio(coin.get("signed_change_rate").asText());
                    dto.setItemId(infoDto.getItemId());
                    dto.setCheckBkMrk(checkYN);
                    resultList.add(dto);
                }

            } catch (Exception e) {
                System.err.println(infoDto.getItemMarkNm() + " 호출 실패");
                e.printStackTrace();
            }
        }
        return resultList;
    }

    @Override
    public List<ChartDto> getCandlestickData(String stockName, String from, String to, String period) {
        List<ChartDto> resultList = new ArrayList<>();
        String coinsymbol = itemDetailMapper.findCoinSymbol(stockName);
        ObjectMapper mapper = new ObjectMapper();
        if (to != null && to.length() == 8) {
            String yyyy = to.substring(0, 4);
            String mm = to.substring(4, 6);
            String dd = to.substring(6, 8);
            to = yyyy + "-" + mm + "-" + dd + " 23:59:59";
        }
        try {

            String url = webconfig.getCoinCandle() + "?market=" + coinsymbol+"&to=" + to + "&count=90";
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);

            JsonNode root = mapper.readTree(response.getBody());

            for (JsonNode coin : root) {
                String time = coin.get("candle_date_time_kst").asText().substring(0, 10); // yyyy-MM-dd

                ChartDto dto =ChartDto.builder()
                        .time(time)
                        .open(coin.get("opening_price").asText())
                        .high(coin.get("high_price").asText())
                        .low(coin.get("low_price").asText())
                        .close(coin.get("trade_price").asText())
                        .volume(coin.get("candle_acc_trade_volume").asText())
                        .build();

                resultList.add(dto);
            }
            resultList.sort(Comparator.comparing(dto -> LocalDate.parse(dto.getTime())));
            log.info("전체코인데이터:{}",resultList);

        } catch (Exception e) {
            System.err.println("코인 호출 실패");
            e.printStackTrace();
        }

        return resultList;
    }

}
