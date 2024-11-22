package board.server.app.member.repository;

import board.server.app.enums.RoleType;
import board.server.app.member.entity.Member;
import board.server.app.role.entity.Role;
import board.server.app.role.repository.RoleRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaMemberRepository implements MemberRepository{

    private final EntityManager em;
    private final RoleRepository roleRepository;

    @Autowired
    public JpaMemberRepository(EntityManager em, RoleRepository roleRepository) {
        this.em = em;
        this.roleRepository = roleRepository;
    }

    @Override
    public Member save(Member member) {
        em.persist(member);

        Role role = Role.builder()
                .roleType(RoleType.MEMBER)
                .member(member)
                .build();

        Role savedRole = roleRepository.save(role);
        member.setRole(savedRole);

        return member;
    }

//    내부적으로 변경감지 (dirty checking)
    @Override
    public void update(Member member) {

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
    public Optional<Member> findByIdAndName(Long id, String name) {
        String sql = "select m from Member m where m.id = :id and m.name = :name";
        List<Member> memberList = em.createQuery(sql, Member.class)
                .setParameter("id", id)
                .setParameter("name", name)
                .getResultList();

        return memberList.stream().findAny();
    }

    @Override
    public Optional<Member> findByNameWithRole(String name) {
        // FETCH EAGER = 즉시 로딩
        String sql = "select m from Member m join fetch m.role r where m.name = :name";
        List<Member> memberList = em.createQuery(sql, Member.class)
                .setParameter("name", name)
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