package board.server.app.boardTags.dto;

import board.server.app.boardTags.entity.BoardTags;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BoardTagsDto {
    private Long id;
    private String tagname;


    public static BoardTagsDto of(BoardTags boardTags){
        return BoardTagsDto.builder()
                .id(boardTags.getId())
                .tagname(boardTags.getTags().getName())
                .build();
    }
}
