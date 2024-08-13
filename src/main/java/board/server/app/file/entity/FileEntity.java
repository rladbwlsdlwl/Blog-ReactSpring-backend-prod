package board.server.app.file.entity;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class FileEntity {
    private Long id;
    private Long postId; // fk - board_id
    private String originalFilename;
    private String currentFilename;
}
