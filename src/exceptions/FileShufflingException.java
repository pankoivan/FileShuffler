package exceptions;

public class FileShufflingException extends Exception {

    public FileShufflingException() {
        super();
    }

    public FileShufflingException(String msg) {
        super(msg);
    }

    public FileShufflingException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

}
