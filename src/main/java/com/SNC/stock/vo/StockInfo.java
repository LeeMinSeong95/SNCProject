package com.SNC.stock.vo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Data
@Component
@ConfigurationProperties(prefix = "koreastock.api")
public class StockInfo {
    private String key;
    private String secret;
    private String token;
}

