package board.server.app.tags.repository;

import board.server.app.tags.entity.Tags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class CustomTagsRepositoryImpl implements CustomTagsRepository{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Tags> saveAll(List<Tags> tagsList) {
        String sql = "insert into tags_table(name) values (?)";

        List<Object[]> taglist = tagsList.stream().map(tags -> new Object[]{
                tags.getName()
        }).collect(Collectors.toList());


        jdbcTemplate.batchUpdate(sql, taglist);

        return tagsList;
    }
}
