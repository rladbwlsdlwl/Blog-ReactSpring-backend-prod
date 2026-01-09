package board.server.app.comments.controller;

import board.server.app.board.entity.Board;
import board.server.app.comments.dto.CommentsRequestDto;
import board.server.app.comments.dto.CommentsRequestPatchDto;
import board.server.app.comments.dto.CommentsResponseDto;
import board.server.app.comments.dto.CommentsResponsePatchDto;
import board.server.app.comments.entity.Comments;
import board.server.app.comments.service.CommentsService;
import board.server.app.member.entity.Member;
import board.server.config.jwt.CustomUserDetail;
import board.server.error.errorcode.CustomExceptionCode;
import board.server.error.exception.BusinessLogicException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@Slf4j
public class CommentsController {

    @Autowired
    private CommentsService commentsService;

    @GetMapping
    public ResponseEntity<?> readComments(@RequestParam(value = "boardId", required = false) List<Long> boardIdList){
        boardIdList = boardIdList == null ? new ArrayList<>() : boardIdList;

        Map<Long, List<CommentsResponseDto>> commentsList = commentsService.getComments(boardIdList);

        return ResponseEntity.status(HttpStatus.CREATED).body(commentsList);
    }

    @PostMapping("/{boardId}")
    public ResponseEntity<?> createComments(@PathVariable("boardId") Long boardId,
                                            @RequestBody @Valid CommentsRequestDto commentsRequestDto,
                                            @AuthenticationPrincipal CustomUserDetail userDetail){

        Long userId = userDetail.getId();
        Long parentId = commentsRequestDto.getParentId() == 0 ? null : commentsRequestDto.getParentId();

        Member member = Member.builder()
                .id(userId)
                .build();

        Comments comments = Comments.builder()
                .contents(commentsRequestDto.getContents())
                .member(member)
                .createdAt(LocalDateTime.now())
                .build();


        Comments savedComments = commentsService.setComments(comments, boardId, parentId);

        return ResponseEntity.status(HttpStatus.CREATED).body(CommentsResponseDto.of(savedComments));
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<?> changeComments(@PathVariable("commentId") Long commentId,
                                            @RequestBody @Valid CommentsRequestPatchDto commentsRequestPatchDto,
                                            @AuthenticationPrincipal CustomUserDetail customUserDetail){

        Long userId = customUserDetail.getId();
        Member member = Member.builder()
                .id(userId)
                .build();

        Comments comments = Comments.builder()
                .member(member)
                .createdAt(LocalDateTime.now()) // 수정일 반영
                .contents(commentsRequestPatchDto.getContents())
                .build();

        commentsService.updateComments(comments, commentId);

        CommentsResponsePatchDto commentsResponsePatchDto = CommentsResponsePatchDto.of(comments);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentsResponsePatchDto);
    }
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComments(@PathVariable("commentId") Long commentId,
                                            @AuthenticationPrincipal CustomUserDetail customUserDetail){

        // 댓글 작성자나 게시글 작성자만 삭제할 수 있음
        Long userId = customUserDetail.getId();

        commentsService.removeComments(commentId, userId);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
