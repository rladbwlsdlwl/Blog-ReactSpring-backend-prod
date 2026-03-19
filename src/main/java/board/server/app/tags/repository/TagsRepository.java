package board.server.app.tags.repository;

import board.server.app.tags.entity.Tags;

import java.util.List;

public interface TagsRepository {
    List<Tags> saveAll(List<Tags> tagsList);
    List<Tags> findByNameIn(List<String> tagnameList);
}
