package com.SNC.coinFavorites.service;

import com.SNC.coinFavorites.mapper.CoinBookmarkMapper;
import com.SNC.coinFavorites.vo.CoinInfoVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class BookmarkService {

    private final CoinBookmarkMapper coinBookmarkMapper;

    /**
     * @ memberId 받아와 해당 멤버가 가진 코인정보 리스트로 반환
     * @param memberId int
     */
    public List<CoinInfoVo> getBookmarkedCoinsByMember(int memberId) {
        return coinBookmarkMapper.selectBookmarkedCoinsByMember(memberId);
    }
}
