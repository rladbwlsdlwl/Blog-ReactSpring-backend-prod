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
        params.put("member_id", board.getAuthor()); // author to member_id

        Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(params));
        board.setId(key.longValue());

        return board;
    }

    // GET - 게시판 글
    @Override
    public Optional<Board> findByIdAndName(Long id, String name) {
        String sql = "select * from BOARD_TABLE as b join MEMBER_TABLE as m on b.member_id = m.id where b.id = ? and m.name = ?";
        List<Board> board = jdbcTemplate.query(sql, BoardMapper(), id, name);

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
        String sql = "update BOARD_TABLE set title = ?, contents = ? where id = ?";

        jdbcTemplate.update(sql, board.getTitle(), board.getContents(), board.getId());


        return board.getId();
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public List<Board> findAll() {
        String sql = "select * from board_table b left join member_table m on b.member_id = m.id limit 10";

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
                    .build();

            return board;
        };
    }
}
