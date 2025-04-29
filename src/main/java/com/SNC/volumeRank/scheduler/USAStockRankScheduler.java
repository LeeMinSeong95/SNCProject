package com.SNC.volumeRank.scheduler;

import com.SNC.common.util.Description;
import com.SNC.stock.vo.USAStockResponse;
import com.SNC.volumeRank.dto.USAStockCache;
import com.SNC.volumeRank.service.USAStockService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Description("스케쥴러를 하루마다(기준시 : 매일 새벽 6시) 실행하여 Rank 정보를 갱신하여 사용자의 화면에 보여준다.")
public class USAStockRankScheduler {

    @PostConstruct
    public void init() {
        try {
            updateUSAStockTop5();
        } catch (Exception e) {
            log.error("🇺🇸 초기화 중 예외 발생", e);
        }
    }

    private final USAStockService usaStockService;
    private final USAStockCache usaStockCache;

    @Scheduled(cron = "0 0 6 * * *")
    public void updateUSAStockTop5() {
        log.info("🇺🇸 미국 주식 Top5 갱신 시작");

        try {
            List<USAStockResponse> top5Increase = usaStockService.getUsaTop5ByIncrease();
            List<USAStockResponse> top5Volume = usaStockService.getUsaTop5ByVolume();
            List<USAStockResponse> top5Price = usaStockService.getUsaTop5ByPrice();

            log.info("🇺🇸 데이터 수집 완료 → 증가율={}, 거래량={}, 현재가={}",
                    top5Increase.size(), top5Volume.size(), top5Price.size());

            if (top5Increase.isEmpty() || top5Price.isEmpty()) {
                log.warn("🇺🇸 Top5 데이터가 비어 있어 캐시 저장 생략");
                return;
            }
            usaStockCache.updateUSAStockTop5(top5Increase, top5Volume, top5Price);
            log.info("🇺🇸 미국 주식 Top5 갱신 완료");
        } catch (Exception e) {
            log.error("❌ 미국 주식 Top5 갱신 중 전체 실패 발생: {}", e.getMessage(), e);
        }
    }


}
