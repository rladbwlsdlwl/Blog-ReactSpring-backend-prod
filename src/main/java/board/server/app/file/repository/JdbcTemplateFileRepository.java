package board.server.app.file.repository;

import board.server.app.board.entity.Board;
import board.server.app.file.entity.FileEntity;
import board.server.app.member.entity.Member;
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
    public List<FileEntity> findByBoard_Id(Long boardId) {
        String sql = "select * from file_table where board_id = ?";

        List<FileEntity> query = jdbcTemplate.query(sql, FileMapper(), boardId);

        return query;
    }

    @Override
    public List<FileEntity> findByBoard_IdWithBoard(Long boardId) {
        String sql = "select f.id as id, current_filename, original_filename, f.board_id as board_id, b.member_id as member_id" +
                " from file_table f join board_table b on f.board_id = b.id where board_id = ?";

        List<FileEntity> query = jdbcTemplate.query(sql, FileWithBoardMapper(), boardId);

        return query;
    }

    @Override
    public Optional<FileEntity> findTop1ByBoard_Id(Long postId) {
        String sql = "select * from file_table where board_id = ? limit 1";

        return jdbcTemplate.query(sql, FileMapper(), postId).stream().findAny();
    }

    @Override
    public List<FileEntity> findFirstImageByBoardIdInWithBoard(List<Long> boardIdList) {
        String sqlfilter = "select min(id) as id from file_table where board_id in (FILTER) group by board_id";
        String sql = "select f.id as id, current_filename, original_filename, f.board_id as board_id, b.member_id as member_id" +
                " from file_table f join board_table b on f.board_id = b.id where f.id in (" + sqlfilter + ")";


        sql = sql.replace("FILTER", boardIdList.stream()
                .map(id -> "?")
                .collect(Collectors.joining(", ")));

        List<FileEntity> fileList = jdbcTemplate.query(sql, FileWithBoardMapper(), boardIdList.toArray());


        return fileList;
    }

    @Override
    public Optional<FileEntity> findByOriginalFilenameAndCurrentFilename(String originalFilename, String currentFilename) {
        String sql = "select * from file_table where original_filename = ? and current_filename = ?";

        return jdbcTemplate.query(sql, FileMapper(), originalFilename, currentFilename).stream().findAny();
    }

    @Override
    public void deleteByCurrentFilenameIn(List<String> currentFilename) {
        String sql = "delete from file_table where current_filename in (FILTER)";

        sql = sql.replace("FILTER", currentFilename.stream()
                        .map(name -> "?")
                        .collect(Collectors.joining(", ")));

        jdbcTemplate.update(sql, currentFilename.toArray());
    }

    @Override
    public void deleteByBoard_Id(Long boardId) {
        String sql = "delete from file_table where board_id = ?";

        jdbcTemplate.update(sql, boardId);
    }


    private RowMapper<FileEntity> FileMapper() {
        return (rs, rowNum) -> {
            Long id = rs.getLong("id");
            Long postId = rs.getLong("board_id");
            String originalFilename = rs.getString("original_filename");
            String currentFilename = rs.getString("current_filename");

            Board board = Board.builder()
                    .id(postId)
                    .build();

            FileEntity fileEntity = FileEntity.builder()
                    .id(id)
                    .board(board)
                    .originalFilename(originalFilename)
                    .currentFilename(currentFilename)
                    .build();

            return fileEntity;
        };
    }

    private RowMapper<FileEntity> FileWithBoardMapper() {
        return (rs, rowNum) -> {
            Long id = rs.getLong("id");
            Long postId = rs.getLong("board_id");
            String originalFilename = rs.getString("original_filename");
            String currentFilename = rs.getString("current_filename");

            Long member_id = rs.getLong("member_id");

            Member member = Member.builder()
                    .id(member_id)
                    .build();

            Board board = Board.builder()
                    .id(postId)
                    .member(member)
                    .build();

            FileEntity fileEntity = FileEntity.builder()
                    .id(id)
                    .board(board)
                    .originalFilename(originalFilename)
                    .currentFilename(currentFilename)
                    .build();

            return fileEntity;
        };
    }
}
