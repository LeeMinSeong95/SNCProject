package com.SNC.volumeRank.dto;

import com.SNC.common.util.Description;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@Slf4j
@Description("Scheduler 결과를 저장할 캐시 클래스")
public class RankCache {

    private List<TopStockDTO> top5ByVolume = new ArrayList<>();     // 거래량 리스트
    private List<TopStockDTO> top5ByPrice = new ArrayList<>();     // 현재가 리스트
    private List<TopStockDTO> top5ByIncrease = new ArrayList<>();  // 거래증가율 리스트

    public void updateTop5(List<TopStockDTO> volume, List<TopStockDTO> price, List<TopStockDTO> increase) {
        try {
            log.info("한국주식 캐시 업데이트 시작: 증가율={}, 거래량={}, 현재가={}",
                    increase.size(), volume.size(), price.size());

            this.top5ByVolume = volume;
            this.top5ByPrice = price;
            this.top5ByIncrease = increase;
            log.info("한국주식 캐시 업데이트 완료");
        } catch (Exception e) {
            log.error("❌ 한국 주식 캐시 업데이트 실패: {}", e.getMessage(), e);
        }
    }
}
