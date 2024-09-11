package board.server.error.handler;

import board.server.app.board.service.BoardService;
import board.server.error.errorcode.CommonExceptionCode;
import board.server.error.errorcode.ExceptionCode;
import board.server.error.exception.BusinessLogicException;
import board.server.error.response.ResponseExceptionCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdvice{
    @Autowired
    private BoardService boardService;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        log.warn(e.getMessage());
        ExceptionCode exceptionCode = CommonExceptionCode.INVALID_PARAMETER;

        return handleExceptionInternal(exceptionCode, e);
    }

    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<?> handleBusinessLogicException(BusinessLogicException e){
        ExceptionCode exceptionCode = e.getExceptionCode();
        log.warn(exceptionCode.getMessage());

//        return new ResponseEntity<>(exceptionMapper(error), HttpStatus.valueOf(error.getStatus()));
        return handleExceptionInternal(exceptionCode);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> handleMaxUploadSizeExceedException(MaxUploadSizeExceededException e){
        ExceptionCode exceptionCode = CommonExceptionCode.FILE_SIZE_EXCEED;
        log.warn(exceptionCode.getMessage());


        return handleExceptionInternal(exceptionCode);
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<?> handleJsonProcessingException(JsonProcessingException e){
        log.warn(e.getMessage());
        ExceptionCode exceptionCode = CommonExceptionCode.JSON_PARSING_ERROR;

        return handleExceptionInternal(exceptionCode);
    }


    private ResponseEntity<Object> handleExceptionInternal(ExceptionCode exceptionCode) {
        return ResponseEntity.status(exceptionCode.getStatus()).body(exceptionMapper(exceptionCode));
    }

    private ResponseEntity<?> handleExceptionInternal(ExceptionCode exceptionCode, BindException e) {
        return ResponseEntity.status(exceptionCode.getStatus()).body(exceptionMapper(e.getFieldErrors()));
    }

    private Object exceptionMapper(List<FieldError> fieldErrors) {
        return fieldErrors.stream().map(err -> {
            return ResponseExceptionCode.ValidationError.builder()
                    .message(err.getDefaultMessage())
                    .field(err.getField())
                    .build();
        });
    }

    private ResponseExceptionCode exceptionMapper(ExceptionCode e) {
        return ResponseExceptionCode.builder()
                .status(e.getStatus())
                .message(e.getMessage())
                .build();
    }



}
