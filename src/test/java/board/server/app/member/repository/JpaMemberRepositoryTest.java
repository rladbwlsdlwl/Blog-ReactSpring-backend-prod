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

        Role role = Role.builder()
                .roleType(RoleType.MEMBER)
                .member(member)
                .build();


        // insert query 2, 객체 영속화
        memberRepository.save(member);
        roleRepository.save(role);


        em.flush();
        em.clear();


        // WHEN
        // query 1
        Member findMember = memberRepository.findById(member.getId()).orElseThrow(() -> new BusinessLogicException(CustomExceptionCode.MEMBER_NOT_FOUND));


        // THEN
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getRole().getRoleType().toString()).isEqualTo("MEMBER");
    }

    @Test
    void findByNameWithRole(){
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

        // query 2
        memberRepository.save(member);
        roleRepository.save(role);


        em.flush();
        em.clear();


        // WHEN
        // query 1 (JPQL 로 선언한 fetch join)
        Member findMember = memberRepository.findByNameWithRole(member.getName()).orElseThrow(() -> new BusinessLogicException(CustomExceptionCode.MEMBER_NOT_FOUND));

        Assertions.assertThat(findMember.getRole().getId()).isEqualTo(role.getId());

    }

    /* JPA TEST */
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

        // query 1
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
        Role role = Role.builder()
                .member(member)
                .roleType(RoleType.MEMBER)
                .build();

        // member.setRole(role);

        // insert query 2, 객체 영속화
        memberRepository.save(member);
        roleRepository.save(role);



        // WHEN
        Role followRole = member.getRole();
        // query 1 - update
        followRole.setRoleType(RoleType.ADMIN); // 일반 필드 수정 가능 (연관관계 주인 참조)

        em.flush();
        em.clear();



        // THEN
        // 필드 수정여부 확인
        // query 1
        Role findRole = roleRepository.findById(role.getId()).orElseThrow(() -> new BusinessLogicException(CustomExceptionCode.MEMBER_NOT_FOUND));
        Assertions.assertThat(findRole.getRoleType().toString()).isEqualTo("ADMIN");


        // => 변경 감지로 값이 변함
    }

    @Test
    void 양방향엔티티_연관관계참조_외래키필드수정(){
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
        // member.setRole(role);


        // insert query 2
        memberRepository.save(member);
        roleRepository.save(role);

        
        
        
        // 새로운 회원 
        Member updateMember = Member.builder()
                .name("helloa")
                .email("sssssssa")
                .password("zzzzzzzzzza")
                .build();
        // query 1
        memberRepository.save(updateMember);
        
        

        Long id1 = member.getId();
        Long id2 = updateMember.getId();

        Long roleId1 = member.getRole().getId();

        em.flush();
        em.clear();

        
        
        // WHEN
        // query 2
        Member m1 = memberRepository.findById(id1).orElseThrow(() -> new BusinessLogicException(CustomExceptionCode.MEMBER_NOT_FOUND));;
        Role findRole = m1.getRole();
        findRole.setMember(updateMember);


        Assertions.assertThat(findRole.getMember().getId()).isEqualTo(id2);


        em.flush();
        em.clear();

        // THEN
        // 필드 수정여부 확인
        Role roleSaved = roleRepository.findById(roleId1).orElseThrow(() -> new BusinessLogicException(CustomExceptionCode.MEMBER_NOT_FOUND));


        // 값이 변함
        Assertions.assertThat(roleSaved.getMember().getId()).isEqualTo(id2);

        
        // => 변경 감지로 값이 변함, 연관관계 주인에서 값을 변경한 것과 동일한 결과
    }

    @Test
    void 양뱡향엔티티_연관관계주인이아님_외래키수정(){
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
        // member.setRole(role);


        // query 2
        memberRepository.save(member);
        roleRepository.save(role);


        em.flush();
        em.clear();

        // WHEN
        // 연관관계 주인이 아닌 쪽에서 외래키를 가진 객체 수정 시도
        // query 1
        Member findMember = memberRepository.findById(member.getId()).orElseThrow(() -> new BusinessLogicException(CustomExceptionCode.MEMBER_NOT_FOUND));

        // query 0
        findMember.setRole(null);


        // THEN
        Assertions.assertThat(findMember.getRole()).isNull();


        // => 연관관계 필드는 직접 수정할 수 없음
    }

    @Test
    void 비영속객체쿼리테스트(){
        // GIVEN
        Member m = Member.builder()
                .email("dsad")
                .name("dsad")
                .password("sadasdasd")
                .build();
        Role role = Role.builder()
                .member(m)
                .roleType(RoleType.MEMBER)
                .build();

        memberRepository.save(m);
        roleRepository.save(role);


        // m r 비영속화
        em.flush();
        em.clear();



        // WHEN query - 2
        Member member = memberRepository.findById(m.getId()).orElseThrow(() -> new BusinessLogicException(CustomExceptionCode.MEMBER_NOT_FOUND));

        // THEN query - 1
        Assertions.assertThat(member.getRole().getRoleType().toString()).isEqualTo("MEMBER");

    }
}