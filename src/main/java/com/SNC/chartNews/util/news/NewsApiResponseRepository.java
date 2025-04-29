package com.SNC.chartNews.util.news;

import com.SNC.chartNews.vo.news.NewsApiResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Slf4j
@Component // 데이터 접근 로직 구현
public class NewsApiResponseRepository {
    @Value("${news.api.secret}")
    private String newsApiKey;

    @Value("${news.api.client}")
    private String newsApiClient;

    /**
     * News 검색 API 호출 후 결과 받아오기.
     *
     * @param keyword 검색어
     * @return Optional : NewsApiResponseDto (( 사용하는쪽에서 null 처리 ))
     */
    public Optional<NewsApiResponseDto> getNewsApiResponseDto(String keyword) {

        RestTemplate restTemplate = new RestTemplate();                                                                 //RestTemplate : HTTP 요청 & 응답 받는객체
        HttpHeaders headers = getNaverApiRequestHeaders();

        String url = "https://openapi.naver.com/v1/search/news.json?query=" + keyword + "&display=100&start=1&sort=date";

        ResponseEntity<NewsApiResponseDto> newsApiResponseDtoEntity
                = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), NewsApiResponseDto.class);
        log.info("Request URL: {}", url);
        log.info("body에 넘어오는 값 {}",newsApiResponseDtoEntity.getBody());


        /*
        *   url: 호출할 API의 주소.
            HttpMethod.GET: GET 방식으로 요청.
            new HttpEntity<>(headers): 요청에 포함할 헤더를 지정.
            NewsApiResponseDto.class: 응답 데이터가 해당 클래스의 형태로 매핑되기를 기대
        * */

        if (newsApiResponseDtoEntity.getStatusCode().is2xxSuccessful()) {                                                // 200 상태코드로 통신 잘 이뤄진경우
            return Optional.ofNullable(newsApiResponseDtoEntity.getBody());
        }
        throw new NaverApiCallException();                                                                              // 통신코드 200 아닐경우 : Advice에서 처리
    }

    private HttpHeaders getNaverApiRequestHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", newsApiClient);
        headers.set("X-Naver-Client-Secret", newsApiKey);
        return headers;
    }
}
