package com.SNC.chartNews.service;

import com.SNC.chartNews.util.news.NewsApiResponseRepository;
import com.SNC.chartNews.util.news.NewsNullException;
import com.SNC.chartNews.vo.news.NewsApiResponseDto;
import com.SNC.chartNews.vo.news.NewsResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NewsService {

    private final NewsApiResponseRepository newsApiResponseRepository;
    public List<NewsResponseDto> getNaverNewsWithKeyword(String keyword) {
        NewsApiResponseDto newsApiResponseDto = newsApiResponseRepository.getNewsApiResponseDto(keyword)
                .orElseThrow(NewsNullException::new);
        return newsApiResponseDto.getNewsResponseDtoWithImg();
    }
}

