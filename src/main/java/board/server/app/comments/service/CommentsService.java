package board.server.app.comments.service;

import board.server.app.board.entity.Board;
import board.server.app.board.repository.BoardRepository;
import board.server.app.comments.dto.CommentsResponseDto;
import board.server.app.comments.entity.Comments;
import board.server.app.comments.repository.CommentsRepository;
import board.server.app.member.repository.MemberRepository;
import board.server.error.errorcode.CustomExceptionCode;
import board.server.error.exception.BusinessLogicException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommentsService {
    @Autowired
    private CommentsRepository commentsRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private MemberRepository memberRepository;


    // 댓글 읽기
    public Map<Long, List<CommentsResponseDto>> getComments(List<Long> boardIdList){
        if(boardIdList.isEmpty()) return new HashMap<>();

        List<CommentsResponseDto> commentsList = commentsRepository.findByBoard_IdInWithMemberOrderByCreatedAtAsc(boardIdList)
                .stream()
                .map(CommentsResponseDto::new)
                .toList();


        Map<Long, List<CommentsResponseDto>> commentsMapList = boardIdList
                .stream()
                .collect(Collectors.toMap(id -> id,
                        id -> commentsList.stream()
                                .filter(c -> c.getBoard_id() == id)
                                .toList()
                ));

        return commentsMapList;
    }

    // 댓글 추가
    public Comments setComments(Comments comments){
        validatePresentBoardId(comments.getBoard().getId());
        validatePresentMemberId(comments.getMember().getId());
        validatePresentId(comments.getComments() != null ? comments.getComments().getId(): null);

        return commentsRepository.save(comments);
    }

    // 댓글 수정
    public void updateComments(Comments comments){
        validatePresentId(comments.getId());

        commentsRepository.update(comments);
    }

    // 댓글 삭제
    public void removeComments(Long commentsId, Long userId){
        validatePresentMemberId(userId);
        Comments comments = validatePresentId(commentsId);
        Long boardId = comments.getBoard().getId(), commentsAuthor = comments.getMember().getId();
        Long author = validatePresentBoardId(boardId).getMember().getId();

        // 댓글 작성자 또는 게시글 작성자만 허용
        if(author != userId && commentsAuthor != userId)
            throw new BusinessLogicException(CustomExceptionCode.MEMBER_NO_PERMISSION);


        commentsRepository.deleteById(commentsId);
    }


    // 대댓글 존재 여부 확인 - 무결성 제약 조건, id <- parent_id
    // 댓글 존재 여부 확인
    private Comments validatePresentId(Long id) {
        if(id != null && id != 0){
            return commentsRepository.findById(id).orElseThrow(() ->
                    new BusinessLogicException(CustomExceptionCode.COMMENTS_NO_PERMISSION)
            );
        }
        return null;
    }

    // 유저 확인
    private void validatePresentMemberId(Long author) {
        memberRepository.findById(author).orElseThrow(() ->
                new BusinessLogicException(CustomExceptionCode.MEMBER_NOT_FOUND)
        );
    }

    // 게시글 확인
    private Board validatePresentBoardId(Long boardId) {
        return boardRepository.findById(boardId).orElseThrow(() ->
                new BusinessLogicException(CustomExceptionCode.BOARD_NOT_FOUND)
        );
    }

}
