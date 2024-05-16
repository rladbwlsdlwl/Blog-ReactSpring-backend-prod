package board.server.app.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class UserRequestDto {
    private Long id;
    @NotNull
    @Size(min = 3, max = 255)
    private final String name;
    @Email
    @NotNull
    @NotEmpty
    private final String email;
    @NotNull
    @Size(min = 3, max = 255)
    private final String password;
}
