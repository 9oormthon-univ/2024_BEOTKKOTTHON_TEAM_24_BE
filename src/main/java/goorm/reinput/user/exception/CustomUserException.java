package goorm.reinput.user.exception;

public class CustomUserException extends RuntimeException {
    private final UserErrorCode errorCode;

    public CustomUserException(UserErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
