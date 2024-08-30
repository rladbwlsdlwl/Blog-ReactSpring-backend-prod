package board.server.app.likes.controller;

import board.server.app.likes.dto.LikesRequestDto;
import board.server.app.likes.entity.Likes;
import board.server.app.likes.service.LikesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/likes")
public class LikesController {

    @Autowired
    private LikesService likesService;

    @GetMapping
    public ResponseEntity<Object> readLikes(@RequestParam("boardId") List<Long> boardIdList){
        // 해당 게시판의 좋아요 목록 반환
        // 홈 화면, 회원 홈 게시판
        Map<Long, List<Likes>> likesList = likesService.getLikesList(boardIdList);

        return ResponseEntity.status(HttpStatus.CREATED).body(likesList);
    }


    @PostMapping("/{boardId}")
    public ResponseEntity<?> saveLike(@RequestBody @Valid LikesRequestDto likesRequestDto,
                                      @PathVariable("boardId") Long boardId){
        // 게시판 좋아요 작성
        Likes likes = Likes.builder()
                .postId(likesRequestDto.getBoardId())
                .author(likesRequestDto.getAuthor())
                .build();

        Likes savedLikes = likesService.setLikes(likes);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedLikes);
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<?> deleteLike(@PathVariable("boardId") Long boardId,
                                        @RequestParam("userId") Long liked_userId){
        // 게시판 좋아요 삭제
        Likes likes = Likes.builder()
                .author(liked_userId)
                .postId(boardId)
                .build();

        likesService.removeLikes(likes);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
