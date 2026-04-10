package board.server.app.tags.repository;

import board.server.app.tags.entity.Tags;

import java.util.List;

public interface CustomTagsRepository {
    List<Tags> saveAll(List<Tags> tagsList);
}
