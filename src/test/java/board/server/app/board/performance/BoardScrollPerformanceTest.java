package board.server.app.board.performance;

import board.server.app.board.entity.Board;
import board.server.app.board.repository.BoardRepository;
import board.server.app.enums.RoleType;
import board.server.app.member.entity.Member;
import board.server.app.member.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@Transactional
@Slf4j
public class BoardScrollPerformanceTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    EntityManager em;

    @BeforeEach
    void init(){

        // 100만건의 dummy data
        Member member = Member.builder()
                .name("hellohello")
                .email("helloworld@aaa.aaa")
                .roleType(RoleType.MEMBER)
                .password("helloabc")
                .build();


        memberRepository.save(member);

        List<Object[]> boardList = new ArrayList<>();

        String sql = "insert into board_table(title, contents, member_id) values (?, ?, ?)";

        for(int i=0; i<100_0000; i++){
            boardList.add(new Object[]{
                    "첫글 제목",
                    "안녕하세요",
                    member.getId()
            });

            if(i%10_0000 == 0){
                jdbcTemplate.batchUpdate(sql, boardList);

                boardList.clear();
            }
        }



        jdbcTemplate.batchUpdate(sql, boardList);
    }

    @Test
    @DisplayName("더미데이터 확인")
    void init_test(){

        String sql = "select count(*) from board_table";

        Long count = jdbcTemplate.queryForObject(sql, Long.class);

        log.info("생성된 데이터 건수: {}", count);
        Assertions.assertThat(count).isGreaterThanOrEqualTo(100_0000L);
    }

    @Test
    @DisplayName("offset 기반 무한 스크롤 테스트")
    void offset_test(){
        em.clear();

        // 100만건의 더미 데이터 중
        // 90만건의 데이터 오프셋부터 10개를 읽음
        // 10번 수행 후 평균시간 측정


        long avg_time = 0L;



        for(int i=0 ;i<5; i++){
            long start = System.currentTimeMillis();
            Pageable pageable = PageRequest.of(90000 + i, 10);

            boardRepository.findAllByOrderByIdDescWithMember(pageable);

            long end = System.currentTimeMillis();

            avg_time += (end - start);
        }

        avg_time /= 5;

        log.info("OFFSET 평균 시간: {}ms", avg_time);
    }

    @Test
    @DisplayName("offset 기반 무한 스크롤 테스트")
    void no_offset_test(){
        em.clear();

        // 100만건의 더미 데이터 중
        // 90만건의 데이터 오프셋부터 10개를 읽음
        // 20번 수행 후 평균시간 측정

        long avg_time = 0L;

        Pageable pageable = PageRequest.of(0, 10);

        for(int i=0 ;i<5; i++){
            long start = System.currentTimeMillis();

            boardRepository.findByLessThanIdOrderByIdDescWithMember(10_0000L - i, pageable);

            long end = System.currentTimeMillis();

            avg_time += (end - start);
        }

        avg_time /= 5;

        log.info("CURSOR 평균 시간: {}ms", avg_time);
    }
}
