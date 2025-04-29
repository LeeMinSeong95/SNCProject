package com.SNC.volumeRank.dto;

import com.SNC.common.util.Description;
import com.SNC.stock.vo.USAStockResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Data
@Slf4j
@Description("코인 Scheduler 결과를 저장할 캐스 클래스 ")
public class CoinCache {
    private List<USAStockResponse> top5ByIncrease = new ArrayList<>();
    private List<USAStockResponse> top5ByVolume = new ArrayList<>();
    private List<USAStockResponse> top5ByPrice = new ArrayList<>();

    public void updateCoinTop5(List<USAStockResponse> price, List<USAStockResponse> volume, List<USAStockResponse> increase) {
        try {
            log.info("🇺🇸 캐시 업데이트 시작: 증가율={}, 거래량={}, 현재가={}",
                    increase.size(), volume.size(), price.size());

            this.top5ByIncrease = increase;
            this.top5ByVolume = volume;
            this.top5ByPrice = price;

            log.info("코인 캐시 업데이트 완료");
        } catch (Exception e) {
            log.error("❌ 코인 캐시 업데이트 실패: {}", e.getMessage(), e);
        }
    }
}
