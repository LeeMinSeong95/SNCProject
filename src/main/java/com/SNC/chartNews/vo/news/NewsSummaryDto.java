package com.SNC.chartNews.vo.news;

import com.SNC.chartNews.util.news.JsoupCrawling;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.select.Elements;

import java.util.Optional;

@Getter
@Slf4j
public class NewsSummaryDto {
    // JSON 객체에서 "title"이라는 키의 값을 이 필드로 설정
    @JsonSetter("title")
    private String title;

    @JsonSetter("link")
    private String link;

    @JsonSetter("description")
    private String description;

    /**
     * image 존재하는 경우 이미지 넣어 반환. image 없는 경우 image null로 setting. 후에 validateImageLink 통해 존재여부 check.
     * @param jsoupCrawling
     * @return NewsResponseDto
     */
    public NewsResponseDto toNewsResponseDto(JsoupCrawling jsoupCrawling) {
        String query = "#contents img";                                                                                 // Element name
        Optional<Elements> jsoupElements = jsoupCrawling.getJsoupElements(link, query);                                 // element 객체

        if ( jsoupElements.isPresent() ){
            return NewsResponseDto.builder()
                    .title(title)
                    .link(link)
                    .description(description)
                    .imageLink(jsoupElements.get().attr("data-src"))                                         // key( "data-src" ) 에 할당된 값 가져옴
                    .build();
        }

        return NewsResponseDto.builder()
                .title(title)
                .link(link)
                .description(description)
                .build();
    }

    public String toString() {
        return "NewsSummaryDto{" +
                "title='" + title + '\'' +
                ", link='" + link + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
