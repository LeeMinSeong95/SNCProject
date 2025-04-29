package com.SNC.stock.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class USAStockData {
    private String name;
    private String price;
    private String change;
    private String percentChange;
    private String volume;
}

