package board.server.app.file.controller;

import board.server.app.file.dto.FileResponseDto;
import board.server.app.file.entity.FileEntity;
import board.server.app.file.service.FileService;
import board.server.config.jwt.CustomUserDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/{username}")
public class FileController {

    @Autowired
    public FileService fileService;

//    이미지 마이그레이션 - 1회성
//    운영자만 접근 가능

    @GetMapping("/file/restore")
    public ResponseEntity<?> restoreFileData(){
        // fileService.migrationImage();

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @PostMapping("/file/{boardId}")
    public ResponseEntity<?> uploadFiles(@PathVariable("username") String username,
                                         @PathVariable("boardId") Long boardId,
                                         @AuthenticationPrincipal CustomUserDetail userDetail,
                                         @RequestParam(value = "file", required = false) List<MultipartFile> multipartFileList) throws IOException {
        // 회원 게시판 - 최초 게시글 작성
        List<MultipartFile> filelist = multipartFileList == null ? new ArrayList<>() : multipartFileList;

        Long memberId = userDetail.getId();
        fileService.upload(filelist, boardId, memberId);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/file/{boardId}")
    public ResponseEntity<List<FileResponseDto>> readFiles(@PathVariable("username") String username,
                                                     @PathVariable("boardId") Long boardId) throws IOException {
        // 회원 게시판 - id에 해당하는 파일 모두 읽어오기
        List<FileResponseDto> fileResponseDtoList = fileService.readAll(boardId);

        return ResponseEntity.status(HttpStatus.CREATED).body(fileResponseDtoList);
    }

    @GetMapping("/file")
    public ResponseEntity<List<FileResponseDto>> readFile(@PathVariable String username,
                                                    @RequestParam(name = "postIdList", required = false) List<Long> postIdList){
        // 회원 게시판 - id에 해당하는 파일 1개씩 읽어오기
        // 홈 화면 - id에 해당하는 파일 1개씩 읽어오기
        postIdList = postIdList == null ? new ArrayList<>() : postIdList;



        List<FileResponseDto> fileEntityList = fileService.read(postIdList);

        return ResponseEntity.status(HttpStatus.CREATED).body(fileEntityList);
    }

    @PatchMapping("/file/{boardId}")
    public ResponseEntity<?> changeFiles(@PathVariable("username") String username,
                                         @PathVariable("boardId") Long boardId,
                                         @AuthenticationPrincipal CustomUserDetail userDetail,
                                         @RequestParam(value = "file", required = false) List<MultipartFile> multipartFileList,
                                         @RequestParam(value = "removeFilenameList", required = false) List<MultipartFile> removeFilenameList) throws IOException {
        // 회원 게시판 - 게시글 수정
        List<MultipartFile> filelist = multipartFileList == null ? new ArrayList<>() : multipartFileList;
        List<MultipartFile> removefilelist = removeFilenameList == null ? new ArrayList<>() : removeFilenameList;

        // filename은 currentFilename과 동일
        List<String> removeFilename = new ArrayList<>();
        for(MultipartFile filename : removefilelist){
            removeFilename.add(new String(filename.getBytes()));
        }

        fileService.update(removeFilename, filelist, boardId, userDetail.getId());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/file/{boardId}")
    public ResponseEntity<?> deleteFile(@PathVariable("username") String username,
                                        @PathVariable("boardId") Long boardId,
                                        @AuthenticationPrincipal CustomUserDetail userDetail) throws IOException {

        fileService.delete(userDetail.getId(), boardId);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

