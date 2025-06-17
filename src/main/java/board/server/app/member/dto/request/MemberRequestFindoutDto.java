package board.server.app.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class MemberRequestFindoutDto {
    @Email
    private String email;
    @Size(min = 3, max = 255)
    private String name;
}
