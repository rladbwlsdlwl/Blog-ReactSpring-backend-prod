package board.server.app.board.service;


import board.server.app.board.entity.Board;
import board.server.app.board.repository.JdbcTemplateBoardRepository;
import board.server.app.user.entity.User;
import board.server.app.user.repository.JdbcTemplateUserRepository;
import board.server.error.errorcode.CustomExceptionCode;
import board.server.error.exception.BusinessLogicException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
public class BoardService {
    private final JdbcTemplateBoardRepository jdbcTemplateBoardRepository;
    private final JdbcTemplateUserRepository jdbcTemplateUserRepository;

    @Autowired
    public BoardService(JdbcTemplateBoardRepository jdbcTemplateBoardRepository, JdbcTemplateUserRepository jdbcTemplateUserRepository) {
        this.jdbcTemplateBoardRepository = jdbcTemplateBoardRepository;
        this.jdbcTemplateUserRepository = jdbcTemplateUserRepository;
    }


    public Board getBoard(Long id, String username){
        validatePresentUser(username);

        return jdbcTemplateBoardRepository.findByIdAndUsername(id, username).orElseThrow(() ->
            new BusinessLogicException(CustomExceptionCode.BOARD_NOT_FOUND)
        );
    }

    public List<Board> getBoardList(String username){
        Long author = validatePresentUser(username).getId();

        return jdbcTemplateBoardRepository.findByAuthor(author);
    }

    public List<Board> getBoardListAll(){
        return jdbcTemplateBoardRepository.findAll();
    }

    public Long join(Board board, String username){
//        validatePresentUser(board.getAuthor());
        validatePresentUser(board.getAuthor(), username);

        return jdbcTemplateBoardRepository.save(board).getId();
    }

    private void validatePresentUser(Long id, String username) {
        jdbcTemplateUserRepository.findByIdAndUsername(id, username).orElseThrow(() ->
                new BusinessLogicException(CustomExceptionCode.USER_NO_PERMISSION)
        );
    }

    // POST, GET - 글 작성 전 or 읽어오기 전, 유효한 계정인지 체크 (Integrity Consrtrait)
    private void validatePresentUser(Long author) {
        jdbcTemplateUserRepository.findById(author).orElseThrow(() ->
                new BusinessLogicException(CustomExceptionCode.USER_NO_PERMISSION)
        );
    }
    private User validatePresentUser(String username) {
        return jdbcTemplateUserRepository.findByUsername(username).orElseThrow(() ->
                new BusinessLogicException(CustomExceptionCode.USER_NO_PERMISSION)
        );
    }
}
