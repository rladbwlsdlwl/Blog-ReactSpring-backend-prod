package board.server.app.board.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BoardRequestDto {
    @NotEmpty
    private final String title;
    @NotEmpty
    private final String contents;
}
