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

    private List<String> excludedFormats;

    public SettingsParser() throws SettingsParsingException {
        this(SETTINGS);
    }

    public SettingsParser(String path) throws SettingsParsingException {
        try {
            List<String> lines = getProcessedSettingsLines(path);

            sourceDir = lines.get(0);
            destDir = lines.get(1);
            shufflesCount = Integer.parseInt(lines.get(2));

            if (lines.size() == 4) {
                excludedFormats = getProcessedExcludedFormats(lines.get(3));
            }

        } catch (IOException | NullPointerException | NumberFormatException e) {
            throw new SettingsParsingException("Settings cannot be created because of errors in settings file " +
                    "\"" + SETTINGS + "\"", e);
        }
    }

    private List<String> getProcessedSettingsLines(String path) throws IOException {
        return Files.readAllLines(Paths.get(path))
                .stream()
                .filter(line -> !line.isBlank())
                .map(String::trim)
                .toList();
    }

    private List<String> getProcessedExcludedFormats(String formats) {
        return Stream.of(formats.split(" "))
                .filter(format -> !format.isBlank())
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

    public List<String> getExcludedFormats() {
        return excludedFormats;
    }

}
