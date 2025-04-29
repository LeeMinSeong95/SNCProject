package com.SNC.board.controller;

import com.SNC.board.dto.BoardDto;
import com.SNC.board.dto.CommentsDto;
import com.SNC.board.dto.LikesDto;
import com.SNC.board.service.BoardService;
import com.SNC.board.service.CommentsService;
import com.SNC.board.service.LikesService;
import com.SNC.login.model.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
@Slf4j
public class BoardController {

    private final BoardService boardService;
    private final CommentsService commentsService;
    private final LikesService likesService;

    // 주식게시판(게시판 메인화면)
    @GetMapping("/home")
    public String home(HttpServletRequest request, Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        // 주식(국내, 해외) 리스트 가져오기
        List<BoardDto> list = boardService.getStockList();
        model.addAttribute("list", list);

        //이주의 인기글 리스트 가져오기
        List<BoardDto> weekList = boardService.getWeekBoardList();
        model.addAttribute("weekList", weekList);

        return "board/boardStock";

    }

    // 코인게시판
    @GetMapping("/coin")
    public String coin(HttpServletRequest request, Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        // 코인 리스트 가져오기
        List<BoardDto> list = boardService.getCoinList();
        model.addAttribute("list", list);

        //이주의 인기글 리스트 가져오기
        List<BoardDto> weekList = boardService.getWeekBoardList();
        model.addAttribute("weekList", weekList);

        return "board/boardCoin";
    }

    // 익명게시판
    @GetMapping("/anonym")
    public String anonym(HttpServletRequest request, Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        // 익명(익명, QnA) 리스트 가져오기
        List<BoardDto> list = boardService.getAnonymList();
        model.addAttribute("list", list);

        //이주의 인기글 리스트 가져오기
        List<BoardDto> weekList = boardService.getWeekBoardList();
        model.addAttribute("weekList", weekList);

        return "board/boardAnonym";
    }

    // 게시판 글쓰기 페이지
    @GetMapping("/insertPage")
    public String stockInsert(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

        model.addAttribute("nickname", userDetails.getNickname());
        model.addAttribute("mbrCd", userDetails.getUserCd());
        return "/board/boardInsert";
    }


    // 게시판 글쓰기 등록
    @PostMapping("/insert")
    public String register(@RequestParam String type,
                           @RequestParam String title,
                           @RequestParam String content,
                           Model model,
                           @AuthenticationPrincipal CustomUserDetails userDetails) {

        String mbrCd = userDetails.getUserCd();

        // 게시글 등록 수행 로직
        boolean isSuccess = boardService.boardInsert(mbrCd, type, title, content);

        log.info("글쓰기 성공");

        if (isSuccess) {
            // type 값에 따라 리디렉션 경로 결정
            if ("0".equals(type) || "1".equals(type)) {
                return "redirect:/board/home"; // 주식 게시판으로 리디렉션
            } else if ("2".equals(type)) {
                return "redirect:/board/coin"; // 코인 게시판으로 리디렉션
            } else if ("3".equals(type) || "4".equals(type)) {
                return "redirect:/board/anonym"; // 익명 게시판으로 리디렉션
            }
            // 기본값 (주식 게시판)
            return "redirect:/main/home";

        } else {
            model.addAttribute("error", "게시글 등록에 실패했습니다.");
            return "/board/insert";  // 실패 시 폼으로 돌아가기
        }

    }

    // 게시글 수정 페이지
    @GetMapping("/detail/{brdId}")
    public String stockDetail(@AuthenticationPrincipal CustomUserDetails userDetails, Model model, @PathVariable String brdId) {
        // (1) 게시글 작성자의 정보 담기
        BoardDto boardDto = boardService.getBoardById(brdId);
        model.addAttribute("board", boardDto);

        // (2) 시큐리티로 로그인 중인 member_code 받아오기
        String memberCode = userDetails.getUserCd();
        model.addAttribute("mbrCd", memberCode);

        // (3) 시큐리티로 로그인 중인 member_nickname 받아오기
        String nickname = userDetails.getNickname();
        model.addAttribute("mbrNickname", nickname);

        // (4) 게시글의 댓글 전체 가져오기
        List<CommentsDto> list = commentsService.getByBoardComment(brdId);

        // (4-1) 익명 게시판인지 확인하고, 해당하는 댓글 작성자의 닉네임을 '익명'으로 변경
        if (boardDto.getBrdType().equals("3") || boardDto.getBrdType().equals("4")) {  // 익명 게시판
            list.forEach(cmt -> cmt.setMbrNickname("익명"));  // 댓글 작성자 닉네임을 "익명"으로 변경
        }
        model.addAttribute("cmtInfo", list);

        // (5) 게시글의 댓글 갯수 가져오기
        int cmtCnt = commentsService.getByCommentsCnt(brdId);
        model.addAttribute("cmtCnt", cmtCnt);

        // (6) 특정 게시물 좋아요 등록 유저
        LikesDto likesDto = likesService.getLikeUserInfo(memberCode, brdId);
        model.addAttribute("likeUser", likesDto);

        // (7) 특정 게시글의 좋아요 갯수 가져오기
        int likeCnt = likesService.getBoardLikeCnt(brdId);
        model.addAttribute("likeCnt", likeCnt);

        return "board/boardDetail";
    }

    // 게시글 수정하기
    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> boardUpdate(@RequestParam String title,
                                                           @RequestParam String content,
                                                           @RequestParam String brdId) {
        Map<String, Object> map = new HashMap<>();

        try {
            // 수정
            boardService.boardUpdate(title, content, brdId);
            // 수정 된 값 꺼내기
            BoardDto boardDto = boardService.getBoardById(brdId);
            log.info("board Dto : {}", boardDto);
            map.put("UpdateInfo", boardDto);

            log.info("update 실행");
            map.put("success", "OK");

            return new ResponseEntity<>(map, HttpStatus.OK);
        } catch (Exception e) {
            log.error("게시글 수정 실패, 오류: ", e);
            map.put("error", e.getMessage());

            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 게시글 삭제
    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity<String> deleteBoard(@RequestParam String id) {
        try{
            boardService.deleteBoard(id);  // 게시물 삭제
            log.info("삭제 성공");

            return ResponseEntity.ok("삭제 성공");
        } catch(Exception e) {
            log.error("게시글 삭제 실패, 오류: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제 실패");
        }
    }

    // 게시판 검색
    @PostMapping("/search")
    public ResponseEntity<Map<String, Object>> searchBoard(@RequestBody Map<String, Object> jsonData) {

        Map<String, Object> response = new HashMap<>();

        log.info(" 값 검색 : {}", jsonData);

        try {
            // 검색 리스트 받기
            List<BoardDto> searchList = boardService.searchBoard(jsonData);
            response.put("searchList", searchList);

            response.put("success", "OK");
            log.info("검색 성공 : {}", searchList);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            log.error("게시글 검색 실패, 오류: ", e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
