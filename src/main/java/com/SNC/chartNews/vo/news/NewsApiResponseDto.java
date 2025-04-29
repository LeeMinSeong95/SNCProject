package com.SNC.chartNews.vo.news;

import com.SNC.chartNews.util.news.JsoupCrawling;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class NewsApiResponseDto {

    @JsonSetter("items")
    private List<NewsSummaryDto> items;

    /**
     * 네이버뉴스만 반환. (getNewsResponseDtoWithImg()에서의 메서드 호출용)
     * @return List
     */
    public List<NewsSummaryDto> getOnlyNaverNews() {
        return items.stream()                                                                                           //items (list객체) 순회하며 조건에 부합하는 값들만 return
                .filter(items -> items.getLink().startsWith("https://n.news")
                        || items.getLink().startsWith("https://news.naver.com"))
                .collect(Collectors.toList());                                                                          // List 변환
    }

    /**
     * 네이버뉴스 + 이미지
     * @return List
     */
    public List<NewsResponseDto> getNewsResponseDtoWithImg(){
        return getOnlyNaverNews().stream()
                .map(items -> items.toNewsResponseDto(new JsoupCrawling()))
                .filter(NewsResponseDto::validateImageLink)                                                             //NewsResponseDto.validateImageLink() 메서드 호출하여 true인 요소만 filtering
                .limit(100L)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "NewsApiResponseDto{" +
                "items="+items+
                '}';
    }
}
