package com.SNC.chartNews.util.news;

public class NaverApiCallException extends RuntimeException {

    public NaverApiCallException() {
        super("Naver API 호출 중 오류가 발생했습니다.");
    }

    // 메시지와 원인(예외)을 지정할 수 있는 생성자
    public NaverApiCallException(String message, Throwable cause) {
        super(message, cause);
    }

}
