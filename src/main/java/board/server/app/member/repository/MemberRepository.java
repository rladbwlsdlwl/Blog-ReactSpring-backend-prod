package board.server.app.member.repository;

import board.server.app.member.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    Member save(Member member);
    Optional<Member> findById(Long id);
    Optional<Member> findByName(String name);
    Optional<Member> findByEmail(String email);
    void delete(Member member);
    void deleteById(Long id);
    List<Member> findAll();
}
