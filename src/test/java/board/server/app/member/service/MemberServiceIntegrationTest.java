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


@SpringBootTest
@Transactional
class MemberServiceIntegrationTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;



    @Test
    void join() {
        // GIVEN
        Member member = Member.builder()
                .password("DASdsaas")
                .name("dsadas")
                .email("Dadasd@aaa.aaa")
                .build();


        // WHEN
        // query 3(select 2, insert 1)
        memberService.join(member);



        // THEN
        Member findMember = memberRepository.findById(member.getId()).orElseThrow(() -> new BusinessLogicException(CustomExceptionCode.MEMBER_NOT_FOUND));

        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getRoleType()).isEqualTo(RoleType.MEMBER);
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


        // query 3(select 2, insert 1)
        memberService.join(member);




        // WHEN
        // dirty checking으로 한번에 update
        // query 3 (update1, select 2: validatePresent Username and Email )
        memberService.update(List.of("changePassword", "changeNickname", "changeEmail"), updatedMember, member);




        // THEN
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

        
        
        // query 3(select 2, insert 1)
        memberService.join(member);

        
        // WHEN
        memberService.delete(member.getId());



        // THEN
        try{
            memberRepository.findById(member.getId()).orElseThrow(() -> new RuntimeException(""));
        }catch (RuntimeException e){
            
            return ;
        }


        Assertions.fail("회원 삭제 오류");
    }
}