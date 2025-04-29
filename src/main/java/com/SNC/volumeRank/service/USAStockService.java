package com.SNC.volumeRank.service;

import com.SNC.stock.service.USAStockApi;
import com.SNC.stock.vo.USAStockResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class USAStockService {
    private double exchangeRate =1370.0;    // 환율 하드코딩
    private final USAStockApi usaStockApi;

    // 생성자 초기화
    public USAStockService(USAStockApi usaStockApi) {
        this.usaStockApi = usaStockApi;
    }

    /**
     * @Description 전일 대비율(prdyctrt) 기준 Top5
     * @return list
     */
    public List<USAStockResponse> getUsaTop5ByIncrease(){
        List<USAStockResponse> collect = usaStockApi.httpGetConnectionForStocks().stream()
                .sorted(Comparator.comparingDouble(
                        USAStockResponse::getPrdyctrt
                ).reversed())
                .limit(5)
                .collect(Collectors.toList());
        collect.forEach(item-> item.setFormattedFields(exchangeRate));
//        log.info("로그 확인한다 : {}", collect);
        return collect;
    }

    /**
     * @Description 전일 대비 기준 Top5
     * @return list
     */
    public List<USAStockResponse> getUsaTop5ByVolume(){
        List<USAStockResponse> collect = usaStockApi.httpGetConnectionForStocks().stream()
                .sorted(Comparator.comparingDouble(
                        USAStockResponse::getPrdyvrss
                ).reversed())
                .limit(5)
                .collect(Collectors.toList());
        collect.forEach(item-> item.setFormattedFields(exchangeRate));
        return collect;
    }

    /**
     * @Description 현재가 기준 Top5
     * @return
     */
    public List<USAStockResponse> getUsaTop5ByPrice(){
        List<USAStockResponse> collect = usaStockApi.httpGetConnectionForStocks().stream()
                .sorted(Comparator.comparingDouble(
                        (USAStockResponse item) -> Double.parseDouble(item.getStckprpr())
                ).reversed())
                .limit(5)
                .collect(Collectors.toList());
        collect.forEach(item-> item.setFormattedFields(exchangeRate));
        return collect;
    }

}
