package board.server.app.tags.repository;

import board.server.app.tags.entity.Tags;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataJpaTagsRepository extends JpaRepository<Tags, Long>, TagsRepository {
}
