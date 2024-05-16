package board.server.app.user.controller;

import board.server.app.user.entity.User;
import board.server.app.user.service.UserService;
import board.server.app.user.dto.UserRequestDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/signup")
    public ResponseEntity<Long> createUser(@RequestBody @Valid UserRequestDto reqUser){
        String name = reqUser.getName();
        String email = reqUser.getEmail();
        String password = passwordEncoder.encode(reqUser.getPassword());

        User user = new User(name, email, password);

        Long id = userService.join(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }
}
