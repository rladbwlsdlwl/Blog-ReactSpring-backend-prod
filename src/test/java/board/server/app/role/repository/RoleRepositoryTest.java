package board.server.app.role.repository;

import board.server.app.enums.RoleType;
import board.server.app.member.entity.Member;
import board.server.app.member.repository.MemberRepository;
import board.server.app.role.entity.Role;
import board.server.error.errorcode.CustomExceptionCode;
import board.server.error.exception.BusinessLogicException;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private EntityManager em;

    Member member = null;
    Role role = null;


    @BeforeEach
    void init(){
        // GIVEN
        this.role = Role.builder()
                .roleType(RoleType.MEMBER)
                .build();
        this.member = Member.builder()
                .name("aaaaa")
                .email("aaaaaa@aaa.aaa")
                .password("aaaaa")
                .role(role)
                .build();


        roleRepository.save(role);
        memberRepository.save(member);

        em.flush();
        em.clear();

    }

    @Test
    void save() {

        // THEN
        // join query
        Member findMember = memberRepository.findByNameWithRole(member.getName()).orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다"));

        // no query
        Assertions.assertThat(member.getId()).isEqualTo(findMember.getId());
        Assertions.assertThat(role.getId()).isEqualTo(findMember.getRole().getId());


    }

    @Test
    void findById() {
    }

    @Test
    void delete() {


        try{
            // WHEN
            // query order
            // delete member
            // delete role
            roleRepository.delete(
                    roleRepository.findById(role.getId()) // eager loading (join)
                    .orElseThrow(() -> new RuntimeException("권한이 존재하지 않음"))
            );

            em.flush();
            em.clear();


            // THEN
            memberRepository.findById(member.getId()).ifPresent(m -> new RuntimeException("회원이 삭제되지 않음"));
            roleRepository.findById(role.getId()).ifPresent(m -> new RuntimeException("권한이 삭제되지 않음"));


        }catch (RuntimeException e){
            Assertions.assertThatException();
        }



    }
}