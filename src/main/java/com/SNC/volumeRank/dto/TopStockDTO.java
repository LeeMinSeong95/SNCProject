package com.SNC.volumeRank.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.text.NumberFormat;
import java.util.Locale;

@Data
public class TopStockDTO {

    @JsonProperty("hts_kor_isnm")
    private String htsKorIsnm;          // 종목이름 KR

    @JsonProperty("mksc_shrn_iscd")
    private String mkscShrnIscd;        // 종목코드 (구분자로 사용되는듯)


    //전일 거래량을 기준 prdyVol & 주식현재가 기준 stckPrpr & 거래량 증가율 기준 volInrt

    @JsonProperty("prdy_vol")
    private String prdyVol;             // 전일 거래량

    @JsonProperty("stck_prpr")
    private String stckPrpr;            // 주식 현재가

    @JsonProperty("vol_inrt")
    private String volInrt;             // 거래량 증가율


    private String formattedPrdyVol;    // 거래량 000,000으로 포멧화위한 필드
    private String formattedStckPrpr;   // 주식 현재가 000,000으로 포멧화위한 필드

    // 생성자 초기화
    public TopStockDTO(String htsKorIsnm, String mkscShrnIscd, String prdyVol, String stckPrpr, String volInrt) {
        this.htsKorIsnm = htsKorIsnm;
        this.mkscShrnIscd = mkscShrnIscd;
        this.prdyVol = prdyVol;
        this.stckPrpr = stckPrpr;
        this.volInrt = volInrt;
    }

    public void setFormattedFields(){
        this.formattedPrdyVol = formatNumber(prdyVol);
        this.formattedStckPrpr = formatNumber(stckPrpr);
    }


    /**
     * @Description String 값 NumberFormat으로 맞춰주기위해 사용
     * @param value
     * @return
     */
    private String formatNumber(String value) {
        try {
            return NumberFormat.getNumberInstance(Locale.KOREA).format(Long.parseLong(value));
        } catch (Exception e) {
            return value;
        }
    }

}
