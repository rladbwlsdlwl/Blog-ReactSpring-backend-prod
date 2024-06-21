package board.server.app.member.controller;

import board.server.app.member.dto.request.MemberRequestUpdateDto;
import board.server.app.member.dto.response.CustomMemberResponseDto;
import board.server.app.member.entity.Member;
import board.server.app.member.service.MemberService;
import board.server.app.member.dto.request.MemberRequestRegisterDto;
import board.server.config.jwt.CustomUserDetail;
import board.server.config.jwt.JwtTokenProvider;
import board.server.error.errorcode.CustomExceptionCode;
import board.server.error.exception.BusinessLogicException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class MemberController {
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public MemberController(MemberService memberService, JwtTokenProvider jwtTokenProvider) {
        this.memberService = memberService;
        this.jwtTokenProvider = jwtTokenProvider;
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
    public ResponseEntity<Object> updateMember(@AuthenticationPrincipal CustomUserDetail userDetails,
                                               @PathVariable String username,
                                               @RequestParam(name = "mode") String mode,
                                               @RequestBody @Valid MemberRequestUpdateDto memberRequestUpdateDto){

        Member originMember = Member.builder()
                .id(userDetails.getId())
                .name(userDetails.getUsername())
                .password(userDetails.getPassword())
                .build();

        String tokenUsername = "";
        if(mode.equals("changeNickname")){
            String name = memberRequestUpdateDto.getName();
            memberService.updateUsername(originMember, name);
            tokenUsername = name;
        }else if(mode.equals("changePassword")){
            String password = memberRequestUpdateDto.getPassword();
            memberService.updatePassword(originMember, password);
            tokenUsername = originMember.getName();
        }else{
            throw new BusinessLogicException(CustomExceptionCode.MEMBER_NO_PERMISSION);
        }

        // header에 새 토큰 발급한거 넣기
        String token = jwtTokenProvider.generateToken(new CustomMemberResponseDto(tokenUsername));
        return ResponseEntity.status(HttpStatus.OK)
                .header("Authentication", "barear " + token)
                .build();
    }
}
