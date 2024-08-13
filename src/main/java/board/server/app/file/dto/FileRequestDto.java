package board.server.app.file.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
public class FileRequestDto {
    private List<String> currentFilename;
}
