package board.server.app.file.service;

import board.server.app.file.dto.FileResponseDto;
import board.server.app.file.entity.FileEntity;
import board.server.app.file.repository.JdbcTemplateFileRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@Slf4j
@Getter
public class FileService {
    @Value("${images.upload.directory}")
    private String uploadDirectory;
    @Autowired
    private JdbcTemplateFileRepository jdbcTemplateFileRepository;

    public List<FileEntity> upload(List<MultipartFile> multipartFileList, Long boardId, String username) throws IOException {

        List<FileEntity> fileEntityList = new ArrayList<>();

        for(MultipartFile multipartFile: multipartFileList){
            /*
                current file name =>
                boardId_UUID.확장자
            */


            // DB 저장 - 파일 정보
            String uid = UUID.randomUUID().toString();

            String originalFilename = multipartFile.getOriginalFilename();
            String currentFilename = boardId.toString() + "_" + uid.concat(originalFilename.substring(originalFilename.indexOf(".")));


            log.info(originalFilename, currentFilename);
            FileEntity fileEntity = FileEntity.builder()
                    .postId(boardId)
                    .originalFilename(originalFilename)
                    .currentFilename(currentFilename)
                    .build();

            fileEntityList.add(fileEntity);


            // 서버 저장 - 파일
            String memberUploadDirectory = uploadDirectory + File.separator + username;
            File file = new File(memberUploadDirectory);

            if(!file.exists()){
                file.mkdir();
            }

            Files.copy(multipartFile.getInputStream(), Path.of(memberUploadDirectory + File.separator + currentFilename));
        }

        List<FileEntity> fileEntities = jdbcTemplateFileRepository.save(fileEntityList);

        return fileEntities;
    }

    public List<FileResponseDto> read(Long boardId, String username) throws IOException {
        List<FileEntity> fileEntityList = jdbcTemplateFileRepository.findByPostId(boardId);


        List<FileResponseDto> fileResponseDtoList = new ArrayList<>();
        for (FileEntity fileEntity : fileEntityList) {
            String filename = fileEntity.getCurrentFilename();

            String path = uploadDirectory + File.separator + username + File.separator + filename;
            byte[] bytes = Files.readAllBytes(Path.of(path));


            String originalFilename = fileEntity.getOriginalFilename();
            fileResponseDtoList.add(
                    FileResponseDto.builder()
                            .file(bytes)
                            .currentFilename(filename)
                            .originalFilename(originalFilename)
                            .build()
            );
        }


        return fileResponseDtoList;
    }

}
