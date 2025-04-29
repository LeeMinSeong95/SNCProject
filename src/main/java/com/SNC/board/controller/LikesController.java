package com.SNC.board.controller;

import com.SNC.board.dto.LikesDto;
import com.SNC.board.service.LikesService;
import com.SNC.login.model.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/likes")
@Slf4j
public class LikesController {

    private final LikesService likesService;

    @PostMapping("/likeOnOff")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> insertAndUpdateLikes(@RequestParam String brdId,
                                                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        Map<String, Object> response = new HashMap<>();

        try{
            // 접속 중인 멤버 정보
            String mbrCd = userDetails.getUserCd();

            // 현재 게시물에 대한 좋아요 상태 확인 (없으면 insert, 있으면 delete)
            boolean UserLikeStatus = likesService.isLikeExists(mbrCd, brdId);

            if (UserLikeStatus) {
                // 이미 좋아요가 있으면 delete 실행
                likesService.deleteLike(mbrCd, brdId);
                log.info("좋아요 삭제, 게시물: {}", brdId);
                response.put("deleteSuccess", "deleteOK");
            } else {
                // 좋아요가 없으면 insert 실행
                likesService.insertLike(mbrCd, brdId);
                log.info("좋아요 등록, 게시물: {}", brdId);
                response.put("insertSuccess", "insertOK");
            }

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch(Exception e) {
            log.error("좋아요 기능 실패, 오류: ", e);
            response.put("error", e.getMessage());

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
