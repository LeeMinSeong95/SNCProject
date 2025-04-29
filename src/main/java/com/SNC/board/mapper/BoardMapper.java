package com.SNC.board.mapper;

import com.SNC.board.dto.BoardDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface BoardMapper {

    // 이주의 글 리스트 가져오기
    List<BoardDto> getWeekBoardList();

    // 게시판 메인(주식)에 뿌려줄 보드 정보
    List<BoardDto> selectStockList();

    // 게시판 코인에 뿌려줄 보드 정보
    List<BoardDto> selectCoinList();

    // 게시판 익명, QnA에 뿌려줄 보드 정보
    List<BoardDto> selectAnonymList();

    // 게시판 글 등록
    int boardInsert(@Param("id") String mbrCd,
                    @Param("type") String type,
                    @Param("title") String title,
                    @Param("content") String content);

    // id 해당 게시글 가져오기
    BoardDto getBoardById(String id);

    // 게시글 수정
    void boardUpdate(@Param("title")String title, @Param("content")String content, @Param("brdId")String brdId);

    // 게시글 삭제
    void deleteBoard(@Param("id") String id);

    // 게시글 검색
    List<BoardDto> searchBoard(Map<String,Object> data);
}
