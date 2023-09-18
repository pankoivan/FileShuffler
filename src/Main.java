import exceptions.FileShufflingException;
import exceptions.SettingsParsingException;

public class Main {

    public static void main(String[] args) {
        try {
            new FileShuffler().shuffleFiles();
        } catch (FileShufflingException | SettingsParsingException e) {
            e.printStackTrace();
        }
    }

}
