package board.server.app.likes.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;


@Transactional
@SpringBootTest
class JdbcTemplateLikesRepositoryTest {

    @Autowired
    LikesRepository likesRepository;


    @Test
    void 해당되는값이없을때반환되는값은() {
        // given when
        int size = likesRepository.findByAuthorAndPostId(3L, 5L).size();

        //then

        Assertions.assertEquals(0, size, "error!!");
    }
}