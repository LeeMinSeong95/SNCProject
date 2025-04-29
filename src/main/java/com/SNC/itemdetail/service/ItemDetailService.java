package com.SNC.itemdetail.service;

import com.SNC.common.domain.DetailInterface;
import com.SNC.itemdetail.vo.ChartDto;
import com.SNC.itemdetail.vo.DetailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
@Slf4j
@RequiredArgsConstructor
public class ItemDetailService {
    private final Map<String, DetailInterface> fetcherMap;

    public List<DetailResponse> getAllInfo(int offset, String chartType, String mbrId) {
        DetailInterface fetcher = fetcherMap.get(chartType.toLowerCase());
        if (fetcher == null) {
            throw new IllegalArgumentException("지원하지 않는 chartType: " + chartType);
        }
        return fetcher.fetch(offset, mbrId);
    }

    public List<DetailResponse> searchByKeyword(String keyWord, String chartType, String mbrId) {
        DetailInterface fetcher = fetcherMap.get(chartType.toLowerCase());
        if (fetcher == null) {
            throw new IllegalArgumentException("지원하지 않는 chartType: " + chartType);
        }
        return fetcher.searchByKeyword(keyWord, mbrId);
    }

    public List<ChartDto> getCandlestickData(String stockName, String from, String to, String period, String chartType) {
        DetailInterface fetcher = fetcherMap.get(chartType.toLowerCase());
        if (fetcher == null) {
            throw new IllegalArgumentException("지원하지 않는 chartType: " + chartType);
        }
        return fetcher.getCandlestickData(stockName,from,to,period);
    }
}


