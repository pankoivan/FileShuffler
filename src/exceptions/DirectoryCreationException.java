package exceptions;

public class DirectoryCreationException extends Exception {
    public DirectoryCreationException() {
        super();
    }

    public DirectoryCreationException(String msg) {
        super(msg);
    }
}
