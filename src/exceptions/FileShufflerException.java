package exceptions;

public class FileShufflerException extends Exception {

    public FileShufflerException() {
        super();
    }

    public FileShufflerException(String msg) {
        super(msg);
    }

    public FileShufflerException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
