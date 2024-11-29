package board.server.app.likes.repository;

import board.server.app.board.entity.Board;
import board.server.app.likes.entity.Likes;
import board.server.app.member.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

//@Repository
public class JdbcTemplateLikesRepository implements LikesRepository{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Likes> findByBoard_Id(Long id) {
        String sql = "select * from LIKES_TABLE where board_id = ?";

        List<Likes> likesList = jdbcTemplate.query(sql, LikesMapper(), id);

        return likesList;
    }

    @Override
    public List<Likes> findByBoard_IdIn(List<Long> idList) {
        String sql = "select * from LIKES_TABLE where board_id in ";
        String sqlIdFilter = idList.stream().map(id -> "?").collect(Collectors.joining(", "));
        String SQL = sql + "(" + sqlIdFilter + ")";

        List<Likes> likesList = jdbcTemplate.query(SQL, LikesMapper(), idList.toArray());

        return likesList;
    }

    @Override
    public Optional<Likes> findByBoard_IdAndMember_Id(Long boardId, Long memberId) {
        String sql = "select * from LIKES_TABLE where board_id = ? and member_id = ?";

        List<Likes> likesList = jdbcTemplate.query(sql, LikesMapper(), boardId, memberId);

        return likesList.stream().findAny();
    }

    @Override
    public Likes save(Likes likes) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);

        simpleJdbcInsert.withTableName("LIKES_TABLE").usingGeneratedKeyColumns("id");

        Map<String, Object> param = new HashMap<>();
        param.put("board_id", likes.getBoard().getId());
        param.put("member_id", likes.getMember().getId());

        Number number = simpleJdbcInsert.executeAndReturnKey(param);

        likes.setId(number.longValue());

        return likes;
    }

    @Override
    public void delete(Likes likes) {
        String sql = "delete from LIKES_TABLE where member_id = ? and board_id = ?";

        jdbcTemplate.update(sql, likes.getMember().getId(), likes.getBoard().getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "delete from LIKES_TABLE where id = ?";

        jdbcTemplate.update(sql, id);
    }


    private RowMapper<Likes> LikesMapper(){
        return (rs, cnt) -> {
            Long id = rs.getLong("id");
            Long author = rs.getLong("member_id");
            Long postId = rs.getLong("board_id");

            Member member = Member.builder()
                    .id(author)
                    .build();
            Board board = Board.builder()
                    .id(postId)
                    .build();

            Likes likes = Likes.builder()
                    .id(id)
                    .member(member)
                    .board(board)
                    .build();

            return likes;
        };
    }

}
