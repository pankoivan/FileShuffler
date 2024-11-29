package exceptions;

public class SettingsParserException extends Exception {

    public SettingsParserException() {
        super();
    }

    public SettingsParserException(String msg) {
        super(msg);
    }

    public SettingsParserException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
