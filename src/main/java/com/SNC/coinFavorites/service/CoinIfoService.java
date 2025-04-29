package com.SNC.coinFavorites.service;

import com.SNC.chartNews.util.chart.CoinName;
import com.SNC.coinFavorites.mapper.CoinMapper;
import com.SNC.coinFavorites.vo.CoinInfoVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CoinIfoService {

    private final CoinMapper coinMapper;

    /**
     * Enum에 정의된 코인 목록을 DB에 Insert
     */
    public void insertAllCoins() {
        CoinName[] coinNames = CoinName.values();

        for (int i = 0; i < coinNames.length; i++) {
            CoinInfoVo coinVO = new CoinInfoVo();

            // coinId = "coin00001", "coin00002" ... 형식으로
            // i+1을 이용해 5자리로 포맷
            String coinId = "coin" + String.format("%05d", (i + 1));

            coinVO.setCoinId(coinId);
            coinVO.setCoinMarkNm(coinNames[i].getMarket());
            coinVO.setCoinKrNm(coinNames[i].getKoreanName());
            coinVO.setCoinEngNm(coinNames[i].getEnglishName());
            coinVO.setCoinMarkYn("N"); // 기본값

            // DB Insert
                coinMapper.insertCoinInfo(coinVO);
        }
    }
}
