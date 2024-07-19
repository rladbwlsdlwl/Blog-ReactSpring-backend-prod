package board.server.app.board.service;


import board.server.app.board.entity.Board;
import board.server.app.board.repository.BoardRepository;
import board.server.app.board.repository.JdbcTemplateBoardRepository;
import board.server.app.member.entity.Member;
import board.server.app.member.repository.JdbcTemplateMemberRepository;
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
    public BoardService(JdbcTemplateBoardRepository jdbcTemplateBoardRepository, JdbcTemplateMemberRepository memberRepository) {
        this.boardRepository = jdbcTemplateBoardRepository;
        this.memberRepository = memberRepository;
    }


    public Board getBoard(Long id, String name){
        validatePresentMember(name);

        return boardRepository.findByIdAndName(id, name).orElseThrow(() ->
            new BusinessLogicException(CustomExceptionCode.BOARD_NOT_FOUND)
        );
    }

    public List<Board> getBoardList(String name){
        Long author = validatePresentMember(name).getId();

        return boardRepository.findByAuthor(author);
    }

    public List<Board> getBoardListAll(){
        return boardRepository.findAll();
    }

    public Long join(Board board, String name){
//        validatePresentMember(board.getAuthor());
        validatePresentMember(board.getAuthor(), name);

        return boardRepository.save(board).getId();
    }

    private void validatePresentMember(Long id, String name) {
        memberRepository.findByIdAndName(id, name).orElseThrow(() ->
                new BusinessLogicException(CustomExceptionCode.MEMBER_NO_PERMISSION)
        );
    }

    // POST, GET - 글 작성 전 or 읽어오기 전, 유효한 계정인지 체크 (Integrity Consrtrait)
    private void validatePresentMember(Long author) {
        memberRepository.findById(author).orElseThrow(() ->
                new BusinessLogicException(CustomExceptionCode.MEMBER_NO_PERMISSION)
        );
    }
    private Member validatePresentMember(String name) {
        return memberRepository.findByName(name).orElseThrow(() ->
                new BusinessLogicException(CustomExceptionCode.MEMBER_NO_PERMISSION)
        );
    }
}
