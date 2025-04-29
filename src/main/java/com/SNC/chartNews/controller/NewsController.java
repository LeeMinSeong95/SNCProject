package com.SNC.chartNews.controller;

import com.SNC.chartNews.service.NewsService;
import com.SNC.chartNews.util.news.NewsApiResponseRepository;
import com.SNC.chartNews.vo.news.NewsResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Component
@Controller
@RequiredArgsConstructor
@Slf4j
//@RestController   //데이터 확인용
public class NewsController {

    private final NewsService newsService;
    private final NewsApiResponseRepository newsApiResponseRepository;

    /**
     * JSON API: 뉴스 데이터를 JSON형태로 변환(API 호출 테스트용)
     * @param keyword
     * @return JSON
     * https://openapi.naver.com/v1/search/news.json?query="비트코인" GET요청 sample
     */
    @GetMapping("/jsonCheck")
    public ResponseEntity<List<NewsResponseDto>> getNewsWithKeyword(@RequestParam("keyword") String keyword) {
        return ResponseEntity.ok(newsService.getNaverNewsWithKeyword(keyword));
    }

    /**
     * HTML 뷰 : Thymeleaf 템플릿을 이용한 뉴스 데이터 크롤링
     * @Description http://localhost:8090/news?keyword="이더리움"
     * @param keyword
     * @return html
     */
    @GetMapping("/news")
    public String checkNews(@RequestParam("keyword") String keyword,Model model) {
        List<NewsResponseDto> newsList = newsService.getNaverNewsWithKeyword(keyword);
        model.addAttribute("keyword", keyword);
        model.addAttribute("newsList", newsList);
        return "/chartNews/searchResultPage";
    }

    /**
     * @Description 뉴스메뉴 nav 화면 (전체 뉴스)
     * @param model
     * @return
     */
    @GetMapping("/allCoinNews")
    public String checkNews(Model model) {
        String keyword = "코인";
        List<NewsResponseDto> newsList = newsService.getNaverNewsWithKeyword(keyword);
        model.addAttribute("keyword", keyword);
        model.addAttribute("newsList", newsList);
        return "/chartNews/searchResultPage";
    }
}
