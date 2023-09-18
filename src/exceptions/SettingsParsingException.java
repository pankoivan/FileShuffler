package exceptions;

public class SettingsParsingException extends Exception {

    public SettingsParsingException() {
        super();
    }

    public SettingsParsingException(String msg) {
        super(msg);
    }

    public SettingsParsingException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

}
