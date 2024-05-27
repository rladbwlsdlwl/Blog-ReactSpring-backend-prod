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

    public Long join(Member member){
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

    private void validateDuplicateMember(Member member) {
        memberRepository.findByEmail(member.getEmail()).ifPresent(e -> {
            throw new BusinessLogicException(CustomExceptionCode.MEMBER_DUPLICATE_EMAIL);
        });
        memberRepository.findByName(member.getName()).ifPresent(e -> {
            throw new BusinessLogicException(CustomExceptionCode.MEMBER_DUPLICATE_NICKNAME);
        });
    }
}
