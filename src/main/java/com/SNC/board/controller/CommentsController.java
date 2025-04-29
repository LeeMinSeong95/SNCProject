package com.SNC.board.controller;

import com.SNC.board.dto.CommentsDto;
import com.SNC.board.service.CommentsService;
import com.SNC.login.model.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oracle.jdbc.proxy.annotation.Post;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/comments")
public class CommentsController {

    private final CommentsService commentsService;

    // 댓글 쓰기
    @PostMapping("/insert")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> insert(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                      Model model,
                                                      @RequestParam String comment,
                                                      @RequestParam String boardId) {
        Map<String, Object> map = new HashMap<>();
        try {
            // insert
            String mbrCd = userDetails.getUserCd(); // 접속중인 멤버 id
            log.info("boardId: {}, comment: {}", boardId, comment);
            log.info("UserDetails: {}", userDetails);
            commentsService.commentsInsert(mbrCd, boardId, comment);

            // 현재 댓글 시퀀스 값 가져오기
            Long cmtId = commentsService.findByCommentsId();

            // select
            CommentsDto commentsDto = commentsService.findByComments(cmtId);

            map.put("cmt", commentsDto);
            map.put("success", "OK");
            log.info("댓글 쓰기 성공 : {}", commentsDto);

            return new ResponseEntity<>(map, HttpStatus.OK);
        } catch (Exception e) {
            log.error("댓글 쓰기 실패", e);
            map.put("error", e.getMessage());

            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    // 댓글 삭제
    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity<String> deleteComment(@RequestParam String id) {
        try {
            commentsService.deleteComment(id);  // 댓글 삭제
            log.info("댓글 삭제 성공");

            return ResponseEntity.ok("삭제 성공");
        } catch (Exception e) {

            log.error("댓글 삭제 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제 실패");
        }
    }

}
