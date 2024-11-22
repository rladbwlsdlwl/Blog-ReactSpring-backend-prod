package board.server.app.board.service;


import board.server.app.board.entity.Board;
import board.server.app.board.repository.BoardRepository;
import board.server.app.member.repository.MemberRepository;
import board.server.error.errorcode.CustomExceptionCode;
import board.server.error.exception.BusinessLogicException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Board join(Board board, String name){
        validatePresentMemberId(board.getMember().getId());

        return boardRepository.save(board);
    }

    // 게시글 읽기
    public Board getBoard(Long id, String name){
        validatePresentMemberName(name);

        Board board = boardRepository.findById(id).orElseThrow(() ->
            new BusinessLogicException(CustomExceptionCode.BOARD_NOT_FOUND)
        );

        // 조회수 올리기
        Long views = board.getViews();
        board.setViews(views + 1);
        setBoard(board);

        return board;
    }

    // 게시글 리스트 읽기 - 유저 1명
    public List<Board> getBoardList(String name){
        validatePresentMemberName(name);

        return boardRepository.findByMember_name(name);
    }

    // 게시글 리스트 읽기 - 모든 유저
    public List<Board> getBoardListAll(){
        return boardRepository.findTop10ByOrderByCreatedAtDesc();
    }

    
    // 게시글 수정
    public Long setBoard(Board board){
        validatePresentMemberId(board.getMember().getId());

        return boardRepository.update(board);
    }

    // 게시글 삭제
    public Long removeBoard(String username, Long boardId){
        validatePresentMemberName(username);

        boardRepository.deleteById(boardId);

        return boardId;
    }


    // POST, GET - 글 작성 전 or 읽어오기 전, 유효한 계정인지 체크 (Integrity Constraint)
    private void validatePresentMemberId(Long author) {
        memberRepository.findById(author).orElseThrow(() ->
                new BusinessLogicException(CustomExceptionCode.MEMBER_NO_PERMISSION)
        );
    }
    private void validatePresentMemberName(String name) {
        memberRepository.findByName(name).orElseThrow(() ->
                new BusinessLogicException(CustomExceptionCode.MEMBER_NO_PERMISSION)
        );
    }
}
