package com.SNC.main.service;

import com.SNC.common.domain.MarketDataHandler;
import com.SNC.itemdetail.vo.DetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MarketDataManager {

    private final List<MarketDataHandler> services;

    /**
     * @param
     * @return List<DetailResponse>
     * @Description MarketDataHandler을 implement한 모든 서비스의 .getdata호출 및 데이터 취합
     */
    public List<DetailResponse> getAllMarketData() {
        List<DetailResponse> result = new ArrayList<>();
        for (MarketDataHandler service : services) {
            result.addAll(service.getData());
        }
        return result;
    }
}