package board.server.app.boardTags.repository;

import board.server.app.boardTags.entity.BoardTags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class CustomBoardTagsRepositoryImpl implements CustomBoardTagsRepository{
    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Override
    public List<BoardTags> saveAll(List<BoardTags> boardTags) {
        String sql = "insert into board_tags_table(board_id, tags_id) values (?, ?)";

        List<Object[]> updateList = boardTags.stream().map(bt -> new Object[]{
                bt.getBoard().getId(),
                bt.getTags().getId()
        }).collect(Collectors.toList());

        jdbcTemplate.batchUpdate(sql, updateList);

        return boardTags;
    }
}
