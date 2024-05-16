package board.server.app.user.service;

import board.server.app.user.entity.User;
import board.server.app.user.repository.JdbcTemplateUserRepository;
import board.server.app.user.repository.UserRepository;
import board.server.error.errorcode.CustomExceptionCode;
import board.server.error.exception.BusinessLogicException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(JdbcTemplateUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Long join(User user){
       validateDuplicateUser(user);
       return userRepository.save(user).getId();
    }

    private void validateDuplicateUser(User user) {
        userRepository.findByEmail(user.getEmail()).ifPresent(e -> {
            throw new BusinessLogicException(CustomExceptionCode.USER_DUPLICATE);
        });
    }
}
