package board.server.app.boardTags.service;

import board.server.app.board.entity.Board;
import board.server.app.board.repository.BoardRepository;
import board.server.app.boardTags.entity.BoardTags;
import board.server.app.boardTags.repository.BoardTagsRepository;
import board.server.app.boardTags.repository.CustomBoardTagsRepository;
import board.server.app.member.entity.Member;
import board.server.app.tags.entity.Tags;
import board.server.app.tags.repository.CustomTagsRepository;
import board.server.app.tags.repository.TagsRepository;
import board.server.error.errorcode.CustomExceptionCode;
import board.server.error.exception.BusinessLogicException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class BoardTagsServiceTest {

    @InjectMocks
    BoardTagsService boardTagsService;

    @Mock
    BoardTagsRepository boardTagsRepository;
    @Mock
    CustomBoardTagsRepository customBoardTagsRepository;
    @Mock
    TagsRepository tagsRepository;
    @Mock
    CustomTagsRepository customTagsRepository;
    @Mock
    BoardRepository boardRepository;


    private Long boardId;
    private Long memberId;
    private Long author;
    private Member member;
    private Board board;


    @BeforeEach
    void init() {
        author = 0L;
        memberId = 0L;
        boardId = 0L;

        member = Member.builder()
                .id(author)
                .build();
        board = Board.builder()
                .member(member)
                .build();
    }


    @DisplayName("getTaglist - 성공: 게시글의 태그 목록 조회")
    @Test
    void getTaglist_success() {

        // GIVEN
        List<BoardTags> boardTagsList = List.of(BoardTags.of(0L, 0L), BoardTags.of(0L, 0L));

        // 게시글 존재 검증
        Mockito.when(boardRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(board));
        // 태그 조회
        Mockito.when(boardTagsRepository.findByBoard_IdWithTags(Mockito.anyLong())).thenReturn(boardTagsList);


        // WHEN
        List<BoardTags> taglist = boardTagsService.getTaglist(boardId);


        // THEN
        Mockito.verify(boardRepository, Mockito.times(1)).findById(boardId);
        Mockito.verify(boardTagsRepository, Mockito.times(1)).findByBoard_IdWithTags(boardId);


        Assertions.assertThat(taglist).containsExactlyInAnyOrderElementsOf(boardTagsList);
    }


    @DisplayName("getTaglist - 실패: 존재하지 않는 게시글")
    @Test
    void getTaglist_fail_boardId() {
        // GIVEN
        Mockito.when(boardRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        // WHEN
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> {

            boardTagsService.getTaglist(boardId);

        });

        // THEN
        Mockito.verify(boardRepository).findById(boardId);
        Mockito.verify(boardTagsRepository, Mockito.never()).findByBoard_IdWithTags(Mockito.anyLong());


        Assertions.assertThat(exception.getExceptionCode()).isEqualTo(CustomExceptionCode.BOARD_NOT_FOUND);
    }


//    --------------------------------------------------------------

    @DisplayName("join - 성공: Tags, BoardTags SAVE ALL")
    @Test
    void join_success() {
        // GIVEN
        // 요청 태그 리스트: ["맛집", "일상"]
        // TAGS 태그 리스트: ["맛집"]
        List<String> taglist = List.of("맛집", "일상");
        List<Tags> beforeTaglist = List.of(Tags.of("맛집"));
        List<Tags> savedTaglist = List.of(Tags.of("일상"));

        // 기댓 값
        // Tags SAVE ALL -> 일상 태그 추가
        // BoardTags SAVE ALL -> 맛집, 일상 추가


        // TAGS 태그 조회 ["맛집"] -> 맛집 태그는 이미 존재
        // TAGS 태그 조회 ["일상"] -> 추가 로직에서 일상 로직 조회 실행
        Mockito.when(tagsRepository.findByNameIn(Mockito.any())).thenReturn(beforeTaglist).thenReturn(savedTaglist);
        // TAGS 태그 저장 ["일상"]
        Mockito.when(customTagsRepository.saveAll(Mockito.any())).thenReturn(savedTaglist);

        // BoardTags 태그 저장 ["맛집", "일상"]
        Mockito.when(customBoardTagsRepository.saveAll(Mockito.anyList())).thenReturn(List.of(BoardTags.of(boardId, 0L, "맛집"), BoardTags.of(boardId, 1L, "일상")));


        // WHEN
        boardTagsService.join(taglist, boardId);


        // THEN
        // TAGS ["일상"] SAVE ALL 검증
        // BOARDTAGS ["일상", "맛집"] SAVE ALL 검증
        ArgumentCaptor<List<Tags>> argcaptor1 = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List<BoardTags>> argcaptor2 = ArgumentCaptor.forClass(List.class);

        Mockito.verify(customTagsRepository, Mockito.times(1)).saveAll(argcaptor1.capture());
        Mockito.verify(customBoardTagsRepository, Mockito.times(1)).saveAll(argcaptor2.capture());


        Assertions.assertThat(argcaptor1.getValue()).extracting(Tags:: getName).containsExactlyInAnyOrderElementsOf(List.of("일상"));
        Assertions.assertThat(argcaptor2.getValue()).extracting(boardTags -> boardTags.getTags().getName()).containsExactlyInAnyOrderElementsOf(List.of("일상", "맛집"));
    }



//    ----------------------------------------------------------

    // A: DB 조회 태그 리스트 (변경 전)
    // B: 유저 요청 태그 리스트 (변경 후)
    @DisplayName("update - 성공: A delete, B save 실행")
    @Test
    void update_success() {
        // 기존 태그 리스트 (A): ["맛집", "먹방"]
        // 요청 태그 리스트 (B): ["일상", "수원맛집", "먹방"]
        List<String> afterList = List.of("일상", "수원맛집", "먹방");
        List<BoardTags> beforeList = List.of(BoardTags.of(0L, boardId, 0L, "맛집"), BoardTags.of(1L, boardId, 1L, "먹방"));


        // 응답 결과
        // -> 맛집 태그 삭제 (0L)
        // -> 일상, 수원맛집 추가

        // CASE 1. 일상 태그는 TAG 테이블에 존재 + 수원맛집 태그는 TAG 테이블에 없음
        // -> 수원맛집 태그 TAG 추가
        // -> 일상, 수원맛집 BoardTags 추가


        // GIVEN
        // 기존 게시글 태그 조회 ["맛집", "먹방"] -> 삭제 및 추가 태그 알아냄
        Mockito.when(boardTagsRepository.findByBoard_IdWithTags(Mockito.any())).thenReturn(beforeList);

        // 태그 조회 ["일상"] -> TAGS 수원 맛집 추가
        Mockito.when(tagsRepository.findByNameIn(List.of("일상", "수원맛집"))).thenReturn(List.of(Tags.of("일상", 5L)));
        // 태그 추가 성공 후 조회 (id 조회 목적) -> ["수원맛집"]
        Mockito.when(tagsRepository.findByNameIn(List.of("수원맛집"))).thenReturn(List.of(Tags.of("수원맛집", 6L)));

        // 태그 저장 ["수원맛집"]
        // tag id 주입 -> 일상, 수원맛집 태그 저장 시 필요
        Mockito.when(customTagsRepository.saveAll(Mockito.any())).thenReturn(List.of(Tags.of("수원맛집", 6L)));

        // WHEN
        boardTagsService.update(afterList, boardId);


        // THEN
        // TAGS 수원맛집 SAVE ALL 검증
        // BOARDTAGS 수원맛집, 일상 SAVE ALL 검증
        // BOARDTAGS 맛집 DELETE BY ID IN 검증
        ArgumentCaptor<List<Tags>> argcaptor1 = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List<BoardTags>> argcaptor2 = ArgumentCaptor.forClass(List.class);


        // A 리스트의 "맛집" 삭제
        Mockito.verify(boardTagsRepository, Mockito.times(1)).deleteByIdIn(List.of(0L));

        // B 리스트의 ["수원맛집"] 추가
        Mockito.verify(customTagsRepository, Mockito.times(1)).saveAll(argcaptor1.capture());

        // B 리스트의 ["일상", "수원맛집"] 포함하여 게시글태그 레코드 추가
        Mockito.verify(customBoardTagsRepository, Mockito.times(1)).saveAll(argcaptor2.capture());

        Assertions.assertThat(argcaptor1.getValue()).extracting(Tags:: getName).containsExactlyInAnyOrderElementsOf(List.of("수원맛집"));
        Assertions.assertThat(argcaptor2.getValue()).extracting(boardTags -> boardTags.getTags().getId()).containsExactlyInAnyOrderElementsOf(List.of(5L, 6L));
    }


    @DisplayName("update - 성공: 태그 리스트 A, B 가 빈 리스트일 경우 -> A, B 는 삭제 추가 연산 작업 X")
    @Test
    void update_success_emptyAB() {
        // 기존 태그 리스트 (A): []
        // 요청 태그 리스트 (B): []
        List<String> afterList = new ArrayList<>();
        List<BoardTags> beforeList = new ArrayList<>();
        List<Long> beforeIdlist = new ArrayList<>();

        // GIVEN
        // 기존 태그리스트는 비어있음
        Mockito.when(boardTagsRepository.findByBoard_IdWithTags(Mockito.any())).thenReturn(beforeList);


        // WHEN
        boardTagsService.update(afterList, boardId);


        // THEN
        Mockito.verify(boardTagsRepository, Mockito.times(1)).findByBoard_IdWithTags(boardId);
        Mockito.verify(tagsRepository, Mockito.never()).findByNameIn(Mockito.anyList());


        // TAGS SAVE ALL X
        Mockito.verify(customTagsRepository, Mockito.never()).saveAll(Mockito.anyList());
        // BOARDTAGS SAVE ALL X
        Mockito.verify(customBoardTagsRepository, Mockito.never()).saveAll(Mockito.anyList());
        // BOARDTAGS DELETE BY ID IN X
        Mockito.verify(boardTagsRepository, Mockito.never()).deleteByIdIn(Mockito.anyList());
    }


    @DisplayName("update - 성공: 태그 리스트 A가 빈 리스트일 경우 -> B save all 실행")
    @Test
    void update_success_emptyA() {
        // 기존 태그 리스트 (A): []
        // 요청 태그 리스트 (B): ["맛집", "먹방"]
        List<String> afterList = List.of("맛집", "먹방");
        List<BoardTags> beforeList = new ArrayList<>();


        // 기대값 -> BOARD TAGS ["맛집", "먹방"] SAVE ALL
        // CASE 2. 태그 리스트에 ["맛집", "먹방"] 존재 + 게시글 태그 리스트는 []
        // -> 맛집, 먹방 BOARD TAGS 추가
        // -> 맛집, 먹방 TAGS 조회 존재


        // GIVEN
        // board tags 빈 리스트
        Mockito.when(boardTagsRepository.findByBoard_IdWithTags(Mockito.anyLong())).thenReturn(beforeList);
        // tags 조회 시, ["맛집", 먹방"]
        Mockito.when(tagsRepository.findByNameIn(Mockito.anyList())).thenReturn(List.of(Tags.of("맛집", 1L), Tags.of("먹방", 2L)));

        // WHEN
        boardTagsService.update(afterList, boardId);


        // THEN
        // TAGS FIND BY NAME IN ["맛집", "먹방"]
        // TAGS SAVE ALL X
        // BOARD TAGS SAVE ALL ["맛집", "먹방"]
        // BOARD TAGS DELETE BY ID IN X
        ArgumentCaptor<List<BoardTags>> argcaptor1 = ArgumentCaptor.forClass(List.class);


        Mockito.verify(tagsRepository, Mockito.times(1)).findByNameIn(afterList);
        Mockito.verify(customTagsRepository, Mockito.never()).saveAll(Mockito.anyList());
        Mockito.verify(customBoardTagsRepository, Mockito.times(1)).saveAll(argcaptor1.capture());
        Mockito.verify(boardTagsRepository, Mockito.never()).deleteByIdIn(Mockito.anyList());

        Assertions.assertThat(argcaptor1.getValue()).extracting(boardTags -> boardTags.getTags().getName()).containsExactlyInAnyOrderElementsOf(List.of("맛집", "먹방"));
    }

    @DisplayName("update - 성공: 태그 리스트 B가 빈 리스트일 경우 -> A delete tag 실행")
    @Test
    void update_success_emptyB() {
        // 기존 태그 리스트 (A): ["맛집", "먹방"]
        // 요청 태그 리스트 (B): []
        List<String> afterList = new ArrayList<>();
        List<BoardTags> beforeList = List.of(BoardTags.of(0L, boardId, 0L, "맛집"), BoardTags.of(1L, boardId, 1L, "일상"));

        // GIVEN
        // 기대값 -> 맛집, 먹방 board tags delete
        // CASE 3. 요청 태그가 비어있어 조회, 저장 X + 기존 게시글 태그 삭제
        // -> NO SAVE ALL
        // -> BOARD TAGS 맛집, 먹방 DELETE

         // 게시글 태그 ["일상", "맛집"] 존재
        Mockito.when(boardTagsRepository.findByBoard_IdWithTags(Mockito.any())).thenReturn(beforeList);


        // WHEN
        boardTagsService.update(afterList, boardId);


        // THEN
        // TAGS SAVE ALL X
        // BOARD TAGS SAVE ALL X
        // BOARD TAGS DELETE 맛집, 먹방
        Mockito.verify(boardTagsRepository, Mockito.times(1)).findByBoard_IdWithTags(boardId);
        Mockito.verify(boardTagsRepository, Mockito.times(1)).deleteByIdIn(List.of(0L, 1L));

        Mockito.verify(customTagsRepository, Mockito.never()).saveAll(Mockito.anyList());
        Mockito.verify(customBoardTagsRepository, Mockito.never()).saveAll(Mockito.anyList());
    }

    @DisplayName("update - 성공: 태그 리스트 A, B 가 존재하며, B 중복 제거 성공 케이스 -> save all, delete tag 실행")
    @Test
    void update_success_nonUniqueTaglist(){
        List<String> taglist = List.of("맛집", "수원맛집", "수원맛집");
        List<BoardTags> beforeTaglist = List.of(BoardTags.of(0L, boardId, 0L, "맛집"), BoardTags.of(1L, boardId, 1L, "일상"));



        // 기댓값 -> 수원 맛집 추가, 일상 삭제
        // CASE 4. taglist 중복 제거 + 일상 BOARD TAGS 삭제 + 수원맛집 TAGS 추가 + 수원맛집 BOARD TAGS 추가
        // -> TAGS ["수원맛집"] 추가
        // -> BOARDTAGS ["수원맛집"] 추가
        // -> BOARDTAGS ["일상"] 삭제

        // GIVEN
         // 태그 조회 ["맛집", "일상"]
        Mockito.when(boardTagsRepository.findByBoard_IdWithTags(Mockito.any())).thenReturn(beforeTaglist);
        // 수원맛집 조회 시, 태그 없음
        // 태그 저장 후 수원맛집 추가된 상태에서 조회 (id 값 찾기 위해 조회)
        Mockito.when(tagsRepository.findByNameIn(List.of("수원맛집")))
                .thenReturn(new ArrayList<>())
                .thenReturn(List.of(Tags.of("수원맛집", 3L)));;

        // 태그 저장 ["수원맛집"]
        Mockito.when(customTagsRepository.saveAll(Mockito.anyList())).thenReturn(List.of(Tags.of("수원맛집", 3L)));
        // 게시글 태그 저장 ["수원맛집"]
        Mockito.when(customBoardTagsRepository.saveAll(Mockito.any())).thenReturn(List.of(BoardTags.of(5L, boardId, 3L, "수원맛집")));


        // WHEN
        boardTagsService.update(taglist, boardId);


        // THEN
        // TAGS SAVE ALL 수원맛집 (Unique)
        // BOARD TAGS SAVE ALL 수원맛집
        // BOARD TAGS DELETE 일상

        Mockito.verify(boardTagsRepository, Mockito.times(1)).findByBoard_IdWithTags(boardId);


        ArgumentCaptor<List<Tags>> argcaptor1 = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List<BoardTags>> argcaptor2 = ArgumentCaptor.forClass(List.class);

        Mockito.verify(customTagsRepository, Mockito.times(1)).saveAll(argcaptor1.capture());
        Mockito.verify(customBoardTagsRepository, Mockito.times(1)).saveAll(argcaptor2.capture());
        Mockito.verify(boardTagsRepository, Mockito.times(1)).deleteByIdIn(List.of(1L)); // 일상 boardtags의 id는 1L

        Assertions.assertThat(argcaptor1.getValue()).extracting(Tags:: getName).containsExactlyInAnyOrderElementsOf(List.of("수원맛집"));
        Assertions.assertThat(argcaptor2.getValue()).extracting(boardTags -> boardTags.getTags().getName()).containsExactlyInAnyOrderElementsOf(List.of("수원맛집"));
        Assertions.assertThat(argcaptor2.getValue()).extracting(boardTags -> boardTags.getTags().getId()).containsExactlyInAnyOrderElementsOf(List.of(3L)); // 수원맛집 TAG ID는 3L
    }
}