package board.server.app.file.service;

import board.server.app.file.dto.FileResponseDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;



@Transactional
@SpringBootTest
class FileServiceTest {

    @Autowired
    private FileService fileService;

    @Test
    void upload() {
//        fileService.upload()
    }

    @Test
    void 게시판아이디로이미지파일불러오기(){
        List<Long> boardIdList = new ArrayList<>();
        String username = "rladbwlsldlwl";

        // GIVEN
        for(Long i = 100L; i<110; i++){
            boardIdList.add(i);
        }

        //WHEN
        List<FileResponseDto> fileEntityList = fileService.read(boardIdList, username);


        // THEN
        int cnt = 0;
        for(FileResponseDto fileResponseDto : fileEntityList){
            cnt++;
        }

        Assertions.assertThat(cnt).isBetween(3, 5);
    }
}