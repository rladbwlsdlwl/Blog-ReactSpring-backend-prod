package board.server.app.user.repository;

import board.server.app.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByIdAndUsername(Long id, String username);
    void delete(User user);
    List<User> findAll();
}
