package com.SNC.volumeRank.scheduler;

import com.SNC.common.util.Description;
import com.SNC.stock.vo.USAStockResponse;
import com.SNC.volumeRank.dto.CoinCache;
import com.SNC.volumeRank.service.CoinRankService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Description("스케쥴러를 1분마다 실행하여 Rank 정보를 갱신하여 사용자의 화면에 보여준다.")
public class CoinRankScheduler {

    @PostConstruct
    public void init() {
        try {
            updateTop5Ranks();
        } catch (Exception e) {
            log.error("초기화 중 에러 발생",e);
        }
    }

    private final CoinRankService coinRankService;
    private final CoinCache coinCache;

    @Scheduled( cron = "0 */3 * * * *" )
    public void updateTop5Ranks() {
        log.info("Top5 코인 랭킹 데이터 업데이트 실행");

        List<USAStockResponse> coinTop5ByPrice = coinRankService.getCoinTop5ByPrice();
        List<USAStockResponse> coinTop5ByVolume = coinRankService.getCoinTop5ByVolume();
        List<USAStockResponse> coinTop5ByIncrease = coinRankService.getCoinTop5ByIncrease();

        if (coinTop5ByPrice.isEmpty() || coinTop5ByVolume.isEmpty() || coinTop5ByIncrease.isEmpty()) {
            log.warn("Top5 데이터가 비어 있어 캐시 저장 생략");
            return;
        }
        coinCache.updateCoinTop5(coinTop5ByPrice,coinTop5ByVolume,coinTop5ByIncrease);
        log.info("코인 Top5 갱신 완료");
    }
}
