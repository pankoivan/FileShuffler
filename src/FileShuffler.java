import exceptions.DirectoryCreationException;
import exceptions.FileShufflingException;
import exceptions.SettingsParsingException;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class FileShuffler {

    private final SettingsParser settingsParser;
    private final List<File> files;
    private final String formatPattern;

    public FileShuffler(String file) throws FileShufflingException {
        try {

            settingsParser = new SettingsParser(file);
            files = Arrays.asList(Objects.requireNonNull(new File(settingsParser.getSourceDir()).listFiles()));
            formatPattern = "%0" + String.valueOf(files.size()).length() + "d";

        } catch (SettingsParsingException | NullPointerException e) {
            throw new FileShufflingException("File shuffler cannot be created because of errors", e);
        }
    }

    private void createDirectory() throws DirectoryCreationException {

        if (!settingsParser.getSourceDir().equalsIgnoreCase(settingsParser.getDestDir()) &&
                !new File(settingsParser.getDestDir()).mkdir()) {

            throw new DirectoryCreationException("Error at creation destination directory \""
                    + settingsParser.getDestDir() + "\"");
        }
    }

    private void shuffle() {
        for (int i = 0; i < settingsParser.getShufflesCount(); ++i) {
            Collections.shuffle(files);
        }
    }

    private List<String> validateNames() {

        List<String> validatedNames = new ArrayList<>();
        for (int i = 0; i < files.size(); ++i) {

            String name = files.get(i).getName();
            StringBuilder stringBuilder = new StringBuilder(name);
            String extension = name.substring(name.lastIndexOf("."));

            while (!Character.isAlphabetic(stringBuilder.charAt(0))) {
                stringBuilder.deleteCharAt(0);
            }

            if (stringBuilder.toString().equalsIgnoreCase(extension)) {
                stringBuilder = new StringBuilder("Undefined file " + (i + 1) + "." + extension);
            }

            stringBuilder.setCharAt(0, Character.toUpperCase(stringBuilder.charAt(0)));

            validatedNames.add(stringBuilder.toString());
        }

        return validatedNames;
    }

    public void shuffleFiles() throws FileShufflingException {
        try {

            createDirectory();
            shuffle();
            List<String> validatedNames = validateNames();

            for (int i = 0; i < files.size(); ++i) {

                String sourceName = files.get(i).getName();
                String destName = String.format(formatPattern, i + 1) + " " + validatedNames.get(i);

                Files.move(Paths.get(settingsParser.getSourceDir() + "\\" + sourceName),
                        Paths.get(settingsParser.getDestDir() + "\\" + destName), StandardCopyOption.REPLACE_EXISTING);

                System.out.println(sourceName + "   ----->   " + destName);
            }

        } catch (DirectoryCreationException | IOException e) {
            throw new FileShufflingException("File shuffler cannot shuffle files because of errors", e);
        }
    }
}
