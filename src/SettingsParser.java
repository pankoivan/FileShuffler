import exceptions.SettingsParsingException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class SettingsParser {

    private static final String SETTINGS = "Settings.txt";

    private final String sourceDir;

    private final String destDir;

    private final int shufflesCount;

    private boolean isWithExtensions;

    private boolean isExcluded;

    private List<String> extensions;

    public SettingsParser() throws SettingsParsingException {
        this(SETTINGS);
    }

    public SettingsParser(String path) throws SettingsParsingException {
        try {
            List<String> lines = getProcessedSettingsLines(path);

            sourceDir = lines.get(0);
            destDir = lines.get(1);
            shufflesCount = Integer.parseInt(lines.get(2));

            if (lines.size() == 5) {
                isWithExtensions = true;
                isExcluded = getExtensionsStatus(lines.get(3));
                extensions = getProcessedExtensions(lines.get(4));
            }

        } catch (IOException | NullPointerException | NumberFormatException e) {
            throw new SettingsParsingException("Settings cannot be created because of errors in settings file " +
                    "\"" + path + "\"", e);
        }
    }

    private List<String> getProcessedSettingsLines(String path) throws IOException {
        return Files.readAllLines(Paths.get(path))
                .stream()
                .filter(line -> !line.isBlank())
                .map(String::trim)
                .toList();
    }

    private boolean getExtensionsStatus(String line) {
        return line.equalsIgnoreCase("excluded");
    }

    private List<String> getProcessedExtensions(String line) {
        return Stream.of(line.split(" "))
                .filter(format -> !format.isBlank())
                .map(String::toLowerCase)
                .toList();
    }

    public String getSourceDir() {
        return sourceDir;
    }

    public String getDestDir() {
        return destDir;
    }

    public int getShufflesCount() {
        return shufflesCount;
    }

    public boolean isWithExtensions() {
        return isWithExtensions;
    }

    public boolean isExcluded() {
        return isExcluded;
    }

    public List<String> getExtensions() {
        return extensions;
    }

}
