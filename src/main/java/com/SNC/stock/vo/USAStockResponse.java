package com.SNC.stock.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.NumberFormat;
import java.util.Locale;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class USAStockResponse {
    // 원 데이터(값 비교 처리용)                                              CoinRankService 필드상..
    private String stockName;         //주식 이름                        // 코인 이름       💟 이거사용 💟
    private String stckprpr;          //주식 현재가  시가 ($)              // 코인 시가
    private Double prdyvrss;          //전일 대비   변동금액 ($)            // 코인 변동금액    x
    private Double prdyctrt;          //전일 대비율  변동률 (%)             // 코인 변동율      x

    private String coinPriceWon;                                        // 코인 시가(₩)    💟 이거사용 💟
    private String coinChangeWon;                                       // 코인 변동금액   💟 이거사용 💟
    private String coinChangeRate;                                      // 코인 변동률     💟 이거사용 💟


    // 데이터 포멧팅할 필드 ( 모델에서 실제로 접근할 필드명 )
    private String stckprprWon;       // 시가 (₩ 환율전환)
    private String prdyvrssWon;       // 변동금액 (₩ 환율전환)
    private String formattedRate;     // 변동률 (%)
    private String formattedStckprprUsd;   // $ 표시된 시가
    private String formattedPrdyvrssUsd;   // $ 표시된 변동금액


    public void setFormattedCoinField() {
        try {
            this.coinPriceWon = wonFormat(stckprpr);
            this.coinChangeWon = wonFormat(prdyvrss);
            this.coinChangeRate = String.format("%.2f%%", prdyctrt*100);
        } catch (Exception e) {
            this.coinPriceWon = "-";
            this.coinChangeWon = "-";
            this.coinChangeRate = "-";
        }
    }

    /**
     * @param exchangeRate 환율값
     * @Description $ -> ₩ 환율로 전환, 변동률 "%" 붙여서 보여줄 포메팅 메서드
     * 환율 받아오는 api 쓰고싶지만 일단은 .. 하드코딩으로 박아넣자
     */
    public void setFormattedFields(double exchangeRate) {
        try {
            double stckprprDouble = Double.parseDouble(stckprpr);
            this.formattedStckprprUsd = String.format("$%.2f", stckprprDouble);
            this.formattedPrdyvrssUsd = String.format("%s$%.2f", prdyvrss < 0 ? "-" : "+", Math.abs(prdyvrss));
            this.stckprprWon = formatWon(stckprprDouble * exchangeRate);
            this.prdyvrssWon = formatWon(prdyvrss * exchangeRate);
            this.formattedRate = String.format("%.2f%%", prdyctrt);
        } catch (Exception e) {
            // 예외 잡히면 '-' 로 값 표시처리
            this.stckprprWon = "-";
            this.prdyvrssWon = "-";
            this.formattedRate = "-";
        }
    }

    /**
     * @param value
     * @return 1234567.89 --> "₩1,234,567"
     * @Description 한국 원화 형식의 문자열로 변환
     */
    public String formatWon(double value) {
        return NumberFormat.getCurrencyInstance(Locale.KOREA).format(value);
    }

    public String wonFormat(String value) {
        try {
            long number = Long.parseLong(value);
            return "₩" + NumberFormat.getNumberInstance(Locale.KOREA).format(number);
        } catch (NumberFormatException e) {
            return "-"; // 변환 실패 시 대체 문자열
        }
    }

    // double type prdyvrss,prdyctrt 의 포멧팅용 오버로딩 메서드 추가정의
    public String wonFormat(double value) {
        try {
            long number = (long) value; // 소수점 이하 절삭
            return "₩" + NumberFormat.getNumberInstance(Locale.KOREA).format(number);
        } catch (Exception e) {
            return "-";
        }
    }

}

