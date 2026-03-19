package board.server.app.boardTags.controller;


import board.server.app.boardTags.dto.BoardTagsRequestDto;
import board.server.app.boardTags.dto.BoardTagsRequestPatchDto;
import board.server.app.boardTags.dto.BoardTagsDto;
import board.server.app.boardTags.dto.BoardTagsResponseDto;
import board.server.app.boardTags.entity.BoardTags;
import board.server.app.boardTags.service.BoardTagsService;
import board.server.config.jwt.CustomUserDetail;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BoardTagsController {

    @Autowired
    private BoardTagsService boardTagsService;

    
    // 게시글에 있는 태그 조회
    @GetMapping("/tags/{boardId}")
    public ResponseEntity<?> findTags(@PathVariable Long boardId){

        List<BoardTags> taglist = boardTagsService.getTaglist(boardId);
        List<BoardTagsDto> taglistDto = taglist.stream().map(BoardTagsDto::of).toList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(new BoardTagsResponseDto(taglistDto));
    }


    // 게시글에 있는 태그 작성
    @PostMapping("/tags/{boardId}")
    public ResponseEntity<?> createTags(@PathVariable Long boardId,
                                        @RequestBody @Valid BoardTagsRequestDto boardTagsRequestDto,
                                        @AuthenticationPrincipal CustomUserDetail customUserDetail){

        boardTagsService.join(boardTagsRequestDto.getName(), boardId, customUserDetail.getId());

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    
    // 게시글에 있는 태그 수정
    @PatchMapping("/tags/{boardId}")
    public ResponseEntity<?> updateTags(@PathVariable Long boardId,
                                        @RequestBody @Valid BoardTagsRequestPatchDto boardTagsRequestPatchDto,
                                        @AuthenticationPrincipal CustomUserDetail customUserDetail){

        boardTagsService.update(boardTagsRequestPatchDto.getTagname(), boardId, customUserDetail.getId());

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
