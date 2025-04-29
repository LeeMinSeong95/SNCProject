package com.SNC.itemdetail.vo;

import lombok.Data;

@Data
public class DetailInfoDto {
    private String itemId;          // 고유 번호
    private String itemMarkNm;      // 고유 코드
    private String itemKrNm;        // 종목명
}
