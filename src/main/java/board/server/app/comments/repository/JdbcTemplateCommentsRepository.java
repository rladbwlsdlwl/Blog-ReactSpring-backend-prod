package board.server.app.comments.repository;

import board.server.app.comments.entity.Comments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;


@Repository
public class JdbcTemplateCommentsRepository implements CommentsRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Comments> findById(Long id) {
        String sql = "select * from COMMENT_TABLE where id = ?";

        return jdbcTemplate.query(sql, CommentsMapper(), id).stream().findAny();
    }

    @Override
    public List<Comments> findByBoardId(Long boardId) {
        String sql = "select * from COMMENT_TABLE c left join MEMBER_TABLE m on c.member_id = m.id where c.board_id = ? order by created_at";

        return jdbcTemplate.query(sql, CommentsNameMapper(), boardId);
    }

    @Override
    public Comments save(Comments comments) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);

        simpleJdbcInsert.withTableName("COMMENT_TABLE").usingGeneratedKeyColumns("id");

        Map<String, Object> param = new HashMap<>();

        LocalDateTime date = LocalDateTime.now();
        param.put("parent_id", comments.getParentId());
        param.put("board_id", comments.getBoardId());
        param.put("member_id", comments.getAuthor());
        param.put("comments", comments.getContents());
        param.put("created_at", date);

        Number number = simpleJdbcInsert.executeAndReturnKey(new MapSqlParameterSource(param));

        comments.setId(number.longValue());
        comments.setCreatedAt(date);

        return comments;
    }

    @Override
    public void update(Comments comments) {
        String sql = "update COMMENT_TABLE set comments = ? where id = ?";

        jdbcTemplate.update(sql, comments.getContents(), comments.getId());
    }

    @Override
    public void delete(Long id) {
        String sql = "delete from COMMENT_TABLE where id = ?";

        jdbcTemplate.update(sql, id);
    }

    private RowMapper<Comments> CommentsMapper() {
        return ((rs, rowNum) -> {
            Long id = rs.getLong("id");
            Long boardId = rs.getLong("board_id");
            Long memberId = rs.getLong("member_id");
            Long parentId = rs.getLong("parent_id");
            String contents = rs.getString("comments");
            LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();

            Comments comments = Comments.builder()
                    .id(id)
                    .parentId(parentId)
                    .author(memberId)
                    .boardId(boardId)
                    .contents(contents)
                    .createdAt(createdAt)
                    .build();

            return comments;
        });
    }

    private RowMapper<Comments> CommentsNameMapper() {
        return ((rs, rowNum) -> {
            Long id = rs.getLong("id");
            Long boardId = rs.getLong("board_id");
            Long memberId = rs.getLong("member_id");
            String name = rs.getString("name");
            Long parentId = rs.getLong("parent_id");
            String contents = rs.getString("comments");
            LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();

            Comments comments = Comments.builder()
                    .id(id)
                    .parentId(parentId)
                    .author(memberId)
                    .authorName(name)
                    .boardId(boardId)
                    .contents(contents)
                    .createdAt(createdAt)
                    .build();

            return comments;
        });
    }
}
