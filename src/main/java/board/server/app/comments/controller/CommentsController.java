package board.server.app.comments.controller;

import board.server.app.comments.dto.CommentsRequestDto;
import board.server.app.comments.service.CommentsService;
import board.server.config.jwt.CustomUserDetail;
import board.server.error.errorcode.CustomExceptionCode;
import board.server.error.exception.BusinessLogicException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
public class CommentsController {

    @Autowired
    private CommentsService commentsService;

    @GetMapping
    public ResponseEntity<?> readComments(@RequestParam List<Long> boardId){
        Map<Long, List> commentsList = commentsService.getComments(boardId);

        return ResponseEntity.status(HttpStatus.CREATED).body(commentsList);
    }

    @PostMapping("/{boardId}")
    public ResponseEntity<?> createComments(@PathVariable("boardId") Long boardId,
                                            @RequestBody @Valid CommentsRequestDto commentsRequestDto,
                                            @AuthenticationPrincipal CustomUserDetail userDetail){

        Long userId = userDetail.getId();
        Long reqUserId = commentsRequestDto.getAuthor();
        if(userId != reqUserId) throw new BusinessLogicException(CustomExceptionCode.MEMBER_NO_PERMISSION);


        Long id = commentsService.setComments(CommentsRequestDto.of(commentsRequestDto));

        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComments(@PathVariable("commentId") Long commentId,
                                            @AuthenticationPrincipal CustomUserDetail customUserDetail){

        Long userId = customUserDetail.getId();

        commentsService.removeComments(commentId, userId);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
