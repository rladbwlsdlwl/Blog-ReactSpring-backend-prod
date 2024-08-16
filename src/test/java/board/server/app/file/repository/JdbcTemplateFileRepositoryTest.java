package board.server.app.file.repository;

import board.server.app.file.entity.FileEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class JdbcTemplateFileRepositoryTest {

    @Autowired
    JdbcTemplateFileRepository jdbcTemplateFileRepository;

    @Test
    void save() {
        // GIVEN
        Long postId = 61L;
        FileEntity fileEntity1 = FileEntity.builder()
                .postId(postId)
                .originalFilename("hello")
                .currentFilename("world")
                .build();

        FileEntity fileEntity2 = FileEntity.builder()
                .postId(postId)
                .originalFilename("hello1")
                .currentFilename("world1")
                .build();

        List<FileEntity> fileEntityList = new ArrayList<>();
        fileEntityList.add(fileEntity1);
        fileEntityList.add(fileEntity2);

        // WHEN
        List<FileEntity> fileEntities = jdbcTemplateFileRepository.saveAll(fileEntityList);


        // THEN
        for (FileEntity fileEntity : fileEntities){
            Assertions.assertEquals(postId, fileEntity.getPostId(), "error - not equal post id");
            Assertions.assertNotEquals(null, fileEntity.getId());
        }

    }

    @Test
    void findByPostId() {



    }
}