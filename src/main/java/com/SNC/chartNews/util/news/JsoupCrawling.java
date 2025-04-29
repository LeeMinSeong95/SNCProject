package com.SNC.chartNews.util.news;

import org.jsoup.Jsoup;
import org.jsoup.Connection;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Optional;


public class JsoupCrawling {

    /**
     * url의 http 연결객체 생성 메서드.
     * @param url
     * @return Connection
     */
    public Connection getConnection(String url) {
        return Jsoup.connect(url);                                                                                      // HTML ->DOM 형태로 만들어 요소에 접근 가능
    }

    /**
     * DOM 형태의 데이터 요소를 가져옴.
     * @param url
     * @param query : 원하는 html 요소 선택하는데 사용.
     * @return Optional (( 사용하는쪽에서 null 처리 ))
     */
    public Optional<Elements> getJsoupElements(String url,String query){
        Connection conn = getConnection(url);

        try {
            return Optional.of(conn.get().select(query));                                                               // GET 요청보내고 데이터 요소 select
        } catch (IOException e) {
            return Optional.empty();                                                                                    //NPE 방지용
        }
    }

}
