package board.server.app.role.repository;

import board.server.app.role.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataJpaRoleRepository extends JpaRepository<Role, Long>, RoleRepository {
}
