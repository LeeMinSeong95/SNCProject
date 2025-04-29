package com.SNC.stock.controller;

import com.SNC.stock.service.RequestAccessToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/request")
@RequiredArgsConstructor
public class RequestAccessTokenController {
    private final RequestAccessToken requestAccessToken;

    /**
     * @param
     * @return 
     * @Description 한국투자증권 신규 token 발급
     */
    @RequestMapping("/token")
    public void getToken() {requestAccessToken.getToken();}

}
