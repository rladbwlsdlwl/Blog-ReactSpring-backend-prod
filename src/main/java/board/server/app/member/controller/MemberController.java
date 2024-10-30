package board.server.app.member.controller;

import board.server.app.member.dto.request.MemberRequestUpdateDto;
import board.server.app.member.dto.response.CustomMemberResponseDto;
import board.server.app.member.dto.response.MemberResponseMeDto;
import board.server.app.member.entity.Member;
import board.server.app.member.service.MemberService;
import board.server.app.member.dto.request.MemberRequestRegisterDto;
import board.server.config.jwt.CustomUserDetail;
import board.server.config.jwt.JwtTokenBlacklist;
import board.server.config.jwt.JwtTokenProvider;
import board.server.error.errorcode.CommonExceptionCode;
import board.server.error.errorcode.CustomExceptionCode;
import board.server.error.exception.BusinessLogicException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api")
@Slf4j
public class MemberController {
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenBlacklist jwtTokenBlacklist;

    @Autowired
    public MemberController(MemberService memberService, JwtTokenProvider jwtTokenProvider, JwtTokenBlacklist jwtTokenBlacklist) {
        this.memberService = memberService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtTokenBlacklist = jwtTokenBlacklist;
    }

    @GetMapping("/auth/me")
    public ResponseEntity<Object> sendMemberInfo(@AuthenticationPrincipal CustomUserDetail userDetail) {
        Long id = userDetail.getId();
        String email = userDetail.getEmail();
        String username = userDetail.getUsername();
        boolean isNotSettingPassword = userDetail.getPassword() == null;

        MemberResponseMeDto memberResponseMeDto = MemberResponseMeDto.builder()
                .id(id)
                .email(email)
                .username(username)
                .isNotSettingPassword(isNotSettingPassword)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(memberResponseMeDto);
    }

    @GetMapping("/logout")
    public ResponseEntity<Object> logoutMember(@AuthenticationPrincipal CustomUserDetail userDetail,
                                               @RequestHeader("Authentication") String token){
        SecurityContextHolder.clearContext();

        token = token.split(" ")[1];
        jwtTokenBlacklist.addBlacklist(token);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @PostMapping("/signup")
    public ResponseEntity<Long> createMember(@RequestBody @Valid MemberRequestRegisterDto memberRequestRegisterDto){
        String name = memberRequestRegisterDto.getName();
        String email = memberRequestRegisterDto.getEmail();
        String password = memberRequestRegisterDto.getPassword();

        Member member = new Member(name, email, password);

        Long id = memberService.join(member);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @PatchMapping("/{username}/setting")
    public ResponseEntity<Object> updateMember(@PathVariable String username,
                                               @RequestParam(name = "mode", required = false) List<String> mode,
                                               @RequestBody @Valid MemberRequestUpdateDto memberRequestUpdateDto,
                                               @AuthenticationPrincipal CustomUserDetail userDetails,
                                               @RequestHeader("Authentication") String token){

        if(mode == null) throw new BusinessLogicException(CustomExceptionCode.MEMBER_NO_PERMISSION);

        Member member = Member.builder()
                .id(userDetails.getId())
                .name(userDetails.getUsername())
                .password(userDetails.getPassword())
                .email(userDetails.getEmail())
                .build();

        // 계정정보 업데이트
        memberService.update(mode, memberRequestUpdateDto, member);


        // 기존 토큰 블랙리스트 추가
        token = token.split(" ")[1];
        jwtTokenBlacklist.addBlacklist(token);

        // header에 새 토큰 발급한거 넣기
        String newToken = jwtTokenProvider.generateToken(new CustomMemberResponseDto(member.getName()));
        return ResponseEntity.status(HttpStatus.OK)
                .header("Authentication", "bearer " + newToken)
                .build();
    }

    @DeleteMapping("/{username}/setting")
    public ResponseEntity<Object> deleteMember(@AuthenticationPrincipal CustomUserDetail userDetail,
                                               @PathVariable String username){
        memberService.delete(userDetail.getId());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
