package board.server.app.boardTags.repository;

import board.server.app.boardTags.entity.BoardTags;

import java.util.List;

public interface CustomBoardTagsRepository {
    List<BoardTags> saveAll(List<BoardTags> boardTags);
}
