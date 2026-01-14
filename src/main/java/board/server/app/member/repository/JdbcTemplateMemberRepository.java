package board.server.app.member.repository;

import board.server.app.enums.RoleType;
import board.server.app.member.entity.Member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.*;

//@Slf4j
//@Repository
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
        ji.withTableName("member_table").usingGeneratedKeyColumns("id");

        Map<String, Object> params = new HashMap<>();
        params.put("name", member.getName());
        params.put("email", member.getEmail());
        params.put("password", member.getPassword());
        params.put("role", member.getRoleType());

        Number key = ji.executeAndReturnKey(new MapSqlParameterSource(params));
        member.setId(key.longValue());

        return member;
    }

    // spring data JPA, update -> save
    public void update(Member member) {
        String sql = "update member_table set name = ?, password = ?, email = ? where id = ?";
        jdbcTemplate.update(sql, member.getName(), member.getPassword(), member.getEmail(), member.getId());
    }

    @Override
    public Optional<Member> findById(Long id) {
        String sql = "select * from member_table where id = ?";
        List<Member> member = jdbcTemplate.query(sql, MemberMapper(), id);

        return member.stream().findAny();
    }

    @Override
    public Optional<Member> findByName(String name) {
        String sql = "select * from member_table where name = ?";
        List<Member> query = jdbcTemplate.query(sql, MemberMapper(), name);

        return query.stream().findAny();
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        String sql = "select * from member_table where email = ?";
        List<Member> query = jdbcTemplate.query(sql, MemberMapper(), email);

        return query.stream().findAny();
    }

    @Override
    public Optional<Member> findByNameOrEmail(String name, String email) {
        String sql = "select * from member_table where name = ? or email = ?";

        List<Member> memberList = jdbcTemplate.query(sql, MemberMapper(), name, email);

        return memberList.stream().findAny();
    }

    @Override
    public void delete(Member member) {
        deleteById(member.getId());
    }

    @Override
    public void deleteById(Long id) {
        String query = "delete from member_table where id = ?";
        jdbcTemplate.update(query, id);
    }

    @Override
    public List<Member> findAll() {
        String sql = "select * from member_table";
        return jdbcTemplate.query(sql, MemberMapper());
    }

    private RowMapper<Member> MemberMapper() {
        // mapRow
        return ((rs, rowNum) -> {
            String username = rs.getString("name");
            String email = rs.getString("email");
            String password = rs.getString("password");
            Long id = rs.getLong("id");
            RoleType roleType = rs.getString("role").equals("MEMBER") ? RoleType.MEMBER : RoleType.ADMIN;

            return Member.builder()
                    .name(username)
                    .email(email)
                    .password(password)
                    .id(id)
                    .roleType(roleType)
                    .build();
        });
    }
}
