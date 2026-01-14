package board.server.app.member.repository;

import board.server.app.member.entity.Member;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//@Repository
public class JpaMemberRepository implements MemberRepository{

    private final EntityManager em;

    @Autowired
    public JpaMemberRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public Member save(Member member) {
        em.persist(member);

        return member;
    }

    @Override
    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);

        return Optional.ofNullable(member);
    }

    @Override
    public Optional<Member> findByName(String name) {
        String sql = "select m from Member m where m.name = :name";
        List<Member> memberList = em.createQuery(sql, Member.class)
                .setParameter("name", name)
                .getResultList();

        return memberList.stream().findAny();
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        String sql = "select m from Member m where m.email = :email";
        List<Member> memberList = em.createQuery(sql, Member.class)
                .setParameter("email", email)
                .getResultList();

        return memberList.stream().findAny();
    }

    @Override
    public Optional<Member> findByNameOrEmail(String name, String email) {
        String sql = "select m from Member m where m.name = :name or m.email = :email";

        List<Member> memberList = em.createQuery(sql, Member.class)
                .setParameter("name", name)
                .setParameter("email", email)
                .getResultList();

        return memberList.stream().findAny();
    }

    @Override
    public void delete(Member member) {
        em.remove(member);
    }

    @Override
    public void deleteById(Long id) {
        String sql = "delete from Member m where m.id = :id";
        em.createQuery(sql)
                .setParameter("id", id)
                .executeUpdate();
    }

    @Override
    public List<Member> findAll() {
        String sql = "select m from Member m";
        List<Member> memberList = em.createQuery(sql, Member.class)
                .getResultList();

        return memberList;
    }
}
