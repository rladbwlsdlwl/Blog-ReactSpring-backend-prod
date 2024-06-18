package board.server.app.member.entity;

import board.server.app.enums.RoleType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//@Entity
public class Member {
//    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private final String name;
    private final String email;
    private final String password;
    private RoleType roleType;

    @Builder
    public Member(Long id, String name, String email, String password, RoleType role){
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.roleType = role;
    }

    @Builder
    public Member(Long id, String name, String email, String password){
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    @Builder
    public Member(String name, String email, String password){
        this.name = name;
        this.email = email;
        this.password = password;
    }
}
