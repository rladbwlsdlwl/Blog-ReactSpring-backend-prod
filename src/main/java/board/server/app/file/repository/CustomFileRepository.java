package board.server.app.file.repository;

import board.server.app.file.entity.FileEntity;

import java.util.List;

public interface CustomFileRepository {
    List<FileEntity> saveAll(List<FileEntity> fileEntities);
}
