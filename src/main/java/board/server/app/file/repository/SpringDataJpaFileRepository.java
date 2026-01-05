package board.server.app.file.repository;

import board.server.app.file.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataJpaFileRepository extends JpaRepository<FileEntity, Long>, FileRepository {
    // 게시글 전체 이미지
    @Override
    @Query("select f from FileEntity f join fetch f.board where f.board.id = :boardId")
    List<FileEntity> findByBoard_IdWithBoard(@Param("boardId") Long id);

    // 게시글 별 대표이미지
    @Override
    @Query("select file from FileEntity file join fetch file.board where file.id in (" +
                "select min(f.id) id from FileEntity f where f.board.id in :boardIdList group by f.board.id )")
    List<FileEntity> findFirstImageByBoardIdInWithBoard(@Param("boardIdList") List<Long> boardIdList);
}
