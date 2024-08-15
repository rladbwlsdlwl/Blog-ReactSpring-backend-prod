package board.server.app.file.repository;

import board.server.app.file.entity.FileEntity;

import java.util.List;
import java.util.Optional;

public interface FileRepository {
    public List<FileEntity> saveAll(List<FileEntity> fileEntities);
    public List<FileEntity> findByPostId(Long postId);
    public Optional<FileEntity> findByOriginalFilenameAndCurrentFilename(String originalFilename, String currentFilename);
    public void deleteAll(List<FileEntity> fileEntities);
}
