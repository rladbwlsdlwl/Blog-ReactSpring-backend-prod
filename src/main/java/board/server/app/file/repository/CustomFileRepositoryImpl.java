package board.server.app.file.repository;

import board.server.app.file.entity.FileEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class CustomFileRepositoryImpl implements CustomFileRepository{

    @Autowired
    private JdbcTemplate jdbcTemplate;


    // 쿼리 최적화 - 배치 업데이트
    @Override
    public List<FileEntity> saveAll(List<FileEntity> fileEntities) {
        String sql = "insert into FILE_TABLE(original_filename, current_filename, data, board_id) values (?, ?, ?, ?)";

        List<Object[]> batchQuery = fileEntities.stream().map(file -> new Object[]{
                file.getOriginalFilename(),
                file.getCurrentFilename(),
                file.getData(),
                file.getBoard().getId()
        }).collect(Collectors.toList());

        jdbcTemplate.batchUpdate(sql, batchQuery);

        // id는 초기화되지 않음
        return fileEntities;
    }
}
