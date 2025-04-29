package com.SNC.itemdetail.mapper;

import com.SNC.itemdetail.vo.DetailInfoDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ItemDetailMapper {

    //모든 한국주식
    List<DetailInfoDto> findAllKrStock(@Param("offset")int offset);

    //모든 미국주식
    List<DetailInfoDto> findAllUsStock(@Param("offset")int offset);

    //모든 코인
    List<DetailInfoDto> findAllCoins(@Param("offset")int offset);

    //키워드로 한국주식검색
    List<DetailInfoDto> searchByKeyword(@Param("keyword")String keyword);

    //키워드로 한국주식검색
    List<DetailInfoDto> searchByUsKeyword(@Param("keyword")String keyword);

    //키워드로 코인검색
    List<DetailInfoDto> searchByCoinKeyword(@Param("keyword")String keyword);

    //한국주식이름으로 symbol
    String findKrStockSymbol(@Param("stockName")String stockName);

    //미국주식이름으로 symbol
    String findUsaStockSymbol(@Param("stockName")String stockName);

    //코인이름으로 symbol
    String findCoinSymbol(@Param("coinName")String coinName);
    
    //즐겨찾기 유무 확인
    int findBookMark(@Param("itemType")String itemType,
                     @Param("itemId")String itemId,
                     @Param("mbrId")String mbrId);

    //즐겨찾기 기록 확인
    int findBookMarkHistory(@Param("itemType")String itemType,
                     @Param("itemId")String itemId,
                     @Param("mbrId")String mbrId);
    
    //BookMark Update
    void updateBookMark(@Param("itemType")String itemType,
                        @Param("itemId")String itemId,
                        @Param("mbrId")String mbrId,
                        @Param("favoriteYN")String yn);

    //BookMark Insert
    void insertBookMark(@Param("itemType")String itemType,
                        @Param("itemId")String itemId,
                        @Param("mbrId")String mbrId);
}