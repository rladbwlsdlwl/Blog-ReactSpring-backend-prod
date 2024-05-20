package board.server.app.user.service;

import board.server.app.user.entity.User;
import board.server.app.user.repository.JdbcTemplateUserRepository;
import board.server.app.user.repository.UserRepository;
import board.server.error.errorcode.CustomExceptionCode;
import board.server.error.exception.BusinessLogicException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(JdbcTemplateUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Long join(User user){
       validateDuplicateUser(user);

       String name = user.getUsername(), email = user.getEmail(), password = user.getPassword();
       password = passwordEncoder.encode(password);

       User user1 = new User(name, email, password);

       return userRepository.save(user1).getId();
    }

    private void validateDuplicateUser(User user) {
        userRepository.findByEmail(user.getEmail()).ifPresent(e -> {
            throw new BusinessLogicException(CustomExceptionCode.USER_DUPLICATE_EMAIL);
        });
        userRepository.findByUsername(user.getUsername()).ifPresent(e -> {
            throw new BusinessLogicException(CustomExceptionCode.USER_DUPLICATE_NICKNAME);
        });
    }
}
