package com.SNC.volumeRank.scheduler;

import com.SNC.common.util.Description;
import com.SNC.volumeRank.dto.RankCache;
import com.SNC.volumeRank.dto.TopStockDTO;
import com.SNC.volumeRank.service.RankService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Description("스케쥴러를 1분마다 실행하여 Rank 정보를 갱신하여 사용자의 화면에 보여준다.")
public class KrStockRankScheduler {


    /**
     * @Description 앱 시작 → /favorite/top5 URL 접근하지만 이때의 RankCache는 초기화되어 비어있는 상태(즉 List값이 비어있음)
     * 스케쥴러는 최초 실행까지 1분을 대기하고 실행되어 화면에 아무것도 나오지 않게되어
     * 결과적으로 컨트롤러는 빈 리스트를 모델에 담고 리턴함.
     */
    @PostConstruct
    public void init() {
        updateTop5Ranks(); // 앱 시작 시 한 번 호출
    }


    private final RankService rankService;
    private final RankCache rankCache;

    // 1분마다 Scheduler 실행
    @Scheduled( cron = "0 */1 * * * *")
    public void updateTop5Ranks() {
        log.info("Top5 한국 주식 랭킹 데이터 업데이트 실행");

        rankService.getVolumeRank()
                .map(fullList ->{

                    // 각 기준별 Top5 생성
                    List<TopStockDTO> topByVolume   = rankService.getTop5ByPreviousVol(fullList);
                    List<TopStockDTO> topByPrice    = rankService.getTop5ByCurrentPrice(fullList);
                    List<TopStockDTO> topByIncrease = rankService.getTop5ByVolumeIncrease(fullList);

                    // Cache에 저장
                    rankCache.updateTop5(topByVolume, topByPrice, topByIncrease);

                    log.info("Top5 갱신 완료");
//                    log.info("✔️ 저장될 현재가 (formatted): {}", topByPrice.get(0).getFormattedStckPrpr());
                    return true;
                    // 여기서는 생략 가능! map() 내부에서 코드 처리후엔 반드시 새로운 값을 반환해야하지만 반환할게 없을땐 통상적으로 true; 반환
                })
                .doOnError(e -> {
                    log.error("🔥 API 호출 중 예외 발생", e);
                })
                .onErrorResume(e -> Mono.empty()) // 에러 발생 시 아무 작업 없이 흐름 유지
                .subscribe();   // 비동기 실행 완료
    }
}
