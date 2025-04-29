package com.SNC.board.service;

import com.SNC.board.dto.CommentsDto;
import com.SNC.board.mapper.CommentsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentsService {

    private final CommentsMapper commentsMapper;

    //  댓글 쓰기
    @Transactional
    public void commentsInsert(String mbrCd, String boardId, String comment) {
        commentsMapper.commentsInsert(mbrCd, boardId, comment);
    }

    // 현재 값 찾기
    @Transactional
    public Long findByCommentsId() {
        return commentsMapper.findByCommentsId();
    }

    @Transactional
    public CommentsDto findByComments(Long cmtId) {
        return commentsMapper.getByComments(cmtId);
    }

    // 특정 게시글의 댓글 전체 검색
    @Transactional
    public List<CommentsDto> getByBoardComment(String brdId){
        return commentsMapper.getByBoardComment(brdId);
    }

    // 게시글의 댓글 갯수
    @Transactional
    public int getByCommentsCnt(String brdId) {
        return commentsMapper.getByCommentsCnt(brdId);
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(String id) {
        commentsMapper.deleteComment(id);
    }

}
