package board.server.app.file.controller;

import board.server.app.file.dto.FileResponseDto;
import board.server.app.file.entity.FileEntity;
import board.server.app.file.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/file/{boardId}")
    public ResponseEntity<?> uploadFiles(@PathVariable("username") String username,
                                         @PathVariable("boardId") Long boardId,
                                         @RequestParam(value = "file", required = false) List<MultipartFile> multipartFileList) throws IOException {
        // 회원 게시판 - 최초 게시글 작성
        List<MultipartFile> filelist = multipartFileList == null ? new ArrayList<>() : multipartFileList;
        List<FileEntity> fileEntities = fileService.upload(filelist, boardId, username);

        return ResponseEntity.status(HttpStatus.CREATED).body(fileEntities);
    }

    @GetMapping("/file/{boardId}")
    public ResponseEntity<List<FileResponseDto>> readFiles(@PathVariable("username") String username,
                                                     @PathVariable("boardId") Long boardId) throws IOException {
        // 회원 게시판 - id에 해당하는 파일 모두 읽어오기
        List<FileResponseDto> fileResponseDtoList = fileService.readAll(boardId, username);

        return ResponseEntity.status(HttpStatus.CREATED).body(fileResponseDtoList);
    }

    @GetMapping("/file")
    public ResponseEntity<List<FileResponseDto>> readFile(@PathVariable String username,
                                                    @RequestParam(name = "postIdList", required = false) List<Long> postIdList,
                                                    @RequestParam(name = "usernameList", required = false) List<String> usernameList){
        // 회원 게시판 - id에 해당하는 파일 1개씩 읽어오기
        // 홈 화면 - id에 해당하는 파일 1개씩 읽어오기
        usernameList = usernameList == null ? new ArrayList<>() : usernameList;
        postIdList = postIdList == null ? new ArrayList<>() : postIdList;



        List<FileResponseDto> fileEntityList = fileService.read(postIdList, usernameList);

        return ResponseEntity.status(HttpStatus.CREATED).body(fileEntityList);
    }

    @PatchMapping("/file/{boardId}")
    public ResponseEntity<?> changeFiles(@PathVariable("username") String username,
                                         @PathVariable("boardId") Long boardId,
                                         @RequestParam(value = "file", required = false) List<MultipartFile> multipartFileList,
                                         @RequestParam(value = "beforeFilenameList", required = false) List<MultipartFile> beforeFilenameList) throws IOException {
        // 회원 게시판 - 게시글 수정
        List<MultipartFile> filelist = multipartFileList == null ? new ArrayList<>() : multipartFileList;
        List<MultipartFile> beforefilelist = beforeFilenameList == null ? new ArrayList<>() : beforeFilenameList;

        List<String> beforeFilename = new ArrayList<>();
        for(MultipartFile filename : beforefilelist){
            beforeFilename.add(new String(filename.getBytes()));
        }

        fileService.update(beforeFilename, filelist, boardId, username);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}

