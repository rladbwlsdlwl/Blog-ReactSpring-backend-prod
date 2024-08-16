package board.server.app.file.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class FileResponseDto {
    private byte[] file;
    private String originalFilename;
    private String currentFilename;
    private Long postId;
}
