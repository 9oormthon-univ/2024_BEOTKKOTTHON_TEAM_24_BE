package goorm.reinput.global.controller;

import goorm.reinput.folder.exception.CustomFolderException;
import goorm.reinput.folder.exception.FolderErrorCode;
import goorm.reinput.global.domain.dto.ErrorResponseDto;
import goorm.reinput.user.exception.CustomUserException;
import goorm.reinput.user.exception.UserErrorCode;
import jdk.jshell.spi.ExecutionControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalControllerAdvice extends ResponseEntityExceptionHandler {
    @ExceptionHandler(CustomUserException.class)
    public ResponseEntity<ErrorResponseDto> handleUserException(CustomUserException e) {
        final UserErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.badRequest().body(new ErrorResponseDto(errorCode.getCode(), errorCode.getMessage()));
    }

    @ExceptionHandler(CustomFolderException.class)
    public ResponseEntity<ErrorResponseDto> handleFolderException(CustomFolderException e) {
        final FolderErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.badRequest().body(new ErrorResponseDto(errorCode.getCode(), errorCode.getMessage()));
    }

}
