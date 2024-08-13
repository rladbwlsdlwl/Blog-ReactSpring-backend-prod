package board.server.app.file.controller;

import board.server.app.file.dto.FileRequestDto;
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
                                         @RequestParam("file") List<MultipartFile> multipartFileList) throws IOException {
        List<FileEntity> fileEntities = fileService.upload(multipartFileList, boardId, username);

        return ResponseEntity.status(HttpStatus.CREATED).body(fileEntities);
    }

    @GetMapping("/file/{boardId}")
    public ResponseEntity<List<FileResponseDto>> readFiles(@PathVariable("username") String username,
                                                     @PathVariable("boardId") Long boardId) throws IOException {

        List<FileResponseDto> fileResponseDtoList = fileService.read(boardId, username);

        return ResponseEntity.status(HttpStatus.CREATED).body(fileResponseDtoList);
    }

    @PatchMapping("/file/{boardId}")
    public ResponseEntity<?> changeFiles(@PathVariable("username") String username,
                                         @PathVariable("boardId") Long boardId,
                                         @RequestParam("file") List<MultipartFile> multipartFileList,
                                         @RequestParam("duplicateFilename") List<MultipartFile> filenameList) throws IOException {

//        for(MultipartFile filename : filenameList){
//            String currentFilename = new String(filename.getBytes());
//            log.info(currentFilename);
//        }
//
//
//        for(MultipartFile file : multipartFileList){
//            log.info(file.getOriginalFilename());
//        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}

