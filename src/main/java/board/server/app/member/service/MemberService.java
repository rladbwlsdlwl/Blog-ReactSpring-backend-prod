package board.server.app.member.service;

import board.server.app.member.entity.Member;
import board.server.app.member.repository.JdbcTemplateMemberRepository;
import board.server.app.member.repository.MemberRepository;
import board.server.error.errorcode.CustomExceptionCode;
import board.server.error.exception.BusinessLogicException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        validateDuplicateMember(member);

        String name = member.getName(), email = member.getEmail(), password = member.getPassword();
        password = passwordEncoder.encode(password);

        Member member1 = Member.builder()
                .name(name)
                .password(password)
                .email(email)
                .build();

        return memberRepository.save(member1).getId();
    }

    // 닉네임 변경
    public void updateUsername(Member member, String newUsername){
        checkAvailableNickname(newUsername);
        checkDuplicateUsername(member.getName(), newUsername);
        validateDuplicateUsername(newUsername);

        Member member1 = Member.builder()
                .id(member.getId())
                .name(newUsername)
                .password(member.getPassword())
                .build();

        memberRepository.update(member1);
    }

    // 비밀번호 변경
    public void updatePassword(Member member, String currPassword, String newPassword){
        // 기존 암호 매칭 확인
        // 기존 암호와 동일한 암호로 변경할 수 없음
        checkAvailablePassword(newPassword);
        checkOriginalPassword(member.getPassword(), currPassword);
        checkDuplicatePassword(member.getPassword(), newPassword);

        String pwd = passwordEncoder.encode(newPassword);
        Member member1 = Member.builder()
                .id(member.getId())
                .name(member.getName())
                .password(pwd)
                .build();

        memberRepository.update(member1);
    }

    // 계정 삭제
    public void delete(Long id){
        memberRepository.deleteById(id);
    }

    private void checkAvailablePassword(String password) {
        if(password == null || password.length() < 5){
            throw new BusinessLogicException(CustomExceptionCode.MEMBER_AUTH_PASSWORD);
        }
    }

    private void checkAvailableNickname(String username) {
        if(username == null || username.length() < 3)
            throw new BusinessLogicException(CustomExceptionCode.MEMBER_AUTH_NICKNAME);
    }

    private void checkDuplicateUsername(String username, String newUsername) {
        if(username.equals(newUsername))
            throw new BusinessLogicException(CustomExceptionCode.MEMBER_DUPLICATE_NICKNAME);
    }

    private void checkOriginalPassword(String password, String currPassword) {
        if(!passwordEncoder.matches(currPassword, password))
            throw new BusinessLogicException(CustomExceptionCode.MEMBER_NO_PERMISSION);
    }

    private void checkDuplicatePassword(String password, String newPassword) {
        if(passwordEncoder.matches(newPassword, password))
            throw new BusinessLogicException(CustomExceptionCode.MEMBER_DUPLICATE_PASSWORD);
    }

    private void validateDuplicateUsername(String username) {
        memberRepository.findByName(username).ifPresent((e) -> {
            throw new BusinessLogicException(CustomExceptionCode.MEMBER_DUPLICATE_NICKNAME);
        });
    }

    private void validateDuplicateMember(Member member) {
        memberRepository.findByEmail(member.getEmail()).ifPresent(e -> {
            throw new BusinessLogicException(CustomExceptionCode.MEMBER_DUPLICATE_EMAIL);
        });
        memberRepository.findByName(member.getName()).ifPresent(e -> {
            throw new BusinessLogicException(CustomExceptionCode.MEMBER_DUPLICATE_NICKNAME);
        });
    }
}