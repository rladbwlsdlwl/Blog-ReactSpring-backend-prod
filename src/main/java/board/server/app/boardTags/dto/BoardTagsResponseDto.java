package board.server.app.boardTags.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class BoardTagsResponseDto {
    private List<BoardTagsDto> data;
}
