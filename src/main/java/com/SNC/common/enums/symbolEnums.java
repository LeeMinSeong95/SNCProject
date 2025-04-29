package com.SNC.common.enums;

import lombok.Data;
import lombok.Getter;

@Getter
public enum symbolEnums {
    BTC_KRW("비트코인"),
    ETH_KRW("이더리움"),
    XRP_KRW("리플"),
    BCH_KRW("비트코인캐시"),
    LTC_KRW("라이트코인"),
    EOS_KRW("이오스"),
    ETC_KRW("이더리움클래식"),
    XLM_KRW("스텔라루멘"),
    TRX_KRW("트론"),
    ADA_KRW("에이다");

    private final String koreanName;

    symbolEnums(String koreanName) {
        this.koreanName = koreanName;
    }

    public static String getKoreanNameBySymbol(String symbol) {
        for (symbolEnums coin : values()) {
            if (coin.name().equalsIgnoreCase(symbol)) {
                return coin.getKoreanName();
            }
        }
        return "알 수 없음";
    }
}
