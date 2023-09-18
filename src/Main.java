import exceptions.FileShufflingException;

public class Main {

    public static void main(String[] args) {
        try {
            FileShuffler fileShuffler = new FileShuffler("Settings.txt");
            fileShuffler.shuffleFiles();
        } catch (FileShufflingException e) {
            e.printStackTrace();
        }
    }
}
