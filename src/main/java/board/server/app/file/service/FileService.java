package board.server.app.file.service;

import board.server.app.board.entity.Board;
import board.server.app.file.dto.FileResponseDto;
import board.server.app.file.entity.FileEntity;
import board.server.app.file.repository.CustomFileRepository;
import board.server.app.file.repository.FileRepository;
import board.server.app.member.entity.Member;
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
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@Getter
public class FileService {
//    @Value("${images.upload.directory}")
//    private String uploadDirectory;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private CustomFileRepository customFileRepository;


    // 파일 작성
    public void upload(List<MultipartFile> multipartFileList, Long boardId, String username) throws IOException {

        validateFilesType(multipartFileList);

        List<FileEntity> fileEntityList = new ArrayList<>();

        for(MultipartFile multipartFile: multipartFileList){
            // DB 저장 - 파일 정보

            String originalFilename = multipartFile.getOriginalFilename();
            String currentFilename = createCurrentFilename(boardId, originalFilename);

            log.info(originalFilename, currentFilename);

            Board board = Board.builder().id(boardId).build();
            FileEntity fileEntity = FileEntity.builder()
                    .data(multipartFile.getBytes())
                    .board(board)
                    .originalFilename(originalFilename)
                    .currentFilename(currentFilename)
                    .build();

            fileEntityList.add(fileEntity);


            // 서버 저장 - 파일
            /*
            String memberUploadDirectory = uploadDirectory + File.separator + username;
            File file = new File(memberUploadDirectory);

            if(!file.exists()){
                file.mkdir();
            }


            String path = getMemberUploadPath(username, currentFilename);
            Files.copy(multipartFile.getInputStream(), Path.of(path));

            */
        }

        customFileRepository.saveAll(fileEntityList);
    }

    // 게시글에 해당하는 모든 파일 읽기
    public List<FileResponseDto> readAll(Long boardId, String username) throws IOException {
        List<FileEntity> fileEntityList = fileRepository.findByBoard_Id(boardId);

        List<FileResponseDto> fileResponseDtoList = fileEntityList.stream()
                .map(FileResponseDto::of)
                .collect(Collectors.toList());

        /*
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
        */

        return fileResponseDtoList;
    }

    // 게시글에 해당하는 파일 1개씩 읽기
    public List<FileResponseDto> read(List<Long> boardIdList, List<String> usernameList){
        if(boardIdList.isEmpty()) return new ArrayList<>();

        return fileRepository.findFirstImageByBoardIdIn(boardIdList).stream().map(FileResponseDto::of).toList();

        /*
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
       */
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
            String currentFilename = createCurrentFilename(boardId, originalFilename);
            String duplicatedFilename = findDuplicateFile(originalFilename, beforeFilenameList);

            // 이미 존재하는 파일이면 삭제하면 안됨 (삭제 리스트에서 제거)
            if(!duplicatedFilename.equals("")){
                beforeFilenameList = beforeFilenameList.stream().filter(filename -> !filename.equals(duplicatedFilename)).toList();
                continue;
            }

            // 존재하지 않는 파일 (파일을 생성)
            // 파일 생성 로직
            // DB 저장 - 파일 정보
            /*
            String memberUploadDirectory = uploadDirectory + File.separator + username;
            File file = new File(memberUploadDirectory);

            if(!file.exists()){
                file.mkdir();
            }


            String path = getMemberUploadPath(username, currentFilename);
            log.info("파일 생성 " + currentFilename, originalFilename);


            // 서버 저장 - 파일
            Files.copy(multipartFile.getInputStream(), Path.of(path));

            */

            Board board = Board.builder().id(boardId).build();
            FileEntity fileEntity = FileEntity.builder()
                    .currentFilename(currentFilename)
                    .originalFilename(originalFilename)
                    .board(board)
                    .data(multipartFile.getBytes())
                    .build();

            uploadFileList.add(fileEntity);
        }

        // 파일 생성
        if(!uploadFileList.isEmpty())
            customFileRepository.saveAll(uploadFileList);


        // 파일 삭제
        // DB 삭제
        if(!beforeFilenameList.isEmpty())
            fileRepository.deleteByCurrentFilenameIn(beforeFilenameList);


        // 서버 삭제
        /*
        for(String removeFileName : beforeFilenameList){
            String path = getMemberUploadPath(username, removeFileName);

            Files.delete(Path.of(path));
            log.info("파일 삭제 " + removeFileName);
        }
        */

        return boardId;
    }

    // 게시글에 해당하는 모든파일 삭제
    public void delete(String username, Long boardId) throws IOException {
        fileRepository.deleteByBoard_Id(boardId);

        /*
        String dir = uploadDirectory + File.separator + username;

        for (Path path : Files.newDirectoryStream(Path.of(dir), boardId + "_*")) {
            log.info("삭제: " + path.getFileName());

            // 삭제로직 추가
            Files.delete(path);
        }
        */
    }

    // 파일 타입 확인
    private void validateFilesType(List<MultipartFile> multipartFileList) {
        for(MultipartFile multipartFile: multipartFileList){
            String filename = multipartFile.getOriginalFilename();

            if(!(filename.toLowerCase().endsWith(".png") || filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".jpeg") || filename.toLowerCase().endsWith(".heif"))){
                throw new BusinessLogicException(CommonExceptionCode.FILE_TYPE_NOT_VALID);
            }
        }
    }

    /*

        배포환경에서는 서버 측에 이미지를 저장하지 않음
        현재 RDBMS인 mysql에 Blob(16mb) 타입(바이트 단위)으로 저장

     */
    // 파일 업로드 경로
//    private String getMemberUploadPath(String username, String filename) {
//        return uploadDirectory + File.separator + username + File.separator + filename;
//    }

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
    // beforeFilename은 unique하기 때문에 (beforeFilename, originalFilename) 또한 Unique함
    // 기존에 작성한 파일은 리스트에서 삭제 (이후 리스트에 남아있는 beforeFilename은 제거)
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
