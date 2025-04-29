package com.SNC.board.service;

import com.SNC.board.dto.LikesDto;
import com.SNC.board.mapper.LikesMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikesService {

    private final LikesMapper likesMapper;

    // 현재 좋아요 상태
    public boolean isLikeExists(String mbrCd, String brdId) {
        return likesMapper.isLikeExists(mbrCd, brdId) > 0;
    }

    // 좋아요 이미 되어 있으면 좋아요 수정
    @Transactional
    public void deleteLike(String mbrCd, String brdId) {
        likesMapper.deleteLike(mbrCd, brdId);
    }

    // 좋아요 등록
    @Transactional
    public void insertLike(String mbrCd, String brdId) {
        likesMapper.insertLike(mbrCd, brdId);
    }

    // 특정 게시글의 특정 유저의 정보
    public LikesDto getLikeUserInfo(String mbrCd, String brdId) {
        return likesMapper.getLikeUserInfo(mbrCd, brdId);
    }

    // 특정 게시글 좋아요 갯수
    public int getBoardLikeCnt(String brdId) {
        return likesMapper.getBoardLikeCnt(brdId);
    }

}
