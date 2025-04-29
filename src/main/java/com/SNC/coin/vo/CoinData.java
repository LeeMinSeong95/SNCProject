package com.SNC.coin.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CoinData {
    private String coinCode;
    private String coinName;
    private String chgAmt;
    private String chgRate;
    private String volume;
    private String closePrice;
    private int rawchgAmt;
    private int rawclosePrice;

    public void formatPrices() {
        this.chgAmt = String.format("%,d원", this.rawchgAmt);
        this.closePrice = String.format("%,d원", this.rawclosePrice);
    }
}

