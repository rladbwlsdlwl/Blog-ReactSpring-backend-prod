package board.server.app.user.repository;

import board.server.app.user.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.*;

@Repository
public class JdbcTemplateUserRepository implements UserRepository{
    private final JdbcTemplate jt;

    @Autowired
    public JdbcTemplateUserRepository(DataSource dataSource) {
        this.jt = new JdbcTemplate(dataSource);
    }

    @Override
    public User save(User user) {
        SimpleJdbcInsert ji = new SimpleJdbcInsert(jt);
        ji.withTableName("USER_TABLE").usingGeneratedKeyColumns("id");

        Map<String, Object> params = new HashMap<>();
        params.put("username", user.getUsername());
        params.put("email", user.getEmail());
        params.put("password", user.getPassword());

        Number key = ji.executeAndReturnKey(new MapSqlParameterSource(params));
        user.setId(key.longValue());

        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        String sql = "select * from USER_TABLE where id = ?";
        List<User> user = jt.query(sql, UserMapper(), id);

        return user.stream().findAny();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "select * from USER_TABLE where username = ?";
        List<User> query = jt.query(sql, UserMapper(), username);

        return query.stream().findAny();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "select * from USER_TABLE where email = ?";
        List<User> query = jt.query(sql, UserMapper(), email);

        return query.stream().findAny();
    }

    @Override
    public void delete(User user) {
        String query = "delete from USER_TABLE where id = ?";
        jt.update(query, user.getId());

    }

    @Override
    public List<User> findAll() {
        String sql = "select * from USER_TABLE";
        return jt.query(sql, UserMapper());
    }


    private RowMapper<User> UserMapper() {
        // mapRow
        return ((rs, rowNum) -> {
            String username = rs.getString("username");
            String email = rs.getString("email");
            String password = rs.getString("password");
            Long id = rs.getLong("id");

            return User.builder()
                    .username(username)
                    .email(email)
                    .password(password)
                    .id(id)
                    .build();
        });
    }

}
