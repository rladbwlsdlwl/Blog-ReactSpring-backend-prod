package board.server.app.boardTags.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.util.List;

@Getter
public class BoardTagsRequestPatchDto {
    @NotBlank
    List<String> tagname;
}
