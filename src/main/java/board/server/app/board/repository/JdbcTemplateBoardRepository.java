package board.server.app.board.repository;

import board.server.app.board.entity.Board;
import org.springframework.beans.factory.annotation.Autowired;
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

@Repository
public class JdbcTemplateBoardRepository implements BoardRepository{
    @Autowired JdbcTemplate jdbcTemplate;


    // POST - 게시판 저장
    @Override
    public Board save(Board board) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("BOARD_TABLE").usingGeneratedKeyColumns("id");

        LocalDateTime currentTime = LocalDateTime.now();

        Map<String, Object> params = new HashMap<>();
        params.put("title", board.getTitle());
        params.put("contents", board.getContents());
        params.put("member_id", board.getAuthor()); // author to member_id
        params.put("views", 0L);
        params.put("created_at", currentTime);

        Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(params));

        board.setId(key.longValue());
        board.setViews(0L);
        board.setCreated_at(currentTime);


        return board;
    }

    @Override
    public Optional<Board> findById(Long id) {
        String sql = "select * from BOARD_TABLE where id = ?";

        return jdbcTemplate.query(sql, BoardMapper(), id).stream().findAny();
    }

    // GET - 게시판 글
    @Override
    public Optional<Board> findByIdAndUsername(Long id, String username) {
        String sql = "select * from BOARD_TABLE as b join MEMBER_TABLE as m on b.member_id = m.id where b.id = ? and m.name = ?";
        List<Board> board = jdbcTemplate.query(sql, BoardMapper(), id, username);

        return board.stream().findAny();
    }

    // GET - 유저의 모든 게시판 목록
    @Override
    public List<Board> findByAuthor(Long author) {
        String sql = "select * from BOARD_TABLE where member_id = ?";

        List<Board> query = jdbcTemplate.query(sql, BoardMapper(), author);

        return query;
    }

    @Override
    public Long update(Board board) {
        String sql = "update BOARD_TABLE set title = ?, contents = ?, views = ? where id = ?";

        jdbcTemplate.update(sql, board.getTitle(), board.getContents(), board.getViews(), board.getId());


        return board.getId();
    }

    @Override
    public void delete(Long id) {
        String sql = "delete from BOARD_TABLE where id = ?";

        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<Board> findAll() {
        String sql = "select * from BOARD_TABLE b left join MEMBER_TABLE m on b.member_id = m.id limit 10";

        return jdbcTemplate.query(sql, BoardUsernameMapper());
    }

    private RowMapper<Board> BoardUsernameMapper() {
        return (rs, rowNum) -> {
            Board board = Board.builder()
                    .id(rs.getLong("id"))
                    .title(rs.getString("title"))
                    .contents(rs.getString("contents"))
                    .author(rs.getLong("member_id"))
                    .username(rs.getString("name"))
                    .views(rs.getLong("views"))
                    .created_at(rs.getTimestamp("created_at").toLocalDateTime())
                    .build();

            return board;
        };
    }

    private RowMapper<Board> BoardMapper() {
        return (rs, rowNum) -> {
            Board board = Board.builder()
                    .id(rs.getLong("id"))
                    .title(rs.getString("title"))
                    .contents(rs.getString("contents"))
                    .author(rs.getLong("member_id"))
                    .views(rs.getLong("views"))
                    .created_at(rs.getTimestamp("created_at").toLocalDateTime())
                    .build();

            return board;
        };
    }
}
