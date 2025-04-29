package com.SNC.chartNews.controller;

import com.SNC.chartNews.vo.chart.ChartVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.SNC.chartNews.service.ChartService;

import java.util.List;

@Component
@Controller
@RequiredArgsConstructor
@Slf4j
//@RestController     //데이터 확인시에만 주석풀고사용
public class ChartController {

    private final ChartService chartService;

    //데이터 확인용 http://localhost:8090/candles/month?keyword=비트코인
    @GetMapping("/candles/month")
    public List<ChartVO> getMonthCandles(@RequestParam String keyword) {
        return chartService.getMonthCandleData(keyword);
    }

    /**
     * @Description http://localhost:8090/candles?keyword=이더리움
     * @param keyword
     * @param model
     * @return
     */
    @GetMapping("/candle")
    public String getCandle(@RequestParam String keyword,@RequestParam(defaultValue = "month") String type, Model model) {
        List<ChartVO> chartList;

        switch (type) {
            case "week":
                chartList = chartService.getWeekCandleData(keyword);
                break;
            case "day":
                chartList = chartService.getDayCandleData(keyword);
                break;
            case "minute":
                chartList = chartService.getMinuteCandleData(keyword);
                break;
            default: // "month"
                chartList = chartService.getMonthCandleData(keyword);
                break;
        }

        model.addAttribute("chartList", chartList);
        model.addAttribute("keyword", keyword);
        model.addAttribute("type", type);

        return "/chartNews/chartResultPageMonth";
    }

    /**
     * @Description navbar 눌렀을때 로그인된 회원의 번호를 받아와 해당 회원이 즐찾한 것들 보여줄 페이지
     * @param mbrCd 회원 테이블 pk (회원 번호)       @RequestParam int mbrCd
     */
    @GetMapping("/favorite")
    public String getFavorite(Model model) {
        return "/favorites/favoriteMainPage";
    }
}
