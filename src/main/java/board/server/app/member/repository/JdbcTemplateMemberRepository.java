package board.server.app.member.repository;

import board.server.app.enums.RoleType;
import board.server.app.member.entity.Member;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.*;

@Slf4j
@Repository
public class JdbcTemplateMemberRepository implements MemberRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcTemplateMemberRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Member save(Member member) {
        SimpleJdbcInsert ji = new SimpleJdbcInsert(jdbcTemplate);

        /* insert member table */
        ji.withTableName("MEMBER_TABLE").usingGeneratedKeyColumns("id");

        Map<String, Object> params = new HashMap<>();
        params.put("name", member.getName());
        params.put("email", member.getEmail());
        params.put("password", member.getPassword());

        Number key = ji.executeAndReturnKey(new MapSqlParameterSource(params));
        member.setId(key.longValue());


        /* insert role table */
        /* default role = "MEMBER" */
        ji = new SimpleJdbcInsert(jdbcTemplate);
        ji.withTableName("ROLE_TABLE").usingGeneratedKeyColumns("id");

        Map<String, Object> param = new HashMap<>();
        param.put("role", "MEMBER");
        param.put("member_id", key);

        ji.executeAndReturnKey(new MapSqlParameterSource(param));
        member.setRoleType(RoleType.ROLE_DEFAULT);


        return member;
    }

    // spring data JPA, update -> save
    @Override
    public void update(Member member) {
        String sql = "update member_table set name = ?, password = ?, email = ? where id = ?";
        jdbcTemplate.update(sql, member.getName(), member.getPassword(), member.getEmail(), member.getId());
    }

    @Override
    public Optional<Member> findById(Long id) {
        String sql = "select * from MEMBER_TABLE where id = ?";
        List<Member> member = jdbcTemplate.query(sql, MemberMapper(), id);

        return member.stream().findAny();
    }

    @Override
    public Optional<Member> findByName(String name) {
        String sql = "select * from MEMBER_TABLE where name = ?";
        List<Member> query = jdbcTemplate.query(sql, MemberMapper(), name);

        return query.stream().findAny();
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        String sql = "select * from MEMBER_TABLE where email = ?";
        List<Member> query = jdbcTemplate.query(sql, MemberMapper(), email);

        return query.stream().findAny();
    }

    @Override
    public Optional<Member> findByIdAndName(Long id, String name) {
        String sql = "select * from MEMBER_TABLE where id = ? and name = ?";
        List<Member> query = jdbcTemplate.query(sql, MemberMapper(), id, name);

        return query.stream().findAny();
    }

    @Override
    public Optional<Member> findByNameAndRole(String name) {
        //m.id, m.name, m.email, m.password, r.role
        String sql = "select m.id, m.name, m.email, m.password, r.role from MEMBER_TABLE m join ROLE_TABLE r on m.id = r.member_id where m.name = ?";
        List<Member> query = jdbcTemplate.query(sql, MemberRoleMapper(), name);

        return query.stream().findAny();
    }

    @Override
    public void deleteById(Long id) {
        String query = "delete from MEMBER_TABLE where id = ?";
        jdbcTemplate.update(query, id);
    }

    @Override
    public List<Member> findAll() {
        String sql = "select * from MEMBER_TABLE";
        return jdbcTemplate.query(sql, MemberMapper());
    }


    private RowMapper<Member> MemberMapper() {
        // mapRow
        return ((rs, rowNum) -> {
            String username = rs.getString("name");
            String email = rs.getString("email");
            String password = rs.getString("password");
            Long id = rs.getLong("id");

            return Member.builder()
                    .name(username)
                    .email(email)
                    .password(password)
                    .id(id)
                    .build();
        });
    }
    private RowMapper<Member> MemberRoleMapper() {
        return ((rs, rowNum) -> {
            String username = rs.getString("name");
            String email = rs.getString("email");
            String password = rs.getString("password");
            Long id = rs.getLong("id");
            RoleType role = rs.getString("role").equals("MEMBER") ? RoleType.ROLE_DEFAULT : RoleType.ROLE_ADMIN;

            return Member.builder()
                    .name(username)
                    .email(email)
                    .password(password)
                    .id(id)
                    .role(role)
                    .build();
        });
    }
}
