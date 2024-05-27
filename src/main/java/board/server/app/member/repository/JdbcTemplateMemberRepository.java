package board.server.app.member.repository;

import board.server.app.member.entity.Member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.*;

@Repository
public class JdbcTemplateMemberRepository implements MemberRepository {
    private final JdbcTemplate jt;

    @Autowired
    public JdbcTemplateMemberRepository(DataSource dataSource) {
        this.jt = new JdbcTemplate(dataSource);
    }

    @Override
    public Member save(Member member) {
        SimpleJdbcInsert ji = new SimpleJdbcInsert(jt);
        ji.withTableName("MEMBER_TABLE").usingGeneratedKeyColumns("id");

        Map<String, Object> params = new HashMap<>();
        params.put("name", member.getName());
        params.put("email", member.getEmail());
        params.put("password", member.getPassword());

        Number key = ji.executeAndReturnKey(new MapSqlParameterSource(params));
        member.setId(key.longValue());

        return member;
    }

    @Override
    public Optional<Member> findById(Long id) {
        String sql = "select * from MEMBER_TABLE where id = ?";
        List<Member> member = jt.query(sql, MemberMapper(), id);

        return member.stream().findAny();
    }

    @Override
    public Optional<Member> findByName(String name) {
        String sql = "select * from MEMBER_TABLE where name = ?";
        List<Member> query = jt.query(sql, MemberMapper(), name);

        return query.stream().findAny();
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        String sql = "select * from MEMBER_TABLE where email = ?";
        List<Member> query = jt.query(sql, MemberMapper(), email);

        return query.stream().findAny();
    }

    @Override
    public Optional<Member> findByIdAndName(Long id, String name) {
        String sql = "select * from MEMBER_TABLE where id = ? and name = ?";
        List<Member> query = jt.query(sql, MemberMapper(), id, name);

        return query.stream().findAny();
    }

    @Override
    public void delete(Member member) {
        String query = "delete from MEMBER_TABLE where id = ?";
        jt.update(query, member.getId());

    }

    @Override
    public List<Member> findAll() {
        String sql = "select * from MEMBER_TABLE";
        return jt.query(sql, MemberMapper());
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

}
