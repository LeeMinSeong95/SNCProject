package com.SNC.chartNews.service;

import com.SNC.chartNews.util.chart.CoinName;
import com.SNC.chartNews.vo.chart.ChartVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class ChartService {

    /**
     * @Description 사용자가 입력한 키워드(한글 혹은 영문)을 기준으로 enum객체 찾음. (+ 이더리 까지만 쳐도 나올수있게)
     * @param keyword
     * @return market 값 ( 나중에 차트데이터 받아올때 요청 url값 설정시 해당 메서드 사용 )
     */
    public static String fromKeyword(String keyword){ //이더리
        String lowerKeyword= keyword.toLowerCase();

        for(CoinName coin : CoinName.values()){
            if(coin.getKoreanName().toLowerCase().contains(lowerKeyword) ||
            coin.getEnglishName().toLowerCase().contains(lowerKeyword)){
                return coin.getMarket();
            }
        }
        //  검색 결과에 없을 경우..! 예외 던져버렷
        throw new IllegalArgumentException("입력한 키워드에 해당하는 코인을 찾을 수 없습니다. 입력한 코인 : " + keyword);
    }

    /**
     * @Description 현재 KST 기준 시각을 "yyyy-MM-dd HH:mm:ss" 형식으로 반환
     */
    public static String getCurrentKSTTimeFormatted(){
        ZoneId kstZone = ZoneId.of("Asia/Seoul");
        LocalDateTime nowKST = LocalDateTime.now(kstZone);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return nowKST.format(formatter);
    }

    /**
     * @Description 사용자가 검색한 data에 따라 요청 url생성 메서드 (분 캔들 데이터)
     */
    public static String generateMinuteCandleRequestUrl(String keyword) {
        //enum을 사용해 입력된 키워드에 해당하는 market을 찾음
        String marketCode = fromKeyword(keyword);
        String baseUrl = "https://api.bithumb.com/v1/candles/minutes/1";
        int count = 120000;
        String toTime = getCurrentKSTTimeFormatted();   // 시간값 설정

        // 찐 url요청 보낼거 !!!
        return String.format("%s?market=%s&count=%s&to=%s", baseUrl, marketCode, count, toTime);
    }

    /**
     * @Description 일 캔들 데이터 요청 url생성
     */
    public static String generateDayCandleRequestUrl(String keyword) {
        String marketCode = fromKeyword(keyword);
        String baseUrl = "https://api.bithumb.com/v1/candles/days";
        int count = 372; // 요청일을 기준으로 31일 전까지에 해당하는 가격변동 데이터를 가져오기위함
        String toTime = getCurrentKSTTimeFormatted();

        return String.format("%s?market=%s&count=%s&to=%s", baseUrl, marketCode, count, toTime);
    }

    /**
     * @Description 주 캔들 데이터 요청 url생성
     */
    public static String generateWeekCandleRequestUrl(String keyword) {
        String marketCode = fromKeyword(keyword);
        String baseUrl = "https://api.bithumb.com/v1/candles/weeks";
        int count = 300; // 요청일을 기준으로 30주 전까지에 해당하는 가격변동 데이터를 가져오기위함
        String toTime = getCurrentKSTTimeFormatted();

        return String.format("%s?market=%s&count=%s&to=%s", baseUrl, marketCode, count, toTime);
    }

    /**
     * @Description 월 캔들 데이터 요청 url생성
     */
    public static String generateMonthCandleRequestUrl(String keyword) {
        String marketCode = fromKeyword(keyword);
        String baseUrl = "https://api.bithumb.com/v1/candles/months";
        int count = 288;
        String toTime = getCurrentKSTTimeFormatted();

        return String.format("%s?market=%s&count=%s&to=%s", baseUrl, marketCode, count, toTime);
    }

    /**
     * @ Description 분당 해당 코인의 가격 list로 반환
     * @param keyword
     * @return
     */
    public List<ChartVO> getMinuteCandleData(String keyword) {
        String requestUrl = generateMinuteCandleRequestUrl(keyword);

        RestTemplate restTemplate = new RestTemplate();
        ChartVO[] candleArray = restTemplate.getForObject(requestUrl, ChartVO[].class);

        return Arrays.asList(candleArray);
    }

    /**
     * @ Description 일별 해당 코인의 가격 list로 반환
     * @param keyword
     * @return
     */
    public List<ChartVO> getDayCandleData(String keyword) {
        String requestUrl = generateDayCandleRequestUrl(keyword);

        RestTemplate restTemplate = new RestTemplate();
        ChartVO[] candleArray = restTemplate.getForObject(requestUrl, ChartVO[].class);

        return Arrays.asList(candleArray);
    }

    /**
     * @ Description 주별 해당 코인의 가격 list로 반환
     * @param keyword
     * @return
     */
    public List<ChartVO> getWeekCandleData(String keyword) {
        String requestUrl = generateWeekCandleRequestUrl(keyword);

        RestTemplate restTemplate = new RestTemplate();
        ChartVO[] candleArray = restTemplate.getForObject(requestUrl, ChartVO[].class);

        return Arrays.asList(candleArray);
    }

    /**
     * @ Description 매달 해당 코인의 가격 list로 반환
     * @param keyword
     * @return
     */
    public List<ChartVO> getMonthCandleData(String keyword) {
        String requestUrl = generateMonthCandleRequestUrl(keyword);

        RestTemplate restTemplate = new RestTemplate();
        ChartVO[] candleArray = restTemplate.getForObject(requestUrl, ChartVO[].class);

        return Arrays.asList(candleArray);
    }

}
