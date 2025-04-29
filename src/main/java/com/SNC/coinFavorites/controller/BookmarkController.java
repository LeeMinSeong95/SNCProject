package com.SNC.coinFavorites.controller;

import com.SNC.coinFavorites.service.BookmarkService;
import com.SNC.coinFavorites.vo.CoinInfoVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Component
@RestController
@RequiredArgsConstructor
@Slf4j
public class BookmarkController {

    private final BookmarkService bookmarkService;


    /**
     * @Description http://localhost:8090/bookmarks?memberId=1
     * @param memberId
     */
    @GetMapping("/bookmarks")
    public List<CoinInfoVo> getBookmarks(@RequestParam int memberId) {
        return bookmarkService.getBookmarkedCoinsByMember(memberId);
    }

}
