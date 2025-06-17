package board.server.app.member.service;

import board.server.app.enums.RoleType;
import board.server.app.member.dto.request.MemberRequestUpdateDto;
import board.server.app.member.entity.Member;
import board.server.app.member.repository.JdbcTemplateMemberRepository;
import board.server.app.member.repository.MemberRepository;
import board.server.app.role.entity.Role;
import board.server.app.role.repository.RoleRepository;
import board.server.error.errorcode.CustomExceptionCode;
import board.server.error.exception.BusinessLogicException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;

@Service
@Transactional
@Slf4j
public class MemberService {
    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public MemberService(MemberRepository memberRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 회원가입
    public Long join(Member member){
        checkAvailableNickname(member.getName());
        checkAvailablePassword(member.getPassword());
        validateDuplicateUsername(member.getName());
        validateDuplicateEmail(member.getEmail());

        // 패스워드 암호화
        member.setPassword(passwordEncoder.encode(member.getPassword()));

        Role role = Role.builder()
                .roleType(RoleType.MEMBER)
                .member(member)
                .build();
        member.setRole(role);

        roleRepository.save(role);
        memberRepository.save(member);

        return member.getId();
    }

    // 회원 찾기 - 아이디
    public String findId(String email){
        checkAvailableEmail(email);

        // 이메일 확인
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new BusinessLogicException(CustomExceptionCode.MEMBER_NOT_FOUND));

        // 아이디 반환
        return member.getName();
    }

    // 회원 찾지 - 패스워드
    public String findPw(String name){
        checkAvailableNickname(name);

        // 닉네임 확인
        Member member = memberRepository.findByName(name).orElseThrow(() -> new BusinessLogicException(CustomExceptionCode.MEMBER_NOT_FOUND));

        // 패스워드 랜덤 발급
        String pwd = generatePassword();

        // 패스워드 초기화
        updatePassword(member, pwd);

        return pwd;
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
//        memberRepository.deleteById(id);

        // Role 삭제
        roleRepository.delete(
                memberRepository.findByIdWithRole(id)
                .map(Member::getRole)
                .orElseThrow(() -> new BusinessLogicException(CustomExceptionCode.MEMBER_NOT_FOUND))
        );
    }

    // 닉네임 변경
    private void updateUsername(Member member, String newUsername){
        checkAvailableNickname(newUsername);
        checkDuplicateUsername(member.getName(), newUsername);
        validateDuplicateUsername(newUsername);

        // 영속 컨택스트 update
        Member findMember = memberRepository.findById(member.getId()).orElseThrow(() -> new BusinessLogicException(CustomExceptionCode.MEMBER_NOT_FOUND));
        findMember.setName(newUsername);
        member.setName(newUsername); // 비영속 객체, 토큰 발급으로 인한 닉네임 세팅
    }

    // 비밀번호 변경
    private void updatePassword(Member member, String newPassword) {
        // 비밀번호 최초 설정
        checkAvailablePassword(newPassword);

        String pwd = passwordEncoder.encode(newPassword);

        // 양속 컨택스트 update
        Member findMember = memberRepository.findById(member.getId()).orElseThrow(() -> new BusinessLogicException(CustomExceptionCode.MEMBER_NOT_FOUND));
        findMember.setPassword(pwd);
    }
    private void updatePassword(Member member, String currPassword, String newPassword){
        // 기존 암호 매칭 확인
        // 기존 암호와 동일한 암호로 변경할 수 없음
        checkAvailablePassword(newPassword);
        checkAvailablePassword(currPassword);
        checkOriginalPassword(member.getPassword(), currPassword);
        checkDuplicatePassword(member.getPassword(), newPassword);

        String pwd = passwordEncoder.encode(newPassword);

        // 양속 컨택스트 update
        Member findMember = memberRepository.findById(member.getId()).orElseThrow(() -> new BusinessLogicException(CustomExceptionCode.MEMBER_NOT_FOUND));
        findMember.setPassword(pwd);
    }

    // 이메일 변경
    private void updateEmail(Member member, String email){
        checkAvailableEmail(email);
        checkDuplicateEmail(member.getEmail(), email);
        validateDuplicateEmail(email);

        // 양속 컨택스트 update
        Member findMember = memberRepository.findById(member.getId()).orElseThrow(() -> new BusinessLogicException(CustomExceptionCode.MEMBER_NOT_FOUND));
        findMember.setEmail(email);
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

    // 난수를 활용한 패스워드 발급
    private String generatePassword(){
        final int SIZE = 10;
        final String CHAR_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        final SecureRandom random = new SecureRandom();

        StringBuilder pwd = new StringBuilder();
        for (int i =0; i<SIZE; i++){
            int rd = random.nextInt(CHAR_SET.length());

            pwd.append(CHAR_SET.charAt(rd));
        }

        log.info(pwd.toString());
        return pwd.toString();
    }
}