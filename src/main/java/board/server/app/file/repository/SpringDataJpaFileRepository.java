package board.server.app.file.repository;

import board.server.app.file.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataJpaFileRepository extends JpaRepository<FileEntity, Long>, FileRepository {
    // 게시글 별 대표이미지
    @Override
    @Query("select file from FileEntity file where file.id in (" +
                "select min(f.id) id from FileEntity f where f.board.id in :boardIdList group by f.board.id )")
    List<FileEntity> findFirstImageByBoardIdIn(@Param("boardIdList") List<Long> boardIdList);
}
