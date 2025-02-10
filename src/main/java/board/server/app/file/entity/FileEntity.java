package board.server.app.file.entity;


import board.server.app.board.entity.Board;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "file_table")
public class FileEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "original_filename")
    private String originalFilename;
    @Column(name = "current_filename")
    private String currentFilename;
    @Lob
    private byte[] data; // blob 타입의 이미지 파일 데이터

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;
}
