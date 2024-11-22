package board.server.app.comments.dto;

import board.server.app.comments.entity.Comments;
import board.server.app.member.entity.Member;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentsRequestPatchDto {
    @NotNull
    private Long author;
    @NotNull
    private String contents;
    private Long id;


    public static Comments of(CommentsRequestPatchDto commentsRequestPatchDto){
        Member member = Member.builder()
                .id(commentsRequestPatchDto.getAuthor())
                .build();

        return Comments.builder()
                .id(commentsRequestPatchDto.getId())
                .member(member)
                .contents(commentsRequestPatchDto.getContents())
                .build();
    }
}
