package board.server.app.role.entity;

import board.server.app.enums.RoleType;
import board.server.app.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
//@AllArgsConstructor
@Entity
@Table(name = "role_table")
public class Role {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "role")
    private RoleType roleType;

    @OneToOne(mappedBy = "role", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private Member member;


    @Builder
    public Role(Long id, RoleType roleType, Member member){
        this.id = id;
        this.roleType = roleType;
        this.member = member;
    }
}
