package board.server.app.boardTags.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.util.List;

@Getter
public class BoardTagsRequestDto {
    private List<@NotBlank String> name;
}
