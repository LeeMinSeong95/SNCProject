package com.SNC.coinFavorites.mapper;

import com.SNC.coinFavorites.vo.CoinInfoVo;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface CoinBookmarkMapper {
    List<CoinInfoVo> selectBookmarkedCoinsByMember(int memberId);
}
