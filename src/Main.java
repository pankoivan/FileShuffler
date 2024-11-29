import exceptions.FileShufflerException;
import exceptions.SettingsParserException;

public class Main {

    public static void main(String[] args) {
        try {
            new FileShuffler().shuffleFiles();
        } catch (SettingsParserException | FileShufflerException e) {
            e.printStackTrace();
        }
    }

}
