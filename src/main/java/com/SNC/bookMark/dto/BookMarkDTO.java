package com.SNC.bookMark.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class BookMarkDTO {
    private String name;            //종목 이름
    private String itemType;        //종목 타입 *001: 한국주식, 002: 미국주식, 003: 코인
    private String ticker;          //종목의 고유 번호( ticker OR symbol )
    private String markId;          //북마크 ID
    private String itemId;          //종목 ID
}