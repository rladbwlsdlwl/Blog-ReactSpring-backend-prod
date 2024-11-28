package board.server.app.comments.repository;

import board.server.app.comments.entity.Comments;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaCommentsRepository implements CommentsRepository{

    @Autowired
    private EntityManager em;

    @Override
    public Optional<Comments> findById(Long id) {
        Comments comments = em.find(Comments.class, id);

        return Optional.ofNullable(comments);
    }

    @Override
    public List<Comments> findByBoard_IdInWithMemberOrderByCreatedAtAsc(List<Long> idList) {
        String sql = "select c from Comments c join fetch c.member where c.board.id in :boardIdList order by c.createdAt asc";

        List<Comments> commentsList = em.createQuery(sql, Comments.class)
                .setParameter("boardIdList", idList)
                .getResultList();

        return commentsList;
    }

    @Override
    public Comments save(Comments comments) {
        em.persist(comments);

        return comments;
    }

    // 변경 감지 (dirty checking)
    @Override
    public void update(Comments comments) {
        return ;
    }

    @Override
    public void deleteById(Long id) {
        String sql = "delete from Comments c where c.id = :id";
        em.createQuery(sql)
                .setParameter("id", id)
                .executeUpdate();
    }

    @Override
    public void delete(Comments comments) {
        em.remove(comments);
    }
}
