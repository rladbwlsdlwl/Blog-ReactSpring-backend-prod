package board.server.app.member.repository;

import board.server.app.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface SpringDataJpaMemberRepository extends JpaRepository<Member, Long>, MemberRepository{
    @Override
    @Query("select m from Member m join fetch m.role where m.name = :name")
    Optional<Member> findByNameWithRole(@Param("name") String name);
}
