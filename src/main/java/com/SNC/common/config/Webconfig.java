package com.SNC.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.URI;

@ConfigurationProperties(prefix = "external.uri")
@Data
@Component
public class Webconfig {
    private URI coin;
    private URI stock;
    private String koreaStockRest;
    private String koreaStockCandle;
    private String usaStockRest;
    private String usaStockCandle;
    private String coinTicker;
    private String coinCandle;
    private String krMarket;
    private String gold;
    private String currency;

}

