package board.server.app.likes.service;

import board.server.app.likes.dto.LikesRequestDto;
import board.server.app.likes.entity.Likes;
import board.server.app.likes.repository.LikesRepository;
import board.server.error.errorcode.CustomExceptionCode;
import board.server.error.exception.BusinessLogicException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class LikesService {
    @Autowired
    private LikesRepository likesRepository;

    public Map<Long, List<Likes>> getLikesList(List<Long> boardIdList){
        Map<Long, List<Likes>> likesList = new HashMap<>();

        boardIdList.stream().forEach(postId -> {
            List<Likes> res = likesRepository.findByPostId(postId);

            likesList.put(postId, res);
        });

        return likesList;
    }

    public Likes setLikes(Likes likes){
        validateDuplicateLikes(likes.getAuthor(), likes.getPostId());

        return likesRepository.save(likes);
    }

    public void removeLikes(Likes likes){
        validatePresentLikes(likes.getAuthor(), likes.getPostId());

        likesRepository.delete(likes);
    }

    // 좋아요 작성
    // 중복 작성 불가능 (게시판당 하나의 좋아요만 가능)
    private void validateDuplicateLikes(Long author, Long postId) {
        likesRepository.findByPostIdAndAuthor(postId, author).ifPresent(likes -> {
            throw new BusinessLogicException(CustomExceptionCode.LIKES_NO_PERMISSION);
        });
    }

    // 좋아요 삭제
    private void validatePresentLikes(Long author, Long postId) {
        likesRepository.findByPostIdAndAuthor(postId, author).orElseThrow(() ->
                new BusinessLogicException(CustomExceptionCode.LIKES_NO_PERMISSION)
        );
    }
}
