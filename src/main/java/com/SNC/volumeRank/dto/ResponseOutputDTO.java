package com.SNC.volumeRank.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseOutputDTO {

    @JsonProperty("hts_kor_isnm")
    private String htsKorIsnm; // HTS 한글 종목명

    @JsonProperty("mksc_shrn_iscd")
    private String mkscShrnIscd; // 유가증권 단축 종목코드

    @JsonProperty("data_rank")
    private String dataRank; // 데이터 순위

    @JsonProperty("stck_prpr")
    private String stckPrpr; // 주식 현재가

    @JsonProperty("prdy_vrss_sign")
    private String prdyVrssSign; // 전일 대비 부호

    @JsonProperty("prdy_vrss")
    private String prdyVrss; // 전일 대비

    @JsonProperty("prdy_ctrt")
    private String prdyCtrt; // 전일 대비율

    @JsonProperty("acml_vol")
    private String acmlVol; // 누적 거래량

    @JsonProperty("prdy_vol")
    private String prdyVol; // 전일 거래량

    @JsonProperty("lstn_stcn")
    private String lstnStcn; // 상장 주식 수

    @JsonProperty("avrg_vol")
    private String avrgVol; // 평균 거래량

    @JsonProperty("n_befr_clpr_vrss_prpr_rate")
    private String nBefrClprVrssPrprRate; // N일전 종가 대비 현재가 대비율

    @JsonProperty("vol_inrt")
    private String volInrt; // 거래량 증가율

    @JsonProperty("vol_tnrt")
    private String volTnrt; // 거래량 회전율

    @JsonProperty("nday_vol_tnrt")
    private String ndayVolTnrt; // N일 거래량 회전율

    @JsonProperty("avrg_tr_pbmn")
    private String avrgTrPbmn; // 평균 거래대금

    @JsonProperty("tr_pbmn_tnrt")
    private String trPbmnTnrt; // 거래대금 회전률

    @JsonProperty("nday_tr_pbmn_tnrt")
    private String ndayTrPbmnTnrt; // N일 거래대금 회전율

    @JsonProperty("acml_tr_pbmn")
    private String acmlTrPbmn; // 누적 거래대금
}
