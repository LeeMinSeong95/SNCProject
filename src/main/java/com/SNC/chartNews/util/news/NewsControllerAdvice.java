package com.SNC.chartNews.util.news;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice   // 예외처리 전역적 관리 @
public class NewsControllerAdvice {
    @ExceptionHandler(NewsNullException.class)
    public ResponseEntity<String> handleNewsNullException(NewsNullException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(NaverApiCallException.class)
    public ResponseEntity<String> handleNaverApiCallException(NaverApiCallException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
    }

}
