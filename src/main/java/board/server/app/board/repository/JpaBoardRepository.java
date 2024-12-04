package board.server.app.board.repository;


import board.server.app.board.entity.Board;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//@Repository
public class JpaBoardRepository implements BoardRepository{

    @Autowired
    private EntityManager em;


    @Override
    public Board save(Board board) {
        em.persist(board);

        return board;
    }

    @Override
    public Optional<Board> findById(Long id) {
        Board board = em.find(Board.class, id);

        return Optional.ofNullable(board);
    }

    // fetch join (eager)
    @Override
    public List<Board> findByMember_name(String name) {
        String sql = "select b from Board b join fetch b.member m where m.name = :name";
        List<Board> boardList = em.createQuery(sql, Board.class)
                .setParameter("name", name)
                .getResultList();

        return boardList;
    }

    @Override
    public void deleteById(Long id) {
        String sql = "delete from Board b where b.id = :id";
        em.createQuery(sql)
                .setParameter("id", id)
                .executeUpdate();
    }

    @Override
    public void delete(Board board) {
        em.remove(board);
    }

    @Override
    public List<Board> findTop10ByOrderByCreatedAtDesc() {
        String sql = "select b from Board b join fetch b.member order by b.createdAt desc";
        List<Board> boardList = em.createQuery(sql, Board.class)
                .setFirstResult(0)
                .setMaxResults(10)
                .getResultList();

        return boardList;
    }

    @Override
    public List<Board> findTop10ByOrderByCreatedAtDescWithMember(Pageable pageable) {
        return null;
    }
}
