package board.server.app.role.repository;

import board.server.app.role.entity.Role;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//@Repository
public class JpaRoleRepository implements RoleRepository{

    private final EntityManager em;

    @Autowired
    public JpaRoleRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public Role save(Role role) {
        em.persist(role);
        return role;
    }

    @Override
    public Optional<Role> findById(Long id) {
        Role role = em.find(Role.class, id);
        return Optional.ofNullable(role);
    }

    @Override
    public void delete(Role role) {
        em.remove(role);
    }

}
