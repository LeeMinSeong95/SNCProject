package com.SNC.coinFavorites.mapper;

import com.SNC.coinFavorites.vo.CoinInfoVo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CoinMapper {
    void insertCoinInfo(CoinInfoVo coinVO);
}
