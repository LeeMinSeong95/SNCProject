package com.SNC.common.domain;

import com.SNC.itemdetail.vo.DetailResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface MarketDataHandler {
    List<DetailResponse> getData();

}
