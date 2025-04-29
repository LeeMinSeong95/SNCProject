package com.SNC.board.service;

import com.SNC.board.dto.BoardDto;
import com.SNC.board.mapper.BoardMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {

    private final BoardMapper boardMapper;

    // 이주의 게시글 리스트 가져오기
    public List<BoardDto> getWeekBoardList() {
        return boardMapper.getWeekBoardList();
    }

    // 주식(국내 홰외) 게시판 리스트
    public List<BoardDto> getStockList() {
        return boardMapper.selectStockList();
    }

    // 코인 게시판 리스트
    public List<BoardDto> getCoinList() {
        return boardMapper.selectCoinList();
    }
    
    // 익명 QnA 게시판 리스트
    public List<BoardDto> getAnonymList() {
        return boardMapper.selectAnonymList();
    }

    // 게시판 글쓰기
    @Transactional
    public boolean boardInsert(String mbrCd, String type, String title, String content) {
        try {
            int result = boardMapper.boardInsert(mbrCd, type, title, content);  // 매퍼 호출
            return result > 0;  // 결과가 0보다 크면 성공
        } catch (Exception e) {
            e.printStackTrace();
            return false;  // 예외 발생 시 실패 처리
        }
    }

    // id로 게시글 내용 가져오기
    public BoardDto getBoardById(String id) {
        return boardMapper.getBoardById(id);
    }

    // 게시글 수정하기
    @Transactional
    public void boardUpdate(String title, String content, String brdId) {
        boardMapper.boardUpdate(title, content, brdId);
    }

    // 게시글 삭제
    @Transactional
    public void deleteBoard(String id) {
        boardMapper.deleteBoard(id);
    }

    // 게시글 검색
    public List<BoardDto> searchBoard(Map<String, Object> data) {
        Map<String, Object> map = new HashMap<>();

        // null 처리
        String mainType = (String) data.get("mainType");
        String subType = (String) data.get("subType");
        String value = (String) data.get("value");
        String start = (String) data.get("startDate");
        String end = (String) data.get("endDate");

        if("".equals(value)) {
            value = null;
        }
        if("".equals(start) || "".equals(end)) {
            start = null;
            end = null;
        }

        // 다시 담기
        map.put("mainType", mainType);
        map.put("subType", subType);
        map.put("value", value);
        map.put("start", start);
        map.put("end", end);

        log.info("value : {}, {}, {}, {}, {}", mainType, subType, value, start, end);


        return boardMapper.searchBoard(map);
    }
}
