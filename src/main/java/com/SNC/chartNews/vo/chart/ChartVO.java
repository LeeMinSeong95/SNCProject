package com.SNC.chartNews.vo.chart;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Data
@Value
@RequiredArgsConstructor
public class ChartVO {

    private String market;                  // 마켓명

    @JsonProperty("candle_date_time_kst")
    private String timeKst;                // 캔들 기준 시각(KST 기준 yyyy-MM-dd'T'HH:mm:ss)

    @JsonProperty("opening_price")
    private Double openingPrice;            // 시가 ( 시작가격 )

    @JsonProperty("high_price")
    private Double highPrice;               // 고가

    @JsonProperty("low_price")
    private Double lowPrice;                // 저가

    @JsonProperty("trade_price")
    private Double tradePrice;              // 종가 ( 종료가격 )

    @JsonProperty("timestamp")
    private Long timeStamp;                 // 캔들 종료 시각(KST기준)

    @JsonProperty("candle_acc_trade_price")
    private Double totalTradePrice;         // 누적 거래 금액

    @JsonProperty("candle_acc_trade_volume")
    private Double totalTradeVolume;        // 누적 거래량




}
