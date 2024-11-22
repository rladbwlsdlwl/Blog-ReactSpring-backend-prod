package board.server.app.comments.dto;


import board.server.app.board.entity.Board;
import board.server.app.comments.entity.Comments;
import board.server.app.member.entity.Member;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CommentsRequestDto {
    @NotNull
    private Long boardId;
    @NotNull
    private Long author;
    @NotNull
    private String authorName;
    @NotNull
    private Long parentId;
    @NotNull
    private String contents;

    public static Comments of(CommentsRequestDto commentsRequestDto){
        Board board = Board.builder()
                .id(commentsRequestDto.getBoardId())
                .build();
        Member member = Member.builder()
                .id(commentsRequestDto.getAuthor())
                .name(commentsRequestDto.getAuthorName())
                .build();
        Comments comments = Comments.builder()
                .id(commentsRequestDto.getParentId())
                .build();

        return Comments.builder()
                .board(board)
                .member(member)
                .comments(commentsRequestDto.getParentId() != 0? comments: null)
                .contents(commentsRequestDto.getContents())
                .build();
    }
}
