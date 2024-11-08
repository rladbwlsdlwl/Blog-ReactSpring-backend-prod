package board.server.app.role.entity;

import board.server.app.enums.RoleType;
import board.server.app.member.entity.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
//@Entity
//@Table(name = "ROLE_TABLE")
public class Role {
//    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
//    @Enumerated(value = EnumType.STRING)
//    @Column(name = "role")
    private RoleType roleType;

//    @OneToOne
//    @JoinColumn(name = "member_id")
    private Member member;
}
