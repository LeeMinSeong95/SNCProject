package com.SNC.common.enums;

import lombok.Getter;

@Getter
public enum USAStockEnums {
    AAPL("애플"),
    MSFT("마이크로소프트"),
    AMZN("아마존"),
    GOOGL("구글"),
    META("메타"),
    TSLA("테슬라"),
    NVDA("엔비디아"),
    NFLX("넷플릭스"),
    ADBE("어도브"),
    INTC("인텔");

    private final String koreanName;
    USAStockEnums(String koreanName) {
        this.koreanName = koreanName;

    }

    public static String getKoreanNameBySymbol(String symbol) {
        for (USAStockEnums coin : values()) {
            if (coin.name().equalsIgnoreCase(symbol)) {
                return coin.getKoreanName();
            }
        }
        return "알 수 없음";
    }

}
