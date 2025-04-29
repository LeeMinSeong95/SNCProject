package com.SNC.stock.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KRStockResponse {
    private String stockName;         //주식 이름
    private int stckprpr;             //주식 현재가
    private int prdyvrss;             //전일 대비
    private Double prdyctrt;          //전일 대비율
    private String formattedStckprpr; //포맷팅된 현재가
    private String formattedPrdyvrss; //포맷팅된 대비

    public void formatPrices() {
        this.formattedStckprpr = String.format("%,d원", this.stckprpr);
        this.formattedPrdyvrss = String.format("%,d원", this.prdyvrss);
    }
}

