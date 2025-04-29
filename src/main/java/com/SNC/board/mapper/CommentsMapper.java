package com.SNC.board.mapper;

import com.SNC.board.dto.CommentsDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentsMapper {

    // 댓글 쓰기
    void commentsInsert(String mbrCd, String boardId, String comment);

    // 댓글 시퀀스 값 찾기
    Long findByCommentsId();

    // 게시글 댓글 찾기
    CommentsDto getByComments(Long cmtId);

    // 특정 게시글의 댓글 전체 검색
    List<CommentsDto> getByBoardComment(String brdId);

    // 게시글의 댓글 갯수
    int getByCommentsCnt(String brdId);

    // 댓글 삭제
    void deleteComment(@Param("id") String id);
}
