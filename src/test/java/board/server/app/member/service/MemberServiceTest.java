package board.server.app.member.service;

import board.server.app.enums.RoleType;
import board.server.app.member.entity.Member;
import board.server.app.member.repository.MemberRepository;
import board.server.error.errorcode.CustomExceptionCode;
import board.server.error.exception.BusinessLogicException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    MemberService memberService;
    @Mock
    MemberRepository memberRepository;
    @Mock
    PasswordEncoder passwordEncoder;


    
    
    // 회원가입
    // 성공 케이스: 회원 save -> id, password, RoleType 검증
    @Test
    void join_success() {
        // GIVEN
        // check: len(username) >= 3 and len(password) >= 5
        Member member = Member.builder()
                .name("helloworld")
                .email("helloworld@aaa.aaa")
                .password("helloworld")
                .build();




        Mockito.when(passwordEncoder.encode(anyString())).thenReturn("password");



        // WHEN
        Long id  = memberService.join(member);





        // THEN
        Assertions.assertEquals(id, member.getId());
        Assertions.assertEquals(RoleType.MEMBER, member.getRoleType());
        Assertions.assertEquals("password", member.getPassword());

        Mockito.verify(memberRepository, Mockito.times(1)).save(any(Member.class));
    }

    // 실패 케이스
    // 닉네임 3자 미만일 경우: CustomExceptionCode
    @Test
    void join_fail_nickname(){
        // GIVEN
        Member member = Member.builder()
                .name("aa")
                .email("helloworld@aaa.aaa")
                .password("helloworld")
                .build();



        // WHEN
        // THEN
        try{

            Long id = memberService.join(member);

            Assertions.fail();

        }catch(RuntimeException exception){


            Assertions.assertEquals(exception.getMessage(), CustomExceptionCode.MEMBER_AUTH_NICKNAME.getMessage());


            Mockito.verify(memberRepository, Mockito.never()).save(any());
        }


    }

    // 실패 케이스
    // 패스워드 5자 미만인 경우
    @Test
    void join_fail_password(){
        // GIVEN
        Member member = Member.builder()
                .name("helloworld")
                .email("helloworld@aaa.aaa")
                .password("aaa")
                .build();




        // WHEN
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> {

            memberService.join(member);

        });


        // THEN
        Assertions.assertEquals(exception.getExceptionCode(), CustomExceptionCode.MEMBER_AUTH_PASSWORD);

        Mockito.verify(memberRepository, Mockito.never()).save(any());

    }

    // 실패 케이스
    // 이미 존재하는 닉네임
    @Test
    void join_fail_validate_username(){
        // GIVEN
        Member member = Member.builder()
                .name("helloworld")
                .email("helloworld@aaa.aaa")
                .password("helloworld")
                .build();


        // ifPresent username
        Mockito.when(memberRepository.findByNameOrEmail(anyString(), anyString())).thenReturn(Optional.of(Member.builder().name("helloworld").build()));

        // WHEN
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> {

            memberService.join(member);

        });


        Assertions.assertEquals(exception.getExceptionCode(), CustomExceptionCode.MEMBER_DUPLICATE_NICKNAME);

        Mockito.verify(memberRepository, Mockito.never()).save(any());

    }
    // 실패 케이스
    // 이메일 중복
    @Test
    void join_fail_validate_email(){
        // GIVEN
        Member member = Member.builder()
                .name("helloworld")
                .email("helloworld@aaa.aaa")
                .password("helloworld")
                .build();



        // Ifpresent email
        Mockito.when(memberRepository.findByNameOrEmail(anyString(), anyString())).thenReturn(Optional.of(Member.builder().email("helloworld@aaa.aaa").build()));


        // WHEN
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> memberService.join(member));


        // THEN
        Assertions.assertEquals(exception.getExceptionCode(), CustomExceptionCode.MEMBER_DUPLICATE_EMAIL);

    }




    // =====================================================================
    @Test
    void findId() {
    }

    @Test
    void findPw() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }
}