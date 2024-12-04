package board.server.app.likes.repository;

import board.server.app.likes.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataJpaLikesRepository extends JpaRepository<Likes, Long>, LikesRepository {
}
