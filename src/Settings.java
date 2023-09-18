import exceptions.SettingsException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Settings {

    private final String sourceDir;
    private final String destDir;
    private final int numberOfShuffles;

    public Settings(String file) throws SettingsException {
        try {

            List<String> lines = Files.readAllLines(Paths.get(file));
            sourceDir = lines.get(0);
            destDir = lines.get(1);
            numberOfShuffles = Integer.parseInt(lines.get(2));

        } catch (IOException | NullPointerException | NumberFormatException e) {
            throw new SettingsException("Settings cannot be created because of errors in settings file " +
                    "\"Settings.txt\"", e);
        }
    }

    public String getSourceDir() {
        return sourceDir;
    }

    public String getDestDir() {
        return destDir;
    }

    public int getNumberOfShuffles() {
        return numberOfShuffles;
    }
}
