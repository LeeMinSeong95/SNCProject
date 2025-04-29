package com.SNC.volumeRank.controller;

import com.SNC.common.util.Description;
import com.SNC.stock.service.USAStockApi;
import com.SNC.stock.vo.USAStockResponse;
import com.SNC.volumeRank.dto.RankCache;
import com.SNC.volumeRank.dto.ResponseOutputDTO;
import com.SNC.volumeRank.dto.TopStockDTO;
import com.SNC.volumeRank.dto.USAStockCache;
import com.SNC.volumeRank.service.CoinRankService;
import com.SNC.volumeRank.service.RankService;
import com.SNC.volumeRank.service.USAStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@RestController
@Description("데이터가 잘 들어오는지 확인하기위한 REST CONTROLLER")
public class RankController {

    private final RankService rankService;
    private final RankCache rankCache;

    @Autowired
    private CoinRankService coinRankService;

    @Autowired
    private USAStockApi usaStockApi;

    @Autowired
    private USAStockService usaStockService;

    @Autowired
    private USAStockCache usaStockCache;

    @Autowired
    public RankController(RankService rankService, RankCache rankCache) {
        this.rankService = rankService;
        this.rankCache = rankCache;
    }

    @GetMapping("/volumeRank")
    public Mono<List<ResponseOutputDTO>> getVolumeRank() {
        return rankService.getVolumeRank();
    }

    @GetMapping("/top5/volume")     // 거래량 기준
    public Mono<ResponseEntity<List<TopStockDTO>>> showTop5ByPreviousVol() {
        // rankService.getVolumeRank().block() : Mono에서 리스트객체 꺼내줌. But 비동기방식 유지를 위해 .map()을 사용하자..
        return rankService.getVolumeRank()
                .map(fullList -> rankService.getTop5ByPreviousVol(fullList))
                .map(ResponseEntity::ok);
    }

    @GetMapping("/top5/price")          //현재가 기준
    public Mono<ResponseEntity<List<TopStockDTO>>> showTop5ByPrice() {
        return rankService.getVolumeRank()
                .map(fullList -> rankService.getTop5ByCurrentPrice(fullList))
                .map(ResponseEntity::ok);
    }

    @GetMapping("/top5/increase")
    public Mono<ResponseEntity<List<TopStockDTO>>> showTop5ByIncrease() {
        return rankService.getVolumeRank()
                .map(fullList -> rankService.getTop5ByVolumeIncrease(fullList))
                .map(ResponseEntity::ok);
    }

    @GetMapping("/api/top5/{type}")
    public List<TopStockDTO> getTop5(@PathVariable String type) {
        return switch (type) {
            case "volume"   -> rankCache.getTop5ByVolume();
            case "price"    -> rankCache.getTop5ByPrice();
            case "increase" -> rankCache.getTop5ByIncrease();
            default         -> Collections.emptyList();
        };
    }

    // 미장 데이터 확인용
    @GetMapping("/top5/usaStock")
    public List<USAStockResponse> top5USAStock() {
//        return usaStockApi.httpGetConnectionForStocks();
//        return usaStockCache.getTop5ByIncrease();
//        return usaStockService.getUsaTop5ByIncrease();      //스케쥴 돌고 캐시에 저장된 값이 들어오게된다.
        return usaStockService.getUsaTop5ByPrice();
//        return usaStockService.getUsaTop5ByVolume();
    }

    @GetMapping("/list/coinTop5")
    public List<USAStockResponse> getCoinTop5() {
        return coinRankService.getCoinTop5ByIncrease();
    }
}
