package board.server.app.user.service;

import board.server.app.user.entity.User;
import board.server.app.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    UserService userService;


    @Test
    public void 중복회원_예외() {
        // GIVEN
        User user1 = new User("yujin", "yujin@aaa.aaa", "aaa");
        User user2 = new User("yujin1", "yujin@aaa.aaa", "aaa");

        // WHEN
        userService.join(user1);

        // THEN
        IllegalStateException err = assertThrows(IllegalStateException.class, () -> userService.join(user2));
    }


}