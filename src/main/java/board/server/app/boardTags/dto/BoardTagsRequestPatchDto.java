package board.server.app.boardTags.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.util.List;

@Getter
public class BoardTagsRequestPatchDto {
    private List<@NotBlank String> name;
}
