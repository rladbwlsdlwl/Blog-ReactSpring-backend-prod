package board.server.app.file.repository;

import board.server.app.file.entity.FileEntity;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//@Repository
public class JpaFileRepository implements FileRepository{

    @Autowired
    private EntityManager em;


    @Override
    public List<FileEntity> findByBoard_Id(Long id) {
        String sql = "select f from FileEntity f where f.board.id = :id";

        List<FileEntity> fileList = em.createQuery(sql, FileEntity.class)
                .setParameter("id", id)
                .getResultList();

        return fileList;
    }

    @Override
    public List<FileEntity> findByBoard_IdWithBoard(Long id) {
        String sql = "select f from FileEntity f join fetch f.board b where f.board.id = :id";

        List<FileEntity> fileList = em.createQuery(sql, FileEntity.class)
                .setParameter("id", id)
                .getResultList();

        return fileList;
    }

    @Override
    public Optional<FileEntity> findTop1ByBoard_Id(Long boardId) {
        String sql = "select f from FileEntity f where f.board.id = :id";

        List<FileEntity> fileList = em.createQuery(sql, FileEntity.class)
                .setParameter("id", boardId)
                .setFirstResult(0)
                .setMaxResults(1)
                .getResultList();

        return fileList.stream().findAny();
    }

    @Override
    public List<FileEntity> findFirstImageByBoardIdInWithBoard(List<Long> boardIdList) {
        // 게시글에 해당하는 파일 찾기
        // 파일을 게시글 별로 묶어서 대표이미지 1개씩 뽑기
        String sqlFilter = "select min(f.id) id from FileEntity f where f.board.id in :FILTER group by f.board.id";
        String sql = "select file from FileEntity file join fetch file.board where file.id in (" + sqlFilter + ")";


        List<FileEntity> fileList = em.createQuery(sql, FileEntity.class)
                .setParameter("FILTER", boardIdList)
                .getResultList();

        return fileList;
    }

    @Override
    public Optional<FileEntity> findByOriginalFilenameAndCurrentFilename(String originalFilename, String currentFilename) {
        String sql = "select f from FileEntity f where f.originalFilename = :originalFilename and f.currentFilename = :currentFilename";

        List<FileEntity> fileList = em.createQuery(sql, FileEntity.class)
                .setParameter("originalFilename", originalFilename)
                .setParameter("currentFilename", currentFilename)
                .getResultList();

        return fileList.stream().findAny();
    }

    @Override
    public void deleteByCurrentFilenameIn(List<String> currentFilename) {
        String sql = "delete from FileEntity f where f.currentFilename in :currentFilename";

        em.createQuery(sql)
                .setParameter("currentFilename", currentFilename)
                .executeUpdate();
    }

    @Override
    public void deleteByBoard_Id(Long boardId) {
        String sql = "delete from FileEntity f where f.board.id = :boardId";

        em.createQuery(sql)
                .setParameter("boardId", boardId)
                .executeUpdate();
    }
}
