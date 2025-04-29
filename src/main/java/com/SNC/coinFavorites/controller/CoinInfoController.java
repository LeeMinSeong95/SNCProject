package com.SNC.coinFavorites.controller;

import com.SNC.coinFavorites.service.CoinIfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Component
@RequiredArgsConstructor
@Slf4j
public class CoinInfoController {

    private final CoinIfoService coinInfoService;

    @GetMapping("/init")
    public String initCoins() {
        coinInfoService.insertAllCoins();
        return "코인정보 다들어갔다!";
    }
}
