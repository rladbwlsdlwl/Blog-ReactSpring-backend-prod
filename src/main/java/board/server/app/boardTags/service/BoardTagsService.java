package board.server.app.boardTags.service;

import board.server.app.board.repository.BoardRepository;
import board.server.app.boardTags.entity.BoardTags;
import board.server.app.boardTags.repository.BoardTagsRepository;
import board.server.app.tags.entity.Tags;
import board.server.app.tags.repository.TagsRepository;
import board.server.error.errorcode.CustomExceptionCode;
import board.server.error.exception.BusinessLogicException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class BoardTagsService {

    @Autowired
    private BoardTagsRepository boardTagsRepository;
    @Autowired
    private TagsRepository tagsRepository;
    @Autowired
    private BoardRepository boardRepository;


    // 게시글 읽기 (태그 전체 읽기)
    public List<BoardTags> getTaglist(Long boardId){
        validatePresentBoard(boardId);

        List<BoardTags> boardTagList = boardTagsRepository.findByBoard_IdWithTags(boardId);

        return boardTagList;
    }


    // 게시글 작성 (태그 전체 작성)
    public void join(List<String> tagList, Long boardId, Long memberId){
        Long author = validatePresentBoard(boardId);
        checkAuthorAndActiveUser(author, memberId);

        if(tagList == null)
            tagList = new ArrayList<>();


        // Tags 테이블의 name은 유니크 조건을 만족
        // 조회 후 없는 태그만 save all
        List<Tags> tagslist = tagsRepository.findByNameIn(tagList);
        Set<String> tagsset = tagslist.stream().map(Tags:: getName).collect(Collectors.toSet());
        // 해시태그 생성 (tags_table)
        List<Tags> savedTaglist = tagList.stream()
                .filter(name -> !tagsset.contains(name))
                .map(name -> Tags.of(name))
                .collect(Collectors.toList());

        if(!savedTaglist.isEmpty())
            savedTaglist = tagsRepository.saveAll(savedTaglist);



        // 게시글 - 해시태그 연결 (board_tags_table)
        List<BoardTags> boardTagsList = Stream.concat(savedTaglist.stream(), tagslist.stream())
                .map(tags -> BoardTags.of(boardId, tags.getId(), tags.getName()))
                .collect(Collectors.toList());

        if(!boardTagsList.isEmpty())
            boardTagsRepository.saveAll(boardTagsList);
    }


    // 게시글 업데이트 (태그 생성 및 삭제)
    public void update(List<String> taglist, Long boardId, Long memberId){
        Long author = validatePresentBoard(boardId);
        checkAuthorAndActiveUser(author, memberId);

        if(taglist == null)
            taglist = new ArrayList<>();

        // ------------------------------------------

        // 기존 태그 조회
        List<BoardTags> beforeBoardTaglist = boardTagsRepository.findByBoard_IdWithTags(boardId);
        List<String> beforeTaglist = beforeBoardTaglist.stream().map(bf -> bf.getTags().getName()).collect(Collectors.toList());


        // 기존 태그 A와 변경된 태그 B 비교
        // A, B 중복 값 제거
        // -> A는 제거할 태그 리스트, B는 추가할 태그 리스트를 의미함


        Set<String> beforeTagset = new HashSet<>(beforeTaglist);
        Set<String> afterTagset = new HashSet<>(taglist);

        // A 중복 제거
        beforeBoardTaglist = beforeBoardTaglist.stream().filter(boardTags -> !afterTagset.contains(boardTags.getTags().getName())).toList();

        // B 중복 제거
        taglist = taglist.stream().filter(tagname -> !beforeTagset.contains(tagname)).distinct().toList();


        // -------------------------------------------

        // 태그 테이블의 name은 유니크해야함
        // 조회 후 조건에 부합하는 데이터만 추가



        // DB INSERT (B)
        // Tags 인스턴스 활성화
        List<Tags> currTagslist = new ArrayList<>();
        if(!taglist.isEmpty())
            currTagslist = tagsRepository.findByNameIn(taglist);

        // 기존 태그에 없던 태그 생성
        List<String> currTaglist = currTagslist.stream().map(Tags:: getName).toList();
        List<Tags> addTagslist = taglist.stream().filter(tagname -> !currTaglist.contains(tagname)).map(Tags:: of).toList();

        if(!addTagslist.isEmpty())
            addTagslist = tagsRepository.saveAll(addTagslist);


        // BoardTags 인스턴스 생성
        List<BoardTags> addBoardTagsList = Stream.concat(currTagslist.stream(), addTagslist.stream())
                .map(tag -> BoardTags.of(boardId, tag.getId(), tag.getName())).toList();

        // 기존에 있던 태그와 없던 태그 모두 게시글과 연결하여 생성
        if(!addBoardTagsList.isEmpty())
            addBoardTagsList = boardTagsRepository.saveAll(addBoardTagsList);


        // DB DELETE (A)
        List<Long> boardTaglist = beforeBoardTaglist.stream().map(boardTags -> boardTags.getId()).toList();


        if(!boardTaglist.isEmpty())
            boardTagsRepository.deleteByIdIn(boardTaglist);
    }


    // 비즈니스 로직
    // 게시글 존재 검증
    private Long validatePresentBoard(Long boardId) {
        return boardRepository.findById(boardId).orElseThrow(() -> new BusinessLogicException(CustomExceptionCode.BOARD_NOT_FOUND)).getMember().getId();
    }


    // 저자와 접속자 일치 여부 확인
    private void checkAuthorAndActiveUser(Long author, Long memberId) {
        if(!author.equals(memberId)) throw new BusinessLogicException(CustomExceptionCode.MEMBER_NO_PERMISSION);
    }

}
