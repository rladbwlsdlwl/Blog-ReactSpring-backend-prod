package board.server.app.comments.repository;

import board.server.app.board.entity.Board;
import board.server.app.comments.entity.Comments;
import board.server.app.member.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


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
    public List<Comments> findByBoard_IdInWithMemberOrderByCreatedAtAsc(List<Long> idList) {
//        String sql = "select * from COMMENT_TABLE c left join MEMBER_TABLE m on c.member_id = m.id where c.board_id = ? order by created_at";
//        return jdbcTemplate.query(sql, CommentsNameMapper(), boardId);

        String sql = "select * from COMMENT_TABLE c join MEMBER_TABLE m on c.member_id = m.id where c.board_id in ";
        String sqlIdFilter = idList.stream().map(id -> "?").collect(Collectors.joining(", "));
        String sqlOrderby = " order by created_at asc";

        String SQL = sql + '(' + sqlIdFilter + ')' + sqlOrderby;
        return jdbcTemplate.query(SQL, CommentsNameMapper(), idList.toArray());
    }

    @Override
    public Comments save(Comments comments) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);

        simpleJdbcInsert.withTableName("COMMENT_TABLE").usingGeneratedKeyColumns("id");

        Map<String, Object> param = new HashMap<>();

        param.put("parent_id", comments.getComments() != null ? comments.getComments().getId(): null);
        param.put("board_id", comments.getBoard().getId());
        param.put("member_id", comments.getMember().getId());
        param.put("comments", comments.getContents());
        param.put("created_at", comments.getCreatedAt());

        Number number = simpleJdbcInsert.executeAndReturnKey(new MapSqlParameterSource(param));

        comments.setId(number.longValue());

        return comments;
    }

    @Override
    public void update(Comments comments) {
        String sql = "update COMMENT_TABLE set comments = ?, created_at = ? where id = ?";

        jdbcTemplate.update(sql, comments.getContents(), comments.getCreatedAt(), comments.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "delete from COMMENT_TABLE where id = ?";

        jdbcTemplate.update(sql, id);
    }

    @Override
    public void delete(Comments comments) {
        deleteById(comments.getId());
    }

    private RowMapper<Comments> CommentsMapper() {
        return ((rs, rowNum) -> {
            Long id = rs.getLong("id");
            Long boardId = rs.getLong("board_id");
            Long memberId = rs.getLong("member_id");
            Long parentId = rs.getLong("parent_id");
            String contents = rs.getString("comments");
            LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();

            Board board = Board.builder()
                    .id(boardId)
                    .build();

            Member member = Member.builder()
                    .id(memberId)
                    .build();

            Comments commentsParent = parentId != null ? Comments.builder()
                    .id(parentId)
                    .build() : null;

            return Comments.builder()
                    .id(id)
                    .board(board)
                    .member(member)
                    .comments(commentsParent)
                    .contents(contents)
                    .createdAt(createdAt)
                    .build();
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

            Board board = Board.builder()
                    .id(boardId)
                    .build();

            Member member = Member.builder()
                    .id(memberId)
                    .name(name)
                    .build();

            Comments commentsParent = parentId != null ? Comments.builder()
                    .id(parentId)
                    .build() : null;

            return Comments.builder()
                    .id(id)
                    .board(board)
                    .member(member)
                    .comments(commentsParent)
                    .contents(contents)
                    .createdAt(createdAt)
                    .build();
        });
    }
}
