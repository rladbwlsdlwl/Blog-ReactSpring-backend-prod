package board.server.app.member.repository;

import board.server.app.enums.RoleType;
import board.server.app.member.entity.Member;
import board.server.app.role.entity.Role;
import board.server.app.role.repository.RoleRepository;
import board.server.error.errorcode.CustomExceptionCode;
import board.server.error.exception.BusinessLogicException;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class JpaMemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private EntityManager em;

    @Test
    void save(){
        // GIVEN
        Member member = Member.builder()
                .name("hello")
                .email("sssssss")
                .password("zzzzzzzzzz")
                .build();

        // insert query 2, 객체 영속화
        Member saved = memberRepository.save(member);

        // WHEN
        // query 0
        Member m = memberRepository.findById(saved.getId()).orElseThrow(() -> new BusinessLogicException(CustomExceptionCode.MEMBER_NOT_FOUND));

        // THEN
        Assertions.assertThat(m.getId()).isEqualTo(saved.getId());
        Assertions.assertThat(m.getRole().getRoleType().toString()).isEqualTo("MEMBER");
    }

    @Test
    void 양방향_저장(){
        // GIVEN
        Member member = Member.builder()
                .name("hello")
                .email("sssssss")
                .password("zzzzzzzzzz")
                .build();

        Role role = Role.builder()
                .roleType(RoleType.MEMBER)
                .member(member)
                .build();

        em.persist(role);
        em.persist(member);

        em.flush();
        em.clear();

        Member findMember = memberRepository.findById(role.getMember().getId()).orElseThrow(() -> new BusinessLogicException(CustomExceptionCode.MEMBER_NOT_FOUND));

        Assertions.assertThat(findMember.getRole().getRoleType().toString()).isEqualTo("MEMBER");
        
        // => 저장 순서는 상관없음
    }

    @Test
    void 양방향엔티티_연관관계참조_일반필드수정(){
        // GIVEN
        Member member = Member.builder()
                .name("hello")
                .email("sssssss")
                .password("zzzzzzzzzz")
                .build();

        // insert query 2, 객체 영속화
        Member saved = memberRepository.save(member);

        // WHEN
        // query 0
        Role role = saved.getRole();
        // query 1 - update
        role.setRoleType(RoleType.ADMIN); // 일반 필드 수정 가능 (연관관계 주인 참조)

        em.flush();
        em.clear();

        // THEN
        // 필드 수정여부 확인
        Role findRole = roleRepository.findById(role.getId()).orElseThrow(() -> new BusinessLogicException(CustomExceptionCode.MEMBER_NOT_FOUND));
        Assertions.assertThat(findRole.getRoleType().toString()).isEqualTo("ADMIN");


        // => 변경 감지로 값은 변하지만 외래키를 갖고 있지 않은 연관관계 주인이 아닌 엔티티는 읽기 전용 권장
    }

    @Test
    void 양방향엔티티_연관관계참조_외래키필드수정(){
        // GIVEN
        Member member = Member.builder()
                .name("hello")
                .email("sssssss")
                .password("zzzzzzzzzz")
                .build();


        Member updateMember = Member.builder()
                .name("helloa")
                .email("sssssssa")
                .password("zzzzzzzzzza")
                .build();

        // insert query 3, 객체 영속화
        Member saved = memberRepository.save(member);
        em.persist(updateMember);

        Long id1 = saved.getId();
        Long id2 = updateMember.getId();

        Long roleId1 = saved.getRole().getId();

        em.flush();
        em.clear();

        // WHEN
        // query 2
        Member m1 = memberRepository.findById(id1).orElseThrow(() -> new BusinessLogicException(CustomExceptionCode.MEMBER_NOT_FOUND));;
        Role role = m1.getRole();
        role.setMember(updateMember);


        Assertions.assertThat(role.getMember().getId()).isEqualTo(id2);


        em.flush();
        em.clear();

        // THEN
        // 필드 수정여부 확인
        Role roleSaved = roleRepository.findById(roleId1).orElseThrow(() -> new BusinessLogicException(CustomExceptionCode.MEMBER_NOT_FOUND));


        // 값이 변함
        Assertions.assertThat(roleSaved.getMember().getId()).isEqualTo(id2);

        
        // => 변경 감지로 값은 변하지만 외래키를 갖고 있지 않은 연관관계 주인이 아닌 엔티티는 읽기 전용 권장
    }

    @Test
    void 비영속객체쿼리테스트(){
        // GIVEN
        Member m = Member.builder()
                .email("dsad")
                .name("dsad")
                .password("sadasdasd")
                .build();

        memberRepository.save(m);

        // m 비영속화
        em.flush();
        em.clear();



        // WHEN query - 1
        Member member = memberRepository.findById(m.getId()).orElseThrow(() -> new BusinessLogicException(CustomExceptionCode.MEMBER_NOT_FOUND));

        // THEN query - 1
        Assertions.assertThat(member.getRole().getRoleType().toString()).isEqualTo("MEMBER");

    }
}