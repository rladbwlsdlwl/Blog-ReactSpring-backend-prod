package board.server.app.boardTags.repository;

import board.server.app.boardTags.entity.BoardTags;

import java.util.List;

public interface BoardTagsRepository {
    List<BoardTags> findByBoard_IdWithTags(Long boardId);
    void deleteByIdIn(List<Long> idList);
}
