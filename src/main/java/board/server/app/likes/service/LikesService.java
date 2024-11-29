package board.server.app.likes.service;

import board.server.app.likes.dto.LikesResponseDto;
import board.server.app.likes.entity.Likes;
import board.server.app.likes.repository.LikesRepository;
import board.server.error.errorcode.CustomExceptionCode;
import board.server.error.exception.BusinessLogicException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class LikesService {
    @Autowired
    private LikesRepository likesRepository;

    // 좋아요 읽기
    public Map<Long, List<LikesResponseDto>> getLikesList(List<Long> boardIdList){
        List<LikesResponseDto> likesList = likesRepository.findByBoard_IdIn(boardIdList).stream()
                .map(LikesResponseDto::of).toList();


        Map<Long, List<LikesResponseDto>> likesMap = boardIdList.stream()
                .collect(Collectors.toMap(
                        boardId -> boardId,
                        boardId -> likesList.stream()
                                .filter(likes -> likes.getPostId().equals(boardId))
                                .toList()
                ));

        return likesMap;

        /*
        Map<Long, List<Likes>> likesList = new HashMap<>();

        boardIdList.stream().forEach(postId -> {
            List<Likes> res = likesRepository.findByBoard_Id(postId);

            likesList.put(postId, res);
        });

        return likesList;
         */
    }

    // 좋아요 생성
    public Likes setLikes(Likes likes){
        validateDuplicateLikes(likes.getMember().getId(), likes.getBoard().getId());

        return likesRepository.save(likes);
    }

    // 좋아요 삭제
    public void removeLikes(Likes likes){
        Likes findLikes = validatePresentLikes(likes.getMember().getId(), likes.getBoard().getId());

        likesRepository.delete(findLikes);
    }

    // 좋아요 작성
    // 중복 작성 불가능 (게시판당 하나의 좋아요만 가능)
    private void validateDuplicateLikes(Long author, Long postId) {
        likesRepository.findByBoard_IdAndMember_Id(postId, author).ifPresent(likes -> {
            throw new BusinessLogicException(CustomExceptionCode.LIKES_NO_PERMISSION);
        });
    }

    // 좋아요 삭제
    private Likes validatePresentLikes(Long author, Long postId) {
        return likesRepository.findByBoard_IdAndMember_Id(postId, author).orElseThrow(() ->
                new BusinessLogicException(CustomExceptionCode.LIKES_NO_PERMISSION)
        );
    }
}
