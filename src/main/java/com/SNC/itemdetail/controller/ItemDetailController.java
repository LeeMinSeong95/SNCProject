package com.SNC.itemdetail.controller;

import com.SNC.bookMark.service.BookMarkService;
import com.SNC.itemdetail.service.ItemDetailService;
import com.SNC.itemdetail.vo.ChartDto;
import com.SNC.itemdetail.vo.DetailResponse;
import com.SNC.login.model.CustomUserDetails;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/itemdetail")
public class ItemDetailController {
    private final ItemDetailService itemDetailService;
    private final BookMarkService bookMarkService;

    /**
     * @Description 한국주식상세페이지 초기화면
     * @param
     * @return model
     */
    @GetMapping("/krstock")
    public String krPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

        String mbrId = null;
        if (userDetails != null) {
            mbrId = userDetails.getUserCd();
            model.addAttribute("nickname", userDetails.getNickname());

        }

        List<DetailResponse> detailInfo = itemDetailService.getAllInfo(0,"kr",mbrId);
        model.addAttribute("infoList", detailInfo);
        return "detail/krstock";
    }

    /**
     * @Description 코인상세페이지 초기화면
     * @param
     * @return model
     */
    @GetMapping("/coin")
    public String coinPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

        String mbrId = null;

        if (userDetails != null) {
            mbrId = userDetails.getUserCd();
            model.addAttribute("nickname", userDetails.getNickname());

        }

        List<DetailResponse> detailInfo = itemDetailService.getAllInfo(0,"coin",mbrId);
        model.addAttribute("infoList", detailInfo);
        return "detail/coin";
    }

    /**
     * @Description 미국주식상세페이지 초기화면
     * @param
     * @return model
     */
    @GetMapping("/usstock")
    public String usPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

        String mbrId = null;

        if (userDetails != null) {
            mbrId = userDetails.getUserCd();
            model.addAttribute("nickname", userDetails.getNickname());

        }

        List<DetailResponse> detailInfo = itemDetailService.getAllInfo(0,"us", mbrId);
        model.addAttribute("infoList", detailInfo);
        return "detail/usstock";
    }

    /**
     * @Description 테이블 무한 스크롤
     * @param offset, chartType
     * @return List<DetailResponse>
     */
    @GetMapping("/detail")
    @ResponseBody
    public List<DetailResponse> itemDetail(@AuthenticationPrincipal CustomUserDetails userDetails,
                                           @RequestParam int offset,
                                           @RequestParam String chartType) {

        String mbrId = null;
        if (userDetails != null) {
            mbrId = userDetails.getUserCd();

        }
        List<DetailResponse> stockData = itemDetailService.getAllInfo(offset, chartType,mbrId);
        return stockData;
    }

    /**
     * @Description 테이블 검색
     * @param keyword, chartType
     * @return List<DetailResponse>
     */
    @GetMapping("/search")
    @ResponseBody
    public List<DetailResponse> searchItem(@AuthenticationPrincipal CustomUserDetails userDetails,
                                           @RequestParam String keyword,
                                           @RequestParam String chartType) {

        String mbrId = null;
        if (userDetails != null) {
            mbrId = userDetails.getUserCd();

        }
        log.info("키워드:{},타입:{}",keyword,chartType);
        List<DetailResponse> stockData = itemDetailService.searchByKeyword(keyword,chartType, mbrId);
        return stockData;
    }

    /**
     * @Description 상세페이지 무한 그래프
     * @param name,from,to,chartType
     * @return List<ChartDto>
     */
    @GetMapping("/candle")
    @ResponseBody
    public List<ChartDto> getCandlestickData(@RequestParam String name,
                                             @RequestParam String from,
                                             @RequestParam String to,
                                             @RequestParam String chartType,
                                             @RequestParam(defaultValue = "D") String period) {

        return itemDetailService.getCandlestickData(name, from, to, period, chartType);
    }

    /**
     * @Description 즐겨찾기 추가
     * @param itemId, itemType
     * @return
     */
    @PostMapping("/favoriteChng")
    public void favoriteChng(@AuthenticationPrincipal CustomUserDetails userDetails,
                                             @RequestParam String itemId,
                                             @RequestParam String chartType,
                                             @RequestParam boolean favoriteYN,
                                             HttpServletResponse response) throws IOException {
        if (userDetails == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            response.getWriter().write("/login/home");
            return;
        }
        String mbrId = userDetails.getUserCd();
        bookMarkService.chngFavorite(itemId, chartType, mbrId, favoriteYN);
    }
}
