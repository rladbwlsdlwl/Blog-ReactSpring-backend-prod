package board.server.app.role.repository;

import board.server.app.role.entity.Role;
import java.util.Optional;

public interface RoleRepository {
    Role save(Role role);
    Optional<Role> findById(Long id);
    void delete(Role role);
}
