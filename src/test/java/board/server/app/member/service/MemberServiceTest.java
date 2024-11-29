package board.server.app.member.service;

import board.server.app.enums.RoleType;
import board.server.app.member.dto.request.MemberRequestUpdateDto;
import board.server.app.member.entity.Member;
import board.server.app.member.repository.MemberRepository;
import board.server.error.errorcode.CustomExceptionCode;
import board.server.error.exception.BusinessLogicException;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private EntityManager em;


    @Test
    void join() {
        // GIVEN
        Member member = Member.builder()
                .password("DASdsaas")
                .name("dsadas")
                .email("Dadasd@aaa.aaa")
                .build();


        // WHEN
        // query 4(select 2, insert 2)
        memberService.join(member);


        em.flush();
        em.clear();


        // THEN
        Member findMember = memberRepository.findById(member.getId()).orElseThrow(() -> new BusinessLogicException(CustomExceptionCode.MEMBER_NOT_FOUND));

        Assertions.assertThat(findMember.getRole().getRoleType()).isEqualTo(RoleType.MEMBER);
    }

    @Test
    void update() {
        // GIVEN
        Member member = Member.builder()
                .password("aaaaa")
                .name("dsadas")
                .email("Dadasd@aaa.aaa")
                .build();
        MemberRequestUpdateDto updatedMember = new MemberRequestUpdateDto("zzzzz", "user@aaa.aaaa", "aaaaa", "DSasdas");


        // query 4(select 2, insert 2)
        memberService.join(member);

        em.flush();
        em.clear();



        // WHEN
        // query 6(insert1, select 3)
        memberService.update(List.of("changePassword", "changeNickname", "changeEmail"), updatedMember, member);



        em.flush();
        em.clear();


        // THEN
        // query 1(join role)
        Member findMember = memberRepository.findById(member.getId()).orElseThrow(() -> new BusinessLogicException(CustomExceptionCode.MEMBER_NOT_FOUND));

        Assertions.assertThat(findMember.getName()).isEqualTo(updatedMember.getName());
        Assertions.assertThat(findMember.getEmail()).isEqualTo(updatedMember.getEmail());

    }

    @Test
    void delete() {
        // GIVEN
        Member member = Member.builder()
                .password("aaaaa")
                .name("dsadas")
                .email("Dadasd@aaa.aaa")
                .build();

        
        
        // query 4(select 2, insert 2)
        memberService.join(member);

        em.flush();
        em.clear();
        
        
        // WHEN
        // query 1
        memberService.delete(member.getId());


        em.flush();
        em.clear();

        // THEN
        // query 1
        try{
            memberRepository.findById(member.getId()).orElseThrow(() -> new RuntimeException(""));
        }catch (RuntimeException e){
            
            return ;
        }


        Assertions.fail("회원 삭제 오류");
    }
}