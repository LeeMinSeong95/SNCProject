package com.SNC.main.controller;

import com.SNC.coin.service.CoinApi;
import com.SNC.itemdetail.vo.DetailResponse;
import com.SNC.login.model.CustomUserDetails;
import com.SNC.main.service.MarketDataManager;
import com.SNC.stock.service.KoreaStockApi;
import com.SNC.stock.service.USAStockApi;
import com.SNC.stock.vo.KRStockResponse;
import com.SNC.stock.vo.USAStockResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/main")
@Slf4j
public class MainController {

    private final KoreaStockApi koreaStockApi;
    private final USAStockApi usaStockApi;
    private final CoinApi coinApi;
    private final MarketDataManager marketDataManager;

    /**
     * @param
     * @return String
     * @Description 메인페이지:국장,미장,코인,지수 api 호출 후 model에 삽입
     */
    @GetMapping("/home")
    public String home(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if(userDetails != null) {
            model.addAttribute("nickname", userDetails.getNickname());

        }
        List<KRStockResponse> CoinData = coinApi.getInfo();
        List<KRStockResponse> KstockData = koreaStockApi.httpGetConnectionForStocks();
        List<USAStockResponse> USAstockData = usaStockApi.httpGetConnectionForStocks();
        List<DetailResponse> MarketData = marketDataManager.getAllMarketData();

        model.addAttribute("CoinList", CoinData);
        model.addAttribute("stockList", KstockData);
        model.addAttribute("USAstockList", USAstockData);
        model.addAttribute("MarketData", MarketData);

        return "/home/home";
    }

}
