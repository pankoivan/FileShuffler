package exceptions;

public class SettingsException extends Exception {
    public SettingsException() {
        super();
    }

    public SettingsException(String msg) {
        super(msg);
    }

    public SettingsException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
