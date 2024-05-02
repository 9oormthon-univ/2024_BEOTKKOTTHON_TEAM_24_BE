package goorm.reinput.user.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum UserErrorCode {
    USER_NOT_FOUND("UserEx001", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    USER_PASSWORD_NOT_MATCH("UserEx002", "비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
