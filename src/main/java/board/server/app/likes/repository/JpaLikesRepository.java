package board.server.app.likes.repository;

import board.server.app.likes.entity.Likes;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//@Repository
public class JpaLikesRepository implements LikesRepository{

    @Autowired
    private EntityManager em;


    @Override
    public List<Likes> findByBoard_Id(Long id) {
        String sql = "select l from Likes l where l.board.id = :id";

        List<Likes> likesList = em.createQuery(sql, Likes.class)
                .setParameter("id", id)
                .getResultList();

        return likesList;
    }

    @Override
    public List<Likes> findByBoard_IdIn(List<Long> idList) {
        String sql = "select l from Likes l where l.board.id in :idList";

        List<Likes> likesList = em.createQuery(sql, Likes.class)
                .setParameter("idList", idList)
                .getResultList();

        return likesList;
    }

    @Override
    public Optional<Likes> findByBoard_IdAndMember_Id(Long boardId, Long memberId) {
        String sql = "select l from Likes l where l.board.id = :boardId and l.member.id = :memberId";

        List<Likes> likesList = em.createQuery(sql, Likes.class)
                .setParameter("boardId", boardId)
                .setParameter("memberId", memberId)
                .getResultList();

        return likesList.stream().findAny();
    }

    @Override
    public Likes save(Likes likes) {
        em.persist(likes);

        return likes;
    }

    @Override
    public void delete(Likes likes) {
        em.remove(likes);
    }

    @Override
    public void deleteById(Long id) {
        String sql = "delete from Likes l where l.id = :id";

        em.createQuery(sql)
                .setParameter("id", id)
                .executeUpdate();
    }
}
