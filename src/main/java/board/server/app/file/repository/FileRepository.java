package board.server.app.file.repository;

import board.server.app.file.entity.FileEntity;

import java.util.List;
import java.util.Optional;

public interface FileRepository {
    List<FileEntity> saveAll(List<FileEntity> fileEntities);
    List<FileEntity> findByBoard_Id(Long Id);
    Optional<FileEntity> findTop1ByBoard_Id(Long boardId);
    List<FileEntity> findFirstImageByBoardIdIn(List<Long> boardIdList);
    Optional<FileEntity> findByOriginalFilenameAndCurrentFilename(String originalFilename, String currentFilename);
    void deleteByCurrentFilenameIn(List<String> currentFilename);
    void deleteByBoard_Id(Long boardId);
}
