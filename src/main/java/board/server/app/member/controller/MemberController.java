package board.server.app.member.controller;

import board.server.app.member.entity.Member;
import board.server.app.member.service.MemberService;
import board.server.app.member.dto.MemberRequestDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class MemberController {
    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Long> createMember(@RequestBody @Valid MemberRequestDto memberRequestDto){
        String name = memberRequestDto.getName();
        String email = memberRequestDto.getEmail();
        String password = memberRequestDto.getPassword();

        Member member = new Member(name, email, password);

        Long id = memberService.join(member);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }
}
