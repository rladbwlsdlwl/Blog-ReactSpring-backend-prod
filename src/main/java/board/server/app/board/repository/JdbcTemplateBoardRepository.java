package board.server.app.board.repository;

import board.server.app.board.entity.Board;
import board.server.app.member.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

//@Repository
public class JdbcTemplateBoardRepository implements BoardRepository{
    @Autowired JdbcTemplate jdbcTemplate;


    // POST - 게시판 저장
    @Override
    public Board save(Board board) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("board_table").usingGeneratedKeyColumns("id");

        Map<String, Object> params = new HashMap<>();
        params.put("title", board.getTitle());
        params.put("contents", board.getContents());
        params.put("member_id", board.getMember().getId()); // author to member_id
        params.put("views", board.getViews());
        params.put("created_at", board.getCreatedAt());

        Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(params));

        board.setId(key.longValue());

        return board;
    }

    @Override
    public Optional<Board> findById(Long id) {
        String sql = "select * from board_table where id = ?";

        return jdbcTemplate.query(sql, BoardMapper(), id).stream().findAny();
    }

    @Override
    public List<Board> findByMember_name(String name) {
        String sql = "select * from board_table b left join member_table m on b.member_id = m.id where m.name = ?";

        return jdbcTemplate.query(sql, BoardNameMapper(), name);
    }

    public Long update(Board board) {
        String sql = "update board_table set title = ?, contents = ?, views = ? where id = ?";

        jdbcTemplate.update(sql, board.getTitle(), board.getContents(), board.getViews(), board.getId());

        return board.getId();
    }

    @Override
    public void deleteById(Long id) {
        String sql = "delete from board_table where id = ?";

        jdbcTemplate.update(sql, id);
    }

    @Override
    public void delete(Board board) {
        deleteById(board.getId());
    }

    @Override
    public List<Board> findTop10ByOrderByCreatedAtDesc() {
        String sql = "select * from board_table b left join member_table m on b.member_id = m.id limit 10";

        return jdbcTemplate.query(sql, BoardNameMapper());
    }

    @Override
    public List<Board> findTop10ByOrderByCreatedAtDescWithMember(Pageable pageable) {
        return null;
    }

    private RowMapper<Board> BoardNameMapper() {
        return (rs, rowNum) -> {
            Member member = Member.builder()
                    .name(rs.getString("name"))
                    .id(rs.getLong("member_id"))
                    .build();

            Board board = Board.builder()
                    .id(rs.getLong("id"))
                    .title(rs.getString("title"))
                    .contents(rs.getString("contents"))
                    .views(rs.getLong("views"))
                    .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                    .member(member)
                    .build();

            return board;
        };
    }

    private RowMapper<Board> BoardMapper() {
        return (rs, rowNum) -> {
            Member member = Member.builder()
                    .id(rs.getLong("member_id"))
                    .build();

            Board board = Board.builder()
                    .id(rs.getLong("id"))
                    .title(rs.getString("title"))
                    .contents(rs.getString("contents"))
                    .views(rs.getLong("views"))
                    .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                    .member(member)
                    .build();


            return board;
        };
    }
}
