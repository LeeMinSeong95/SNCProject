package com.SNC.bookMark.mapper;

import com.SNC.bookMark.dto.BookMarkDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BookMarkMapper {

    //MbrId로 즐겨찾기 및 Mark_NM 가져오기
    List<BookMarkDTO> findAllBookMarkByMember(@Param("mbrId")String mbrId);
}