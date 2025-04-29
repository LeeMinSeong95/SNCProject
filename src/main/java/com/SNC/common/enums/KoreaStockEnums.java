package com.SNC.common.enums;

import lombok.Getter;

@Getter
public enum KoreaStockEnums {
    SAMSUNG("005930", "삼성전자"),
    SK_HYUNIX("000660", "SK하이닉스"),
    LG_CHEM("051910", "LG화학"),
    SAMSUNG_BIO("207940", "삼성바이오로직스"),
    HYUNDAI("005380", "현대자동차"),
    NAVER("035420", "네이버"),
    KAKAO("035720", "카카오"),
    POSCO("005490", "포스코"),
    KB_FINANCIAL("105560", "KB금융그룹"),
    CELLTRION("068270", "셀트리온");

    private final String code;
    private final String koreanName;

    KoreaStockEnums(String code, String koreanName) {
        this.code = code;
        this.koreanName = koreanName;
    }

    public static String getKoreanNameByCode(String code) {
        for (KoreaStockEnums symbol : values()) {
            if (symbol.getCode().equals(code)) {
                return symbol.getKoreanName();
            }
        }
        return "알 수 없음";
    }
}
