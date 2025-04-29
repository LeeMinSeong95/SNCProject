package com.SNC.coinFavorites.vo;

import lombok.Data;

@Data
public class CoinInfoVo {
    private String coinId;          // 고유 코인 번호
    private String coinMarkNm;      // 고유 코인 검색이름
    private String coinKrNm;        // 코인 한글명
    private String coinEngNm;       // 코인 영문명
    private String coinMarkYn;      // 기본값 N으로 넣을것임.
}
