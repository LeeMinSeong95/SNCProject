package com.SNC.volumeRank.service;

import com.SNC.volumeRank.dto.ResponseOutputDTO;
import com.SNC.volumeRank.dto.TopStockDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RankService {
    @Value("${koreastock.api.token}")
    private String accessToken;

    @Value("${koreastock.api.key}")
    private String appkey;

    @Value("${koreastock.api.secret}")
    private String appSecret;

    private final WebClient webClient;              // 외부 HTTP API 요청을 비동기식으로 보낼 수 있는 도구(요청을 보내고 결과가 도착하는 동안에 다른 처리가능토록..)
    private final ObjectMapper objectMapper;        // API 응답이 JSON으로 오기 때문에, 파싱해서 자바 객체로 바꿔 사용하기위한 라이브러리


    /**
     * @param webClientBuilder 외부 주입
     * @param objectMapper     외부 주입
     * @Description 모든 요청에서 baseUrl이 자동으로 붙음
     */
    @Autowired
    public RankService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.baseUrl("https://openapi.koreainvestment.com:9443").build();      // 기본 주소값 설정
        this.objectMapper = objectMapper;                                                                    // JSON 처리 설정
    }

    /**
     * @return
     * @Description 요청 url Header부(필수값) 생성 메서드
     */
    private HttpHeaders createVolumeRankHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        headers.set("appKey", appkey);
        headers.set("appSecret", appSecret);
        headers.set("tr_id", "FHPST01710000");                  // 거래 ID
        headers.set("custtype", "P");                          // 고객 유형 : P 개인 / J 법인

        return headers;
    }

    /**
     * @param response API 응답으로 받은 JSON 문자열
     * @return Mono<List < ResponseOutputDTO>>  비동기로 값을 감싸서 반환
     * @Description api 응답값(=JSON) 문자열 파싱해서 ResponseOutputDTO(자바객체)리스트로 변환한 뒤 , 해당 결과를 Mono<List<...>>형태로 감싸 반환
     */
    private Mono<List<ResponseOutputDTO>> parseVolumeRank(String response) {
        try {
            List<ResponseOutputDTO> responseDataList = new ArrayList<>();

            // JSON 문자열 트리구조로 파싱
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode outputNode = rootNode.get("output");   // output : 의 배열들을 가져옴(outputNode에 담아줌).
            if (outputNode != null) {
                for (JsonNode node : outputNode) {
                    ResponseOutputDTO responseData = new ResponseOutputDTO();
                    responseData.setHtsKorIsnm(node.get("hts_kor_isnm").asText());          // HTS 한글 종목명 ex)삼성전자
                    responseData.setMkscShrnIscd(node.get("mksc_shrn_iscd").asText());      // 유가증권 단축 종목코드
                    responseData.setDataRank(node.get("data_rank").asText());               // 데이터 순위
                    responseData.setStckPrpr(node.get("stck_prpr").asText());               // 주식 현재가
                    responseData.setPrdyVrssSign(node.get("prdy_vrss_sign").asText());      // 전일 대비 부호
                    responseData.setPrdyVrss(node.get("prdy_vrss").asText());               // 전일 대비
                    responseData.setPrdyCtrt(node.get("prdy_ctrt").asText());               // 전일 대비율
                    responseData.setAcmlVol(node.get("acml_vol").asText());                 // 누적 거래량
                    responseData.setPrdyVol(node.get("prdy_vol").asText());                 // 전일 거래량
                    responseData.setLstnStcn(node.get("lstn_stcn").asText());               // 상장 수주
                    responseData.setAvrgVol(node.get("avrg_vol").asText());                 // 평균 거래량
                    responseData.setNBefrClprVrssPrprRate(node.get("n_befr_clpr_vrss_prpr_rate").asText());     // N일전종가대비현재가대비율
                    responseData.setVolInrt(node.get("vol_inrt").asText());                 // 거래량 증가율
                    responseData.setVolTnrt(node.get("vol_tnrt").asText());                 // 거래량 회전율
                    responseData.setNdayVolTnrt(node.get("nday_vol_tnrt").asText());        // N일 거래량 회전율
                    responseData.setAvrgTrPbmn(node.get("avrg_tr_pbmn").asText());          // 평균 거래 대액
                    responseData.setTrPbmnTnrt(node.get("tr_pbmn_tnrt").asText());          // 거래대금회전률
                    responseData.setNdayTrPbmnTnrt(node.get("nday_tr_pbmn_tnrt").asText()); // N일 거래대금 회전율
                    responseData.setAcmlTrPbmn(node.get("acml_tr_pbmn").asText());          // 누적 거래 대금

                    responseDataList.add(responseData);
                }
            }
            return Mono.just(responseDataList);
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    /**
     * @return Mono<List < ResponseOutputDTO>>
     * @Description 한국투자증권의 거래량 상위 종목 조회 API를 호출, 응답(JSON)을 받아 파싱해서 Mono로 리턴
     */
    public Mono<List<ResponseOutputDTO>> getVolumeRank() {
        HttpHeaders headers = createVolumeRankHttpHeaders();

        return webClient.get()          //GET 요청 보냄
                .uri(uriBuilder -> uriBuilder.path("/uapi/domestic-stock/v1/quotations/volume-rank")           //URL + 쿼리 연결
                        .queryParam("FID_COND_MRKT_DIV_CODE", "J")
                        .queryParam("FID_COND_SCR_DIV_CODE", "20171")
                        .queryParam("FID_INPUT_ISCD", "0002")
                        .queryParam("FID_DIV_CLS_CODE", "0")
                        .queryParam("FID_BLNG_CLS_CODE", "0")
                        .queryParam("FID_TRGT_CLS_CODE", "111111111")
                        .queryParam("FID_TRGT_EXLS_CLS_CODE", "000000")
                        .queryParam("FID_INPUT_PRICE_1", "0")
                        .queryParam("FID_INPUT_PRICE_2", "0")
                        .queryParam("FID_VOL_CNT", "0")
                        .queryParam("FID_INPUT_DATE_1", "0")
                        .build())
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .retrieve()                                                // 요청 전송,서버 응답 받을 준비
                .onStatus(status -> status.is5xxServerError(),
                        response -> {
                            log.error("🔥 5xx 에러 발생: {}", response.statusCode());
                            return Mono.error(new RuntimeException("🔥 API 서버 내부 오류 발생"));
                        }
                )
                .bodyToMono(String.class)                                  // 응답데이터를 String type으로 ex)  {"output":[{"hts_kor_isnm":"삼성전자",...}]}
                .flatMap(response -> parseVolumeRank(response))     // 받아온 JSON 문자열 -> parseVolumeRank() 에 넘겨서 파싱처리
                .onErrorResume(ex -> {
                    log.warn("⚠️ API 호출 실패. 빈 리스트 반환", ex);
                    return Mono.just(Collections.emptyList());             // 실패 시 빈 리스트 반환
                });

    }


    /**
     * @param fullList
     * @return List<TopStockDTO>
     * @Description 전일 거래량(prdyVol) 기준 Top5
     */
    public List<TopStockDTO> getTop5ByPreviousVol(List<ResponseOutputDTO> fullList) {
        return fullList.stream()
                .sorted(Comparator.comparingLong(
                        (ResponseOutputDTO item) -> Long.parseLong(item.getPrdyVol())
                ).reversed())
                // Comparator.comparingLong() 에서 각 fullList의 항목을 비교하여 오름차순 정렬기준
                // 분류 처리 : prdyVol(문자열) Long 타입이어야 숫자정렬 reversed() 가능 (즉 내림차순이어야 거래량 많은 순으로 나열되니까..)
                .peek(item -> System.out.println("거래량 확인: " + item.getHtsKorIsnm() + " / " + item.getPrdyVol()))    // 로그확인용
                .limit(5)
                .map(this::convertToTopStockDTO)
                // .map(item -> this.convertToTopStockDTO(item)) 이랑 동일코드
                // Stream 안의 요소를 하나씩 꺼내서 convertToTopStockDTO()메서드 안에 넣고 변환된 TopStockDTO 리스트의 결과로 바꿈
                // ..스트림 속 각각의 데이터(type : ResponseOutputDTO)를 TopStockDTO로 바꾸는 메서드를 실행
                .collect(Collectors.toList());
    }


    /**
     * @param fullList
     * @return List<TopStockDTO>
     * @Description 주식 현재가(stckPrpr) 기준 Top5
     */
    public List<TopStockDTO> getTop5ByCurrentPrice(List<ResponseOutputDTO> fullList) {
        return fullList.stream()
                .sorted(Comparator.comparingLong((ResponseOutputDTO item) -> Long.parseLong(item.getStckPrpr())).reversed())
                .limit(5)
                .map(this::convertToTopStockDTO)
                .collect(Collectors.toList());
    }


    /**
     * @param fullList
     * @return List<TopStockDTO>
     * @Description 거래량 증가율(volInrt) 기준 Top5
     */
    public List<TopStockDTO> getTop5ByVolumeIncrease(List<ResponseOutputDTO> fullList) {
        return fullList.stream()
                .sorted(Comparator.comparingDouble(
                        (ResponseOutputDTO item) -> Double.parseDouble(item.getVolInrt())
                ).reversed())
                .limit(5)
                .map(this::convertToTopStockDTO)
                .peek(dto -> System.out.println("✅ 최종 정렬 확인: " + dto.getHtsKorIsnm() + " / " + dto.getVolInrt()))
                .collect(Collectors.toList());
    }


    /**
     * @param item
     * @return TopStockDTO
     * @Description 분류해온 데이터 TopStockDTO 객체로 변환하여 반환하여 사용하기 위한 메서드
     */
    private TopStockDTO convertToTopStockDTO(ResponseOutputDTO item) {
//  로그용      System.out.println("🔁 변환 전: " + item.getHtsKorIsnm() + " / volInrt: " + item.getVolInrt());

        TopStockDTO result = new TopStockDTO(
                item.getHtsKorIsnm(),               // 종목명
                item.getMkscShrnIscd(),             // 종목코드
                item.getPrdyVol(),                  // 전일 거래량
                item.getStckPrpr(),                 // 현재가
                formatPercnt(item.getVolInrt())     // 거래량 증가율 +4.50% 형식 ( 원 데이터 : 104.50 )
        );
        result.setFormattedFields();
//  로그용      System.out.println("✅ 포멧된 현재가 : " + result.getFormattedPrdyVol() + " / 포멧된 거래량 : " + result.getFormattedStckPrpr());
        return result;
    }


    /**
     * @param volInrt
     * @return +4.50% String 값 반환
     * @Description convertToTopStockDTO getInrt() 의 포멧 변환목적
     */
    private String formatPercnt(String volInrt) {
        double value = Double.parseDouble(volInrt);
        double diff = value - 100;

        // %+ : 양수/음수 모두 기호를 무조건 붙이고    .2 : 소수점 둘째자리까지     %% : 실제 %문자를 출력
        return String.format("%+.2f%%", diff);
    }


}



















