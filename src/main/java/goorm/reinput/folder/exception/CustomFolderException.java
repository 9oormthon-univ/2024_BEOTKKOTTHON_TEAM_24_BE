package goorm.reinput.folder.exception;

public class CustomFolderException extends RuntimeException {
    private final FolderErrorCode errorCode;

    public CustomFolderException(FolderErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public FolderErrorCode getErrorCode() {
        return errorCode;
    }
}
