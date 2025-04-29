package com.SNC.board.mapper;

import com.SNC.board.dto.LikesDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LikesMapper {

    // 현재 좋아요 상태
    int isLikeExists(@Param("mbrCd") String mbrCd,
                     @Param("brdId") String brdId);

    // 이미 좋아요 되어 있으면 삭제
    void deleteLike(@Param("mbrCd") String mbrCd,
                    @Param("brdId") String brdId);

    // 좋아요 등록
    void insertLike(@Param("mbrCd") String mbrCd,
                    @Param("brdId") String brdId);

    // 특정 게시글의 특정 유저의 정보
    LikesDto getLikeUserInfo(@Param("mbrCd") String mbrCd,
                             @Param("brdId") String brdId);

    // 특정 게시글 좋아요 갯수
    int getBoardLikeCnt(@Param("brdId") String brdId);
}
