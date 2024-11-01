package board.server.app.file.service;

import board.server.app.file.dto.FileResponseDto;
import board.server.app.file.entity.FileEntity;
import board.server.app.file.repository.FileRepository;
import board.server.error.errorcode.CommonExceptionCode;
import board.server.error.exception.BusinessLogicException;
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
    private FileRepository fileRepository;

    // 파일 작성
    public List<FileEntity> upload(List<MultipartFile> multipartFileList, Long boardId, String username) throws IOException {

        validateFilesType(multipartFileList);

        List<FileEntity> fileEntityList = new ArrayList<>();

        for(MultipartFile multipartFile: multipartFileList){
            // DB 저장 - 파일 정보

            String originalFilename = multipartFile.getOriginalFilename();
            String currentFilename = createCurrentFilename(boardId, originalFilename);

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


            String path = getMemberUploadPath(username, currentFilename);
            Files.copy(multipartFile.getInputStream(), Path.of(path));
        }

        List<FileEntity> fileEntities = fileRepository.saveAll(fileEntityList);

        return fileEntities;
    }

    // 게시글에 해당하는 모든 파일 읽기
    public List<FileResponseDto> readAll(Long boardId, String username) throws IOException {
        List<FileEntity> fileEntityList = fileRepository.findByPostId(boardId);


        List<FileResponseDto> fileResponseDtoList = new ArrayList<>();
        for (FileEntity fileEntity : fileEntityList) {
            String originalFilename = fileEntity.getOriginalFilename();
            String filename = fileEntity.getCurrentFilename();

            String path = getMemberUploadPath(username, filename);
            byte[] bytes = Files.readAllBytes(Path.of(path));


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

    // 게시글에 해당하는 파일 1개씩 읽기
    public List<FileResponseDto> read(List<Long> boardIdList, List<String> usernameList){
        List<FileResponseDto> fileResponseDtoList = new ArrayList<>();

        for(var i = 0; i < boardIdList.size(); i++){
            Long boardId = boardIdList.get(i);
            String username = usernameList.get(i);

            fileRepository.findByPostIdOne(boardId).ifPresent(fileEntity -> {
                String originalFilename = fileEntity.getOriginalFilename();
                String currentFilename = fileEntity.getCurrentFilename();
                String path = getMemberUploadPath(username, currentFilename);

                // 파일값 읽어오기
                try {
                    byte[] bytes = Files.readAllBytes(Path.of(path));

                    FileResponseDto fileResponseDto = FileResponseDto.builder()
                            .file(bytes)
                            .currentFilename(currentFilename)
                            .originalFilename(originalFilename)
                            .postId(boardId)
                            .build();


                    fileResponseDtoList.add(fileResponseDto);

                } catch (IOException e) {
                    throw new BusinessLogicException(CommonExceptionCode.FILE_NOT_VALID);
                }

            });
        }

        return fileResponseDtoList;
    }

    // 파일 수정
    public Long update(List<String>beforeFilenameList, List<MultipartFile> afterFileList, Long boardId, String username) throws IOException {

        validateFilesType(afterFileList);

        List<FileEntity> uploadFileList = new ArrayList<>();

        for(MultipartFile multipartFile : afterFileList){

            // 포스팅 이전에 기록한 파일인지 확인
            // 이미 등록한 파일은 생성하지 않음
            // 중복 파일 허용
            // case
            // A B A 파일 업로드하는 상황, A B는 이미 업로드되어있는 상태
            // => A파일만 추가 업로드

            String originalFilename = multipartFile.getOriginalFilename();
            String duplicatedFilename = findDuplicateFile(originalFilename, beforeFilenameList);

            if(!duplicatedFilename.equals("")){
                beforeFilenameList = beforeFilenameList.stream().filter(filename -> !filename.equals(duplicatedFilename)).toList();
                continue;
            }

            // 파일 생성 로직
            // DB 저장 - 파일 정보
            String memberUploadDirectory = uploadDirectory + File.separator + username;
            File file = new File(memberUploadDirectory);

            if(!file.exists()){
                file.mkdir();
            }


            String currentFilename = createCurrentFilename(boardId, originalFilename);
            String path = getMemberUploadPath(username, currentFilename);
            log.info("파일 생성 " + currentFilename, originalFilename);


            FileEntity fileEntity = FileEntity.builder()
                    .currentFilename(currentFilename)
                    .originalFilename(originalFilename)
                    .postId(boardId)
                    .build();

            uploadFileList.add(fileEntity);



            // 서버 저장 - 파일
            Files.copy(multipartFile.getInputStream(), Path.of(path));
        }
        fileRepository.saveAll(uploadFileList);


        // 파일 삭제 로직
        // DB 삭제
        List<FileEntity> fileEntityList = beforeFilenameList.stream().map(filename -> FileEntity.builder().currentFilename(filename).build()).toList();
        fileRepository.deleteAll(fileEntityList);


        // 서버 삭제
        for(String removeFileName : beforeFilenameList){
            String path = getMemberUploadPath(username, removeFileName);

            Files.delete(Path.of(path));
            log.info("파일 삭제 " + removeFileName);
        }


        return boardId;
    }

    // 게시글에 해당하는 모든파일 삭제
    // DB 제약조건에 의해 파일 자동 삭제 (서버에 저장된 파일 삭제)
    public void delete(String username, Long boardId) throws IOException {
        String dir = uploadDirectory + File.separator + username;

        for (Path path : Files.newDirectoryStream(Path.of(dir), boardId + "_*")) {
            log.info("삭제: " + path.getFileName());

            // 삭제로직 추가
            Files.delete(path);
        }

    }

    // 파일 타입 확인
    private void validateFilesType(List<MultipartFile> multipartFileList) {
        for(MultipartFile multipartFile: multipartFileList){
            String filename = multipartFile.getOriginalFilename();

            if(!(filename.endsWith(".png") || filename.endsWith(".jpg") || filename.endsWith(".jpeg") || filename.endsWith(".heif"))){
                throw new BusinessLogicException(CommonExceptionCode.FILE_TYPE_NOT_VALID);
            }
        }
    }

    // 파일 업로드 경로
    private String getMemberUploadPath(String username, String filename) {
        return uploadDirectory + File.separator + username + File.separator + filename;
    }

    // 파일 이름
    private String createCurrentFilename(Long boardId, String originalFilename) {
        /*
                current file name =>
                boardId_UUID.확장자
         */
        String uid = UUID.randomUUID().toString();
        return boardId.toString() + "_" + uid.concat(originalFilename.substring(originalFilename.indexOf(".")));
    }

    // 중복 파일 검증
    private String findDuplicateFile(String originalFilename, List<String> beforeFilenameList) {

        for(String filename : beforeFilenameList){
            boolean duplicatedFile = fileRepository.findByOriginalFilenameAndCurrentFilename(originalFilename, filename).isPresent();

            if(duplicatedFile){
                return filename;
            }
        }

        return "";
    }


}
