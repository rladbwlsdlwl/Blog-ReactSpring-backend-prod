package board.server.app.file.service;

import board.server.app.board.entity.Board;
import board.server.app.board.repository.BoardRepository;
import board.server.app.file.dto.FileResponseDto;
import board.server.app.file.entity.FileEntity;
import board.server.app.file.repository.CustomFileRepository;
import board.server.app.file.repository.FileRepository;
import board.server.app.member.entity.Member;
import board.server.error.errorcode.CommonExceptionCode;
import board.server.error.errorcode.CustomExceptionCode;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@Getter
public class FileService {
    @Value("${images.upload.directory}")
    private String uploadDirectory;
    @Value("${images.server.link}")
    private String linkServer;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private CustomFileRepository customFileRepository;
    @Autowired
    private BoardRepository boardRepository;


    // 파일 작성
    public void upload(List<MultipartFile> multipartFileList, Long boardId, Long memberId) throws IOException {

        validateFilesType(multipartFileList);
        validateBoardIdAndAuthor(boardId, memberId);

        List<FileEntity> fileEntityList = new ArrayList<>();

        for(MultipartFile multipartFile: multipartFileList){
            // DB 저장 - 파일 정보

            String originalFilename = multipartFile.getOriginalFilename();
            String currentFilename = createCurrentFilename(boardId, originalFilename);

            log.info(originalFilename, currentFilename);

            Board board = Board.builder().id(boardId).build();
            FileEntity fileEntity = FileEntity.builder()
                    .board(board)
                    .originalFilename(originalFilename)
                    .currentFilename(currentFilename)
                    .build();

            fileEntityList.add(fileEntity);


            // 서버 저장 - 파일
            String pathDir = getUploadMemberDir(memberId);
            File file = new File(pathDir);

            if (!file.exists()) {
                file.mkdir();
            }


            String path = getUploadMemberPath(memberId, currentFilename);
            Files.copy(multipartFile.getInputStream(), Path.of(path));
        }

        customFileRepository.saveAll(fileEntityList);
    }

    // 게시글에 해당하는 모든 파일 읽기
    public List<FileResponseDto> readAll(Long boardId) throws IOException {
        List<FileResponseDto> fileList = fileRepository.findByBoard_IdWithBoard(boardId).stream()
                .map(fileEntity -> {
                    FileResponseDto fileResponseDto = FileResponseDto.of(fileEntity);

                    //setter url
                    Long memberId = fileEntity.getBoard().getMember().getId();
                    String url = getFileReadPath(memberId, fileResponseDto.getCurrentFilename());
                    fileResponseDto.setFile(url);

                    return fileResponseDto;
                }).toList();

        return fileList;
    }

    // 게시글에 해당하는 파일 1개씩 읽기
    public List<FileResponseDto> read(List<Long> boardIdList){
        if(boardIdList.isEmpty()) return new ArrayList<>();


        List<FileResponseDto> fileList = fileRepository.findFirstImageByBoardIdInWithBoard(boardIdList).stream()
                .map(fileEntity -> {
                    // init
                    FileResponseDto fileResponseDto = FileResponseDto.of(fileEntity);

                    // setter url
                    Long memberId = fileEntity.getBoard().getMember().getId();
                    String url = getFileReadPath(memberId, fileResponseDto.getCurrentFilename());
                    fileResponseDto.setFile(url);

                    return fileResponseDto;
                }).toList();


        return fileList;
    }

    // 파일 수정
    // beforeFilenameList: 변경 전, 파일 업데이트 (비교 후 없어진 파일 삭제)
    // afterFileList: 새로 추가된 파일 (생성)
    @Transactional
    public Long update(List<String> beforeFilenameList, List<MultipartFile> afterFileList, Long boardId, Long userId) throws IOException {

        validateFilesType(afterFileList);
        validateBoardIdAndAuthor(boardId, userId);

        List<FileEntity> fileEntityList = fileRepository.findByBoard_Id(boardId);

        List<FileEntity> uploadFileList = new ArrayList<>();
       for(MultipartFile multipartFile: afterFileList){
           String dir = getUploadMemberDir(userId);
           File file = new File(dir);

           if(!file.exists()){
               file.mkdir();
           }

           // 서버 파일 생성
           String originalFilename = multipartFile.getOriginalFilename();
           String currentFilename = createCurrentFilename(boardId, originalFilename);
           String path = getUploadMemberPath(userId, currentFilename);

           Files.copy(multipartFile.getInputStream(), Path.of(path));

           // DB 파일 생성
           Board board = Board.builder().id(boardId).build();

           FileEntity fileEntity = FileEntity.builder()
                   .currentFilename(currentFilename)
                   .originalFilename(originalFilename)
                   .board(board)
                   .build();

           uploadFileList.add(fileEntity);
       }


        // 파일 생성
        if(!uploadFileList.isEmpty())
            customFileRepository.saveAll(uploadFileList);


        // 파일 삭제
        // DB 삭제
        // 기존 파일 리스트
        List<String> removeFileList = new ArrayList<>();
        for(FileEntity fileEntity: fileEntityList){
            String currentFilename = fileEntity.getCurrentFilename();

            boolean stopped = false;
            for(String filename: beforeFilenameList){
                if(filename.equals(currentFilename)){
                    stopped = true;
                    break;
                }
            }

            if(!stopped) removeFileList.add(currentFilename);
        }


        if(!removeFileList.isEmpty())
            fileRepository.deleteByCurrentFilenameIn(removeFileList);


        // 서버 삭제
        for(String removeFileName : removeFileList){
            String path = getUploadMemberPath(userId, removeFileName);

            Files.delete(Path.of(path));
            log.info("파일 삭제 " + removeFileName);
        }

        return boardId;
    }

    // 게시글 삭제
    // 게시글에 해당하는 모든파일 삭제
    public void delete(Long userId, Long boardId) throws IOException {
        validateBoardIdAndAuthor(boardId, userId);

        fileRepository.deleteByBoard_Id(boardId);

        String pathDir = getUploadMemberDir(userId);

        for (Path path : Files.newDirectoryStream(Path.of(pathDir), boardId + "_*")) {
            log.info("삭제: " + path.getFileName());

            // 삭제로직 추가
            Files.delete(path);
        }
    }



// 파일 레포지토리 마이그레이션
    /*
    public void migrationImage(){
        try{
            migrationImageFromMysqlToServer();
        }catch(IOException e){
            log.warn("파일 작성 실패! " + e.getStackTrace());
        }catch(Exception e){
            log.warn("파일 작성 실패! " + e.getStackTrace());
        }
    }
     */

    /*
    private void migrationImageFromMysqlToServer() throws IOException {
        // 모든 게시글 id 리스트
        List<Board> boardList = boardRepository.findTop10ByOrderByCreatedAtDesc();

        // 게시글 id에 대한 파일 리스트 찾아 파일 생성하기
        for (Board board: boardList){
            List<FileEntity> fileList = fileRepository.findByBoard_Id(board.getId());

            for(FileEntity fileEntity: fileList){
                // 파일 경로
                // uploads/{username}/{currentFilename}.png

                String pathDir = getUploadMemberDir(fileEntity.getBoard().getMember().getName());
                File file = new File(pathDir);
                if(!file.exists()){
                    file.mkdirs();
                }

                String path = getUploadMemberPath(fileEntity.getBoard().getMember().getName(), fileEntity.getCurrentFilename());

                Files.write(Path.of(path), fileEntity.getData());
            }
        }
    }
     */


    // 파일 타입 확인
    private void validateFilesType(List<MultipartFile> multipartFileList) {
        for(MultipartFile multipartFile: multipartFileList){
            String filename = multipartFile.getOriginalFilename();

            if(!(filename.toLowerCase().endsWith(".png") || filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".jpeg") || filename.toLowerCase().endsWith(".heif"))){
                throw new BusinessLogicException(CommonExceptionCode.FILE_TYPE_NOT_VALID);
            }
        }
    }
    // 게시글 존재 여부
    // 게시글 작성자와 로그인 유저 일치 여부
    private void validateBoardIdAndAuthor(Long boardId, Long memberId) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new BusinessLogicException(CustomExceptionCode.BOARD_NOT_FOUND));

        if(board.getMember().getId() != memberId)
            throw new BusinessLogicException(CommonExceptionCode.FORBIDDEN);
    }

    // 파일 업로드 디렉토리 경로
    // uploads/{user_id}
    private String getUploadMemberDir(Long member_id) {
        return uploadDirectory + File.separator + member_id;
    }

    // 파일 업로드 경로
    // uploads/{user_id}/{currentFilename}
    private String getUploadMemberPath(Long member_id, String filename) {
        return getUploadMemberDir(member_id) + File.separator + filename;
    }

    // 파일 READ 경로
    private String getFileReadPath(Long member_id, String filename){
        return linkServer + '/' + member_id + '/' + filename;
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
    // DB에 이미 저장한 파일인지 체크
    // 이미 존재하는 파일이면 currentFilename 새롭게 추가한 파일이면 "" 반환
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
