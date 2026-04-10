package board.server.app.boardTags.service;

import board.server.app.board.entity.Board;
import board.server.app.board.repository.BoardRepository;
import board.server.app.boardTags.entity.BoardTags;
import board.server.app.boardTags.repository.BoardTagsRepository;
import board.server.app.boardTags.repository.CustomBoardTagsRepository;
import board.server.app.member.entity.Member;
import board.server.app.member.repository.MemberRepository;
import board.server.app.tags.entity.Tags;
import board.server.app.tags.repository.CustomTagsRepository;
import board.server.app.tags.repository.TagsRepository;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BoardTagsServiceIntegrationTest {

    @Autowired
    private BoardTagsService boardTagsService;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BoardTagsRepository boardTagsRepository;
    @Autowired
    private CustomBoardTagsRepository customBoardTagsRepository;
    @Autowired
    private TagsRepository tagsRepository;
    @Autowired
    private CustomTagsRepository customTagsRepository;

    private Long boardId;
    private Long memberId;
    private Board board;
    private Member member;


    @BeforeEach
    void init(){
        member = Member.builder()
                .name("ygguhhuhihijijo")
                .email("huhihihihi@aaa.aaa")
                .build();
        board = Board.builder()
                .member(member)
                .title("hello")
                .contents("world")
                .createdAt(LocalDateTime.now())
                .views(0L)
                .build();


        member = memberRepository.save(member);
        board = boardRepository.save(board);

        memberId = member.getId();
        boardId = board.getId();
    }


    @Test
    void getTaglist() {
        // GIVEN
        // Board Id 에 연결된 서울맛집 고기맛집 태그 찾기


        // Tags -> ['서울맛집', '고기맛집']
        Tags tags1 = Tags.of("서울맛집");
        Tags tags2 = Tags.of("고기맛집");

        customTagsRepository.saveAll(List.of(tags1, tags2));

        // customTagsRepository.saveAll은 id 값을 저장하지 않음
        // 재조회하여 저장 확인과 id 찾기
        List<Tags> byNameIn = tagsRepository.findByNameIn(List.of("서울맛집", "고기맛집"));



        // BoardTags -> [{서울맛집, boardId}, {고기맛집, boardId}]
        List<BoardTags> savedtaglist = byNameIn.stream().map(tags -> {

            return BoardTags.builder()
                    .tags(tags).board(board)
                    .build();
        }).toList();


        customBoardTagsRepository.saveAll(savedtaglist);

        // WHEN
        List<BoardTags> taglist = boardTagsService.getTaglist(boardId);



        // THEN
        // 태그 수 검증
        Assertions.assertThat(taglist.size()).isEqualTo(2);

        // 태그에 연결된 게시글 검증
        Assertions.assertThat(taglist.get(0).getBoard().getId()).isEqualTo(boardId);
        Assertions.assertThat(taglist.get(1).getBoard().getId()).isEqualTo(boardId);

        // 태그에 연결된 게시글의 작성자 검증
        Assertions.assertThat(taglist.get(0).getBoard().getMember().getId()).isEqualTo(memberId);
        Assertions.assertThat(taglist.get(1).getBoard().getMember().getId()).isEqualTo(memberId);

        // 태그명 검증 ["서울맛집", "고기맛집"]
        List<String> tagnamelist = taglist.stream().map(boardTags -> boardTags.getTags().getName()).toList();

        Assertions.assertThat(tagnamelist).containsExactlyInAnyOrderElementsOf(List.of("고기맛집", "서울맛집"));
    }

    @Test
    void join() {
        // GIVEN
        // 서울맛집/고기맛집 태그 TAGS, BOARDTAGS 추가
        List<String> taglist = List.of("서울맛집", "고기맛집");


        // WHEN
        boardTagsService.join(taglist, boardId);




        // THEN
        // 저장되었는지 확인
        List<BoardTags> boardtaglist  = boardTagsRepository.findByBoard_IdWithTags(boardId);


        // 태그 수 확인
        Assertions.assertThat(boardtaglist.size()).isEqualTo(2);

        // 서울맛집, 고기맛집 확인
        List<String> checkTag = boardtaglist.stream().map(boardTags -> boardTags.getTags().getName()).toList();
        Assertions.assertThat(checkTag).containsExactlyInAnyOrderElementsOf(List.of("서울맛집", "고기맛집"));
    }

    @Test
    void update() {
        // GIVEN
        // 서울맛집 태그 TAGS, BOARDTAGS 추가
        // 고기맛집 태그 BOARDTAGS 추가
        List<String> taglist = List.of("서울맛집", "고기맛집");



        // WHEN
        boardTagsService.update(taglist, boardId);




        // THEN
        List<BoardTags> boardtagslist = boardTagsRepository.findByBoard_IdWithTags(boardId);


        // 서울맛집, 고기맛집 검증
        List<String> tagnamelist = boardtagslist.stream().map(boardtags -> boardtags.getTags().getName()).toList();
        Assertions.assertThat(tagnamelist).containsExactlyInAnyOrderElementsOf(List.of("서울맛집", "고기맛집"));
    }
}