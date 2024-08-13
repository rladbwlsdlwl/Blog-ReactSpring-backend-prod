package board.server.app.file.repository;

import board.server.app.file.entity.FileEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class JdbcTemplateFileRepository implements FileRepository{
    @Autowired
    private JdbcTemplate jdbcTemplate;



    @Override
    public List<FileEntity> save(List<FileEntity> fileEntities) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);

        simpleJdbcInsert.withTableName("FILE_TABLE").usingGeneratedKeyColumns("id");


        for (FileEntity fileEntity : fileEntities) {
            Map<String, Object> params = new HashMap<>();

            params.put("board_id", fileEntity.getPostId());
            params.put("originalFilename", fileEntity.getOriginalFilename());
            params.put("currentFilename", fileEntity.getCurrentFilename());

            Number number = simpleJdbcInsert.executeAndReturnKey(new MapSqlParameterSource(params));

            fileEntity.setId(number.longValue());
        }


        return fileEntities;
    }

    @Override
    public List<FileEntity> findByPostId(Long postId) {
        String sql = "select * from FILE_TABLE where board_id = ?";

        List<FileEntity> query = jdbcTemplate.query(sql, FileMapper(), postId);

        return query;
    }

    private RowMapper<FileEntity> FileMapper() {
        return (rs, rowNum) -> {
            Long id = rs.getLong("id");
            Long postId = rs.getLong("board_id");
            String originalFilename = rs.getString("originalFilename");
            String currentFilename = rs.getString("currentFilename");

            FileEntity fileEntity = FileEntity.builder()
                    .id(id)
                    .postId(postId)
                    .originalFilename(originalFilename)
                    .currentFilename(currentFilename)
                    .build();

            return fileEntity;
        };
    }
}
