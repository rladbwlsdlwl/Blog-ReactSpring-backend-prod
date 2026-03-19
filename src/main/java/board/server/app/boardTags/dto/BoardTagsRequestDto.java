package board.server.app.boardTags.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.util.List;

@Getter
public class BoardTagsRequestDto {
    @NotBlank
    private List<String> name;
}
