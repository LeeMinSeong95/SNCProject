package com.SNC.alert.service;

import com.SNC.alert.mapper.AlertMapper;
import com.SNC.alert.vo.AlertVo;
import com.SNC.bookMark.dto.BookMarkDTO;
import com.SNC.coin.service.CoinApi;
import com.SNC.email.Service.EmailSendService;
import com.SNC.itemdetail.vo.DetailResponse;
import com.SNC.kakao.service.KakaoSendService;
import com.SNC.register.mapper.MemberMapper;
import com.SNC.stock.service.KoreaStockApi;
import com.SNC.stock.service.USAStockApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlertCheckService {

    // mapper
    private final AlertMapper alertMapper;
    private final MemberMapper memberMapper;

    // alert send service
    private final KakaoSendService kakaoSendService;
    private final EmailSendService emailSendService;

    // api
    private final KoreaStockApi koreaStockApi;
    private final USAStockApi usaStockApi;
    private final CoinApi coinApi;

    // 모든 알림을 검사하고 전송하는 메소드
    public void checkAlertType() {
        // 중복 제거를 위해 Set 사용
        Set<String> itemTypes = new HashSet<>(alertMapper.findByItemType());

        // 중복이 제거된 itemTypes에 대해 처리
        for (String itemType : itemTypes) {
            checkOrSend(itemType);  // 각 종목에 대해 처리
        }
    }

    // 알림 유형에 맞는 알림 처리
    public void checkOrSend(String itemType) {
        List<AlertVo> alerts = alertMapper.findAlertsByItemType(itemType);  // 해당 itemType의 알림 목록 조회

        // 1. 알림 처리
        for (AlertVo alert : alerts) {
            // 2. 알림 설정 당시 해당 종목의 시세 조회
            double alertCurrPrice = alert.getAlertCurrPrice();

            // 3. 해당 종목의 현재 시세
            double currentPrice = getCurrentPrice(itemType, alert.getItemId());  // itemType과 itemId를 통해 현재 시세 조회

            // 4. 목표 퍼센트 도달 여부 확인
            if (alert.getAlertPercent() >= 0) { // 상승 알림일 경우
                if (currentPrice >= alertCurrPrice * (1 + alert.getAlertPercent() / 100)) {
                    // 목표치 도달 시 알림 전송
                    sendAlert(alert);
                    alertMapper.deleteAlert(String.valueOf(alert.getAlertId()));  // 알림 전송 후 삭제
                }
            } else { // 하락 알림일 경우
                if (currentPrice <= alertCurrPrice * (1 + alert.getAlertPercent() / 100)) {
                    // 목표치 도달 시 알림 전송
                    sendAlert(alert);
                    alertMapper.deleteAlert(String.valueOf(alert.getAlertId()));  // 알림 전송 후 삭제
                }
            }
        }
    }

    // 시세를 조회하는 메서드 (각각의 itemType에 맞는 API 사용)
    private double getCurrentPrice(String itemType, String itemId) {
        String ticker = null;

        // 해당 itemId에 대한 ticker 조회
        try {
            switch (itemType) {
                case "001":
                    ticker = alertMapper.getTickerByKRStockId(itemId); // 한국 주식
                    break;
                case "002":
                    ticker = alertMapper.getTickerByUSStockId(itemId); // 미국 주식
                    break;
                case "003":
                    ticker = alertMapper.getTickerByCoinId(itemId);  // 코인
                    break;
                default:
                    log.warn("Unknown itemType: {}", itemType);
                    return 0.0;
            }

            // ticker가 null일 경우 에러 로그 출력
            if (ticker == null) {
                log.warn("No ticker found for itemType: {}, itemId: {}", itemType, itemId);
                return 0.0;
            }

            // BookMarkDTO 객체 생성 후 시세 조회
            BookMarkDTO dto = BookMarkDTO.builder()
                    .itemId(itemId)
                    .ticker(ticker)  // 조회한 ticker 값 사용
                    .build();

            List<BookMarkDTO> list = List.of(dto); // 리스트 형태로 만든 후 조회 API에 전달
            List<DetailResponse> responses = new ArrayList<>();

            switch (itemType) {
                case "001":
                    responses = koreaStockApi.getSimpleInfo(list);  // 한국 주식 시세 조회
                    break;
                case "002":
                    responses = usaStockApi.getSimpleInfo(list);  // 미국 주식 시세 조회
                    break;
                case "003":
                    responses = coinApi.getSimpleInfo(list);  // 코인 시세 조회
                    break;
                default:
                    log.warn("Unknown itemType: {}", itemType);
                    return 0.0;
            }

            if (!responses.isEmpty()) {
                String priceStr = responses.get(0).getNowprice();
                String cleanedPrice = priceStr.replaceAll("[^\\d.]", ""); // 숫자와 소수점만 남김
                return Double.parseDouble(cleanedPrice);
            }

        } catch (Exception e) {
            log.error("시세 조회 중 오류 - itemType: {}, itemId: {}, error: {}", itemType, itemId, e.getMessage());
        }

        // 실패시 반환
        return 0.0;
    }

    // 알림 전송
    private void sendAlert(AlertVo alert) {
        String nickname = memberMapper.findNicknameByMbrCd(String.valueOf(alert.getMbrCd()));
        String itemType = alert.getItemType();
        String currType;
        String ticker = null;
        switch(itemType) {
            case "001":
                ticker = alertMapper.getTickerByKRStockId(alert.getItemId());
                currType = "₩";
                break;
            case "002":
                ticker = alertMapper.getTickerByUSStockId(alert.getItemId());
                currType = "$";
                break;
            case "003":
                ticker = alertMapper.getTickerByCoinId(alert.getItemId());
                currType = "$";
                break;
            default:
                currType = "";
                break;
        }

        String message = """
        %s님이 설정하신 [%s] 종목이 목표 시세에 도달했습니다.
        해당 알림은 자동 해제되었으며, 새 알림이 필요하다면 재설정 해주세요.
        👉 http://localhost:8090/main/home
        (금액: %s%.2f, 목표 퍼센트: %.2f%%)
        """.formatted(nickname, ticker, currType, alert.getAlertCurrPrice(), alert.getAlertPercent());

        if ("KAKAO".equals(alert.getAlertType())) {
            kakaoSendService.sendKakaoAlert(String.valueOf(alert.getMbrCd()), message);
        } else if ("EMAIL".equals(alert.getAlertType())) {
            emailSendService.sendEmail(String.valueOf(alert.getMbrCd()), message);
        }
    }

}
