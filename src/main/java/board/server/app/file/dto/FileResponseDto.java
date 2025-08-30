package board.server.app.file.dto;


import board.server.app.file.entity.FileEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class FileResponseDto {
    private String file; // url
    private String originalFilename;
    private String currentFilename;
    private Long postId;

    public static FileResponseDto of(FileEntity fileEntity){
        return FileResponseDto.builder()
                .originalFilename(fileEntity.getOriginalFilename())
                .currentFilename(fileEntity.getCurrentFilename())
                .postId(fileEntity.getBoard().getId())
                .build();
    }
}
