package com.SNC.volumeRank.controller;

import com.SNC.alert.dto.AlertDto;
import com.SNC.alert.service.AlertService;
import com.SNC.bookMark.dto.BookMarkDTO;
import com.SNC.bookMark.service.BookMarkService;
import com.SNC.coin.service.CoinApi;
import com.SNC.itemdetail.vo.DetailResponse;
import com.SNC.login.model.CustomUserDetails;
import com.SNC.stock.service.KoreaStockApi;
import com.SNC.stock.service.USAStockApi;
import com.SNC.stock.vo.USAStockResponse;
import com.SNC.volumeRank.dto.CoinCache;
import com.SNC.volumeRank.dto.RankCache;
import com.SNC.volumeRank.dto.TopStockDTO;
import com.SNC.volumeRank.dto.USAStockCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class Top5PageController {

    private final RankCache rankCache;
    private final USAStockCache usaStockCache;
    private final BookMarkService bookMarkService;
    private final CoinApi coinApi;
    private final KoreaStockApi koreaStockApi;
    private final USAStockApi usaStockApi;
    private final CoinCache coinCache;

    private final AlertService alertService;

    @GetMapping("/favorite/top5")
    public String showTop5Page(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

        String mbrId =  userDetails.getUserCd();
        //mbrId를 사용하여 모든 즐겨찾기 가져오고 type별로 매핑
        Map<String, List<BookMarkDTO>> bookmarkMap = bookMarkService.getBookmarkItemIdsByType(mbrId);

        List<DetailResponse> krList = new ArrayList<>();
        List<DetailResponse> usList = new ArrayList<>();
        List<DetailResponse> coinList = new ArrayList<>();

        List<AlertDto> alertList = alertService.findByAlertStatus(mbrId); // 알림 상태 가져오기

        Map<String, AlertDto> alertMap = alertList.stream()
                .collect(Collectors.toMap(AlertDto::getMarkId, Function.identity())); // markId를 활용

        //itemType: 001 = KR, 002 = US, 003 = COIN
        if (bookmarkMap.containsKey("001")) {
            krList = koreaStockApi.getSimpleInfo(bookmarkMap.get("001"));

            for (DetailResponse kr : krList) {
                AlertDto alert = alertMap.get(kr.getMarkId()); // markId로 찾음
                kr.setAlert(alert); // 없으면 null
            }
        }
        if (bookmarkMap.containsKey("002")) {
            usList = usaStockApi.getSimpleInfo(bookmarkMap.get("002"));

            for (DetailResponse us : usList) {
                AlertDto alert = alertMap.get(us.getMarkId()); // markId로 찾음
                us.setAlert(alert); // 없으면 null
            }
        }
        if (bookmarkMap.containsKey("003")) {
            coinList = coinApi.getSimpleInfo(bookmarkMap.get("003"));

            for (DetailResponse coin : coinList) {
                AlertDto alert = alertMap.get(coin.getMarkId()); // markId로 찾음
                coin.setAlert(alert); // 없으면 null
            }
        }

        model.addAttribute("krList", krList);
        model.addAttribute("usList", usList);
        model.addAttribute("coinList", coinList);

        // 한국 주식 스케쥴처리된 데이터값 가져옴
        List<TopStockDTO> topByVolume = rankCache.getTop5ByVolume();
        List<TopStockDTO> topByPrice = rankCache.getTop5ByPrice();
        List<TopStockDTO> topByIncrease = rankCache.getTop5ByIncrease();
        // 포맷팅 필드 적용 (한국주식)
        topByVolume.forEach(stock -> stock.setFormattedFields());
        topByPrice.forEach(stock -> stock.setFormattedFields());
        topByIncrease.forEach(stock -> stock.setFormattedFields());

        // 미국 주식 스케쥴처리된 데이터값 값 가져옴 --> 미국주식은 service쪽에서 미리 데이터값 포메팅처리해둠.
        List<USAStockResponse> top5ByIncrease = usaStockCache.getTop5ByIncrease();
        List<USAStockResponse> top5ByVolume = usaStockCache.getTop5ByVolume();
        List<USAStockResponse> top5ByPrice = usaStockCache.getTop5ByPrice();

        // 코인 스케쥴처리된 데이터값 가져옴 --> 얘도 미리 데이터값 포메팅 완료
        List<USAStockResponse> topCoinIncrease = coinCache.getTop5ByIncrease();
        List<USAStockResponse> topCoinVol = coinCache.getTop5ByVolume();
        List<USAStockResponse> topCoinPrice = coinCache.getTop5ByPrice();


        // 한국주식 화면 Model
        model.addAttribute("topByVolume", topByVolume);
        model.addAttribute("topByPrice", topByPrice);
        model.addAttribute("topByIncrease", topByIncrease);

        // 미국주식 화면 Model
        model.addAttribute("top5ByVolume", top5ByVolume);
        model.addAttribute("top5ByPrice", top5ByPrice);
        model.addAttribute("top5ByIncrease", top5ByIncrease);

        // 코인 화면 Model
        model.addAttribute("topCoinIncrease", topCoinIncrease);
        model.addAttribute("topCoinVol", topCoinVol);
        model.addAttribute("topCoinPrice", topCoinPrice);


//        log.info("거래량 top5 : {} ",topByVolume);
//        log.info("DTO 확인 : {}" , top5ByPrice.get(0));
        return "favorites/favoriteMainPage";
    }
}
