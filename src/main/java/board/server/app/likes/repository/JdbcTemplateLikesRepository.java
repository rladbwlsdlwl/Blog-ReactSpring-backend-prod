package board.server.app.likes.repository;

import board.server.app.likes.entity.Likes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcTemplateLikesRepository implements LikesRepository{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Likes> findByPostId(Long postId) {
        String sql = "select * from LIKES_TABLE where board_id = ?";

        List<Likes> likesList = jdbcTemplate.query(sql, LikesMapper(), postId);

        return likesList;
    }

    @Override
    public Optional<Likes> findByPostIdAndAuthor(Long postId, Long author) {
        String sql = "select * from LIKES_TABLE where board_id = ? and member_id = ?";

        List<Likes> likesList = jdbcTemplate.query(sql, LikesMapper(), postId, author);

        return likesList.stream().findAny();
    }

    @Override
    public Likes save(Likes likes) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);

        simpleJdbcInsert.withTableName("LIKES_TABLE").usingGeneratedKeyColumns("id");

        Map<String, Object> param = new HashMap<>();
        param.put("board_id", likes.getPostId());
        param.put("member_id", likes.getAuthor());

        Number number = simpleJdbcInsert.executeAndReturnKey(param);

        likes.setId(number.longValue());

        return likes;
    }

    @Override
    public void delete(Likes likes) {
        String sql = "delete from LIKES_TABLE where member_id = ? and board_id = ?";

        jdbcTemplate.update(sql, likes.getAuthor(), likes.getPostId());
    }


    private RowMapper<Likes> LikesMapper(){
        return (rs, cnt) -> {
            Long id = rs.getLong("id");
            Long author = rs.getLong("member_id");
            Long postId = rs.getLong("board_id");

            Likes likes = Likes.builder()
                    .id(id)
                    .author(author)
                    .postId(postId)
                    .build();

            return likes;
        };
    }

}
