package board.server.app.board.dto;

import board.server.app.board.entity.Board;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class BoardResponseHomeDto {
    private Long id;
    private String title;
    private String contents;
    private Long memberId;
    private String username;

    public BoardResponseHomeDto(Board board){
        this.id = board.getId();
        this.title = board.getTitle();
        this.contents = board.getTitle();
        this.memberId = board.getAuthor();
        this.username = board.getUsername();
    }
}
