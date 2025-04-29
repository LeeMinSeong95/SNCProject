package com.SNC.chartNews.util.news;

public class NewsNullException extends RuntimeException{

    public NewsNullException() {
        super("뉴스가 존재하지 않습니다.");        // 기본 에러메세지
    }

    public NewsNullException(String msg) {
        super(msg);                             // 에러메세지 직접 지정
    }
}
