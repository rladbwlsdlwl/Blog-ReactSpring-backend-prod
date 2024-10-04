package board.server.app.comments.controller;

import board.server.app.comments.dto.CommentsRequestDto;
import board.server.app.comments.dto.CommentsRequestPatchDto;
import board.server.app.comments.dto.CommentsResponseDto;
import board.server.app.comments.entity.Comments;
import board.server.app.comments.service.CommentsService;
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

        Map<Long, List> commentsList = commentsService.getComments(boardIdList);

        return ResponseEntity.status(HttpStatus.CREATED).body(commentsList);
    }

    @PostMapping("/{boardId}")
    public ResponseEntity<?> createComments(@PathVariable("boardId") Long boardId,
                                            @RequestBody @Valid CommentsRequestDto commentsRequestDto,
                                            @AuthenticationPrincipal CustomUserDetail userDetail){

        Long userId = userDetail.getId();
        Long reqUserId = commentsRequestDto.getAuthor();
        String username = userDetail.getUsername();
        String reqUsername = commentsRequestDto.getAuthorName();
        if(!userId.equals(reqUserId) || !username.equals(reqUsername)) throw new BusinessLogicException(CustomExceptionCode.MEMBER_NO_PERMISSION);


        Comments comments = commentsService.setComments(CommentsRequestDto.of(commentsRequestDto));
        CommentsResponseDto commentsResponseDto = new CommentsResponseDto(comments);

        return ResponseEntity.status(HttpStatus.CREATED).body(commentsResponseDto);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<?> changeComments(@PathVariable("commentId") Long commentId,
                                            @RequestBody @Valid CommentsRequestPatchDto commentsRequestPatchDto,
                                            @AuthenticationPrincipal CustomUserDetail customUserDetail){

        // 댓글 작성자만 수정할 수 있음
        if(customUserDetail.getId() != commentsRequestPatchDto.getAuthor())
            throw new BusinessLogicException(CustomExceptionCode.MEMBER_NO_PERMISSION);

        commentsRequestPatchDto.setId(commentId);
        Comments comments = CommentsRequestPatchDto.of(commentsRequestPatchDto);

        commentsService.updateComments(comments);

        return ResponseEntity.status(HttpStatus.CREATED).build();
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
