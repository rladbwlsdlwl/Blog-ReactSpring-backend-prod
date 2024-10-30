package board.server.app.member.service;

import board.server.app.member.dto.request.MemberRequestUpdateDto;
import board.server.app.member.entity.Member;
import board.server.app.member.repository.JdbcTemplateMemberRepository;
import board.server.app.member.repository.MemberRepository;
import board.server.error.errorcode.CustomExceptionCode;
import board.server.error.exception.BusinessLogicException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public MemberService(JdbcTemplateMemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 회원가입
    public Long join(Member member){
        checkAvailableNickname(member.getName());
        checkAvailablePassword(member.getPassword());
        validateDuplicateUsername(member.getName());
        validateDuplicateEmail(member.getEmail());

        String name = member.getName(), email = member.getEmail(), password = member.getPassword();
        password = passwordEncoder.encode(password);

        Member member1 = Member.builder()
                .name(name)
                .password(password)
                .email(email)
                .build();

        return memberRepository.save(member1).getId();
    }

    public void update(List<String> mode, MemberRequestUpdateDto memberRequestUpdateDto, Member member) {
        if(mode.contains("changeNickname"))
            updateUsername(member, memberRequestUpdateDto.getName());
        if(mode.contains("changePassword")){
            if(member.getPassword() == null) // 패스워드를 처음 설정하는 회원 - 소셜로그인 회원
                updatePassword(member, memberRequestUpdateDto.getPassword());
            else    
                updatePassword(member, memberRequestUpdateDto.getOriginalPassword(), memberRequestUpdateDto.getPassword());
        }
        if(mode.contains("changeEmail"))
            updateEmail(member, memberRequestUpdateDto.getEmail());
    }

    // 계정 삭제
    public void delete(Long id){
        memberRepository.deleteById(id);
    }

    // 닉네임 변경
    private void updateUsername(Member member, String newUsername){
        checkAvailableNickname(newUsername);
        checkDuplicateUsername(member.getName(), newUsername);
        validateDuplicateUsername(newUsername);

        Member member1 = Member.builder()
                .id(member.getId())
                .name(newUsername)
                .password(member.getPassword())
                .email(member.getEmail())
                .build();

        memberRepository.update(member1);

        member.setName(newUsername);
    }

    // 비밀번호 변경
    private void updatePassword(Member member, String newPassword) {
        // 비밀번호 최초 설정
        checkAvailablePassword(newPassword);

        String pwd = passwordEncoder.encode(newPassword);
        Member member1 = Member.builder()
                .id(member.getId())
                .name(member.getName())
                .password(pwd)
                .email(member.getEmail())
                .build();

        memberRepository.update(member1);

        member.setPassword(pwd);
    }
    private void updatePassword(Member member, String currPassword, String newPassword){
        // 기존 암호 매칭 확인
        // 기존 암호와 동일한 암호로 변경할 수 없음
        checkAvailablePassword(newPassword);
        checkAvailablePassword(currPassword);
        checkOriginalPassword(member.getPassword(), currPassword);
        checkDuplicatePassword(member.getPassword(), newPassword);

        String pwd = passwordEncoder.encode(newPassword);
        Member member1 = Member.builder()
                .id(member.getId())
                .name(member.getName())
                .password(pwd)
                .email(member.getEmail())
                .build();

        memberRepository.update(member1);

        member.setPassword(pwd);
    }

    // 이메일 변경
    private void updateEmail(Member member, String email){
        checkAvailableEmail(email);
        checkDuplicateEmail(member.getEmail(), email);
        validateDuplicateEmail(email);

        Member member1 = Member.builder()
                .id(member.getId())
                .name(member.getName())
                .password(member.getPassword())
                .email(email)
                .build();

        memberRepository.update(member1);

        member.setEmail(email);
    }

    // RequestDto 값 확인
    private void checkAvailableNickname(String username) {
        if(username == null || username.length() < 3)
            throw new BusinessLogicException(CustomExceptionCode.MEMBER_AUTH_NICKNAME);
    }

    private void checkAvailablePassword(String password) {
        if(password == null || password.length() < 5){
            throw new BusinessLogicException(CustomExceptionCode.MEMBER_AUTH_PASSWORD);
        }
    }

    private void checkAvailableEmail(String email) {
        if(email == null || email.length() < 9 || !email.matches("(.*)@(.*)"))
            throw new BusinessLogicException(CustomExceptionCode.MEMBER_AUTH_EMAIL);
    }

    // 암호 변경 전 기존 암호 일치여부 확인
    private void checkOriginalPassword(String password, String currPassword) {
        if(!passwordEncoder.matches(currPassword, password))
            throw new BusinessLogicException(CustomExceptionCode.MEMBER_NO_MATCH_PASSWORD);
    }

    // 기존 정보와 동일한 값으로 변경할 수 없음
    private void checkDuplicateUsername(String username, String newUsername) {
        if(username.equals(newUsername))
            throw new BusinessLogicException(CustomExceptionCode.MEMBER_DO_NOT_USE_NICKNAME);
    }

    private void checkDuplicatePassword(String password, String newPassword) {
        if(passwordEncoder.matches(newPassword, password))
            throw new BusinessLogicException(CustomExceptionCode.MEMBER_DO_NOT_USE_PASSWORD);
    }

    private void checkDuplicateEmail(String email, String newEmail) {
        if(email.equals(newEmail))
            throw new BusinessLogicException(CustomExceptionCode.MEMBER_DO_NOT_USE_EMAIL);
    }

    // 중복 확인 - Integrity Constraint
    private void validateDuplicateUsername(String username) {
        memberRepository.findByName(username).ifPresent((e) -> {
            throw new BusinessLogicException(CustomExceptionCode.MEMBER_DUPLICATE_NICKNAME);
        });
    }

    private void validateDuplicateEmail(String email) {
        memberRepository.findByEmail(email).ifPresent(e -> {
            throw new BusinessLogicException(CustomExceptionCode.MEMBER_DUPLICATE_EMAIL);
        });
    }
}