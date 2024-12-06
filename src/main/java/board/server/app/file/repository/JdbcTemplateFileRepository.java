package board.server.app.file.repository;

import board.server.app.board.entity.Board;
import board.server.app.file.entity.FileEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.sql.Blob;
import java.util.*;
import java.util.stream.Collectors;

//@Repository
public class JdbcTemplateFileRepository implements FileRepository{
    @Autowired
    private JdbcTemplate jdbcTemplate;



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

    @Override
    public List<FileEntity> findByBoard_Id(Long boardId) {
        String sql = "select * from FILE_TABLE where board_id = ?";

        List<FileEntity> query = jdbcTemplate.query(sql, FileMapper(), boardId);

        return query;
    }

    @Override
    public Optional<FileEntity> findTop1ByBoard_Id(Long postId) {
        String sql = "select * from FILE_TABLE where board_id = ? limit 1";

        return jdbcTemplate.query(sql, FileMapper(), postId).stream().findAny();
    }

    @Override
    public List<FileEntity> findFirstImageByBoardIdIn(List<Long> boardIdList) {
        String sqlfilter = "select min(id) as id from FILE_TABLE where board_id in (FILTER) group by board_id";
        String sql = "select * from FILE_TABLE where id in (" + sqlfilter + ")";


        sql = sql.replace("FILTER", boardIdList.stream()
                .map(id -> "?")
                .collect(Collectors.joining(", ")));

        List<FileEntity> fileList = jdbcTemplate.query(sql, FileMapper(), boardIdList.toArray());


        return fileList;
    }

    @Override
    public Optional<FileEntity> findByOriginalFilenameAndCurrentFilename(String originalFilename, String currentFilename) {
        String sql = "select * from FILE_TABLE where original_filename = ? and current_filename = ?";

        return jdbcTemplate.query(sql, FileMapper(), originalFilename, currentFilename).stream().findAny();
    }

    @Override
    public void deleteByCurrentFilenameIn(List<String> currentFilename) {
        String sql = "delete from FILE_TABLE where current_filename in (FILTER)";

        sql = sql.replace("FILTER", currentFilename.stream()
                        .map(name -> "?")
                        .collect(Collectors.joining(", ")));

        jdbcTemplate.update(sql, currentFilename.toArray());
    }

    @Override
    public void deleteByBoard_Id(Long boardId) {
        String sql = "delete from FILE_TABLE where board_id = ?";

        jdbcTemplate.update(sql, boardId);
    }

    private RowMapper<FileEntity> FileMapper() {
        return (rs, rowNum) -> {
            Long id = rs.getLong("id");
            Long postId = rs.getLong("board_id");
            String originalFilename = rs.getString("original_filename");
            String currentFilename = rs.getString("current_filename");
            Blob blob = rs.getBlob("data");
            byte[] data = blob.getBytes(1, (int) blob.length());

            Board board = Board.builder()
                    .id(postId)
                    .build();

            FileEntity fileEntity = FileEntity.builder()
                    .id(id)
                    .data(data)
                    .board(board)
                    .originalFilename(originalFilename)
                    .currentFilename(currentFilename)
                    .build();

            return fileEntity;
        };
    }
}
