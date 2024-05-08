package goorm.reinput.folder.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum FolderErrorCode {
    FOLDER_NOT_FOUND("FolderEx001", "폴더를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND("FolderEx002", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INSIGHT_NOT_FOUND_BY_FOLDER("FolderEx003", "폴더에 해당하는 인사이트를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
