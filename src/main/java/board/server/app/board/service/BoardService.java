package board.server.app.board.service;


import board.server.app.board.entity.Board;
import board.server.app.board.repository.BoardRepository;
import board.server.app.member.repository.MemberRepository;
import board.server.error.errorcode.CustomExceptionCode;
import board.server.error.exception.BusinessLogicException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@Transactional
public class BoardService {
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public BoardService(BoardRepository boardRepository, MemberRepository memberRepository) {
        this.boardRepository = boardRepository;
        this.memberRepository = memberRepository;
    }

    // 게시글 작성
    public Board join(Board board){
        return boardRepository.save(board);
    }

    // 게시글 읽기
    public Board getBoard(Long id, String name){
        validatePresentMemberName(name);

        Board board = boardRepository.findById(id).orElseThrow(() ->
            new BusinessLogicException(CustomExceptionCode.BOARD_NOT_FOUND)
        );

        // 조회수 올리기
        // dirty checking
        Long views = board.getViews();
        board.setViews(views + 1);

        return board;
    }

    // 게시글 리스트 읽기 - 유저 1명
    public List<Board> getBoardList(String name){
        validatePresentMemberName(name);

        return boardRepository.findByMember_name(name);
    }

    // 게시글 리스트 읽기 - 모든 유저
    public List<Board> getBoardListAll(Integer lastId){
        return boardRepository.findTop10ByOrderByCreatedAtDescWithMember(PageRequest.of(lastId, 10, Sort.by("createdAt").descending()));
    }

    
    // 게시글 수정
    public Long setBoard(Board board, Long boardId){

        Board findBoard = validateLoginUserAndAuthor(board.getMember().getId(), boardId);


        findBoard.setTitle(board.getTitle());
        findBoard.setContents(board.getContents());

        return findBoard.getId();
        // return boardRepository.update(board);
    }



    // 게시글 삭제
    public Long removeBoard(Long userId, Long boardId){
        Board findBoard = validateLoginUserAndAuthor(userId, boardId);

        boardRepository.deleteById(boardId);

        return boardId;
    }


    // PATCH, DELETE: 게시글 접근 시 유저 정보와 저자 정보가 일치하는지 검증
    private Board validateLoginUserAndAuthor(Long userId, Long boardId) {
        Board findBoard = boardRepository.findById(boardId).orElseThrow(() -> new BusinessLogicException(CustomExceptionCode.BOARD_NOT_FOUND));

        // 게시글 작성자와 로그인 유저와 다른 경우
        if(findBoard.getMember().getId() != userId) throw new BusinessLogicException(CustomExceptionCode.MEMBER_NO_PERMISSION);


        return findBoard;
    }

    // GET: 게시글 작성자가 존재하는지 검증
    private void validatePresentMemberName(String name) {
        memberRepository.findByName(name).orElseThrow(() ->
                new BusinessLogicException(CustomExceptionCode.MEMBER_NO_PERMISSION)
        );
    }
}
