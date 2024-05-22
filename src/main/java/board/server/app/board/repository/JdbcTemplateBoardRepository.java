package board.server.app.board.repository;

import board.server.app.board.entity.Board;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcTemplateBoardRepository implements BoardRepository{
    @Autowired JdbcTemplate jdbcTemplate;


    // POST - 게시판 저장
    @Override
    public Board save(Board board) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("BOARD_TABLE").usingGeneratedKeyColumns("id");

        HashMap<String, Object> params = new HashMap<>();
        params.put("title", board.getTitle());
        params.put("contents", board.getContents());
        params.put("user_id", board.getAuthor()); // author to user_id

        Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(params));
        board.setId(key.longValue());

        return board;
    }

    // GET - 게시판 글
    @Override
    public Optional<Board> findByIdAndUsername(Long id, String username) {
        String sql = "select * from BOARD_TABLE as b join USER_TABLE as u on b.user_id = u.id where b.id = ? and u.username = ?";
        List<Board> board = jdbcTemplate.query(sql, BoardMapper(), id, username);

        return board.stream().findAny();
    }

    // GET - 유저의 모든 게시판 목록
    @Override
    public List<Board> findByAuthor(Long author) {
        String sql = "select * from BOARD_TABLE where user_id = ?";

        List<Board> query = jdbcTemplate.query(sql, BoardMapper(), author);

        return query;
    }

    @Override
    public Long update(Board board) {

        return null;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public List<Board> findAll() {
        String sql = "select * from board_table";

        return jdbcTemplate.query(sql, BoardMapper());
    }

    private RowMapper<Board> BoardMapper() {
        return (rs, rowNum) -> {
            Board board = Board.builder()
                    .id(rs.getLong("id"))
                    .title(rs.getString("title"))
                    .contents(rs.getString("contents"))
                    .author(rs.getLong("user_id"))
                    .build();

            return board;
        };
    }
}
