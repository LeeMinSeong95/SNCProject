package com.SNC.coinFavorites.vo;

import lombok.Data;

@Data
public class BookmarkVo {

    private String bookmarkId;      // 고유 즐찾 번호
    private String coinId;          // 고유 코인 번호
    private String bookmarkYn;      // 즐찾 여부 Y/N
    private String coinMarkNm;      // 고유 코인 검색이름
    private String coinKrNm;        // 코인 한글명
    private String coinEngNm;       // 코인 영문명
}
