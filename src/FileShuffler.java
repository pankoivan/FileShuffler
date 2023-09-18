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

    public FileShuffler() throws FileShufflingException, SettingsParsingException {
        this(new SettingsParser());
    }

    public FileShuffler(SettingsParser settingsParser) throws FileShufflingException {
        try {
            this.settingsParser = settingsParser;
            files = new ArrayList<>(Arrays.asList(Objects.requireNonNull(
                    new File(this.settingsParser.getSourceDir()).listFiles())));
            formatPattern = "%0" + String.valueOf(files.size()).length() + "d";
            
        } catch (NullPointerException e) {
            throw new FileShufflingException("File shuffler cannot be created because of errors", e);
        }
    }

    public void shuffle() throws FileShufflingException {
        try {
            createDestDirectoryIfNecessary();
            excludeUnnecessary();
            cyclicShuffle();
            moveOrRename();
        } catch (DirectoryCreationException | IOException e) {
            throw new FileShufflingException("File shuffler cannot shuffle files because of errors", e);
        }
    }

    private void createDestDirectoryIfNecessary() throws DirectoryCreationException {

        if (!settingsParser.getSourceDir().equalsIgnoreCase(settingsParser.getDestDir())
                && !Files.exists(Paths.get(settingsParser.getDestDir()))
                && !new File(settingsParser.getDestDir()).mkdir()) {

            throw new DirectoryCreationException("Error at creation destination directory \""
                    + settingsParser.getDestDir() + "\"");
        }
    }

    private void excludeUnnecessary() {
        removeAll();
    }

    private void removeAll() {
        files.removeIf(file -> settingsParser.getExcludedFormats().contains(getExtension(file)));
    }

    private void cyclicShuffle() {
        for (int i = 0; i < settingsParser.getShufflesCount(); ++i) {
            Collections.shuffle(files);
        }
    }

    private void moveOrRename() throws IOException {
        for (int i = 0; i < files.size(); ++i) {

            String sourceName = files.get(i).getName();
            String destName = String.format(formatPattern, i + 1) + " " + originalNameOrUndefined(files.get(i));

            Files.move(Paths.get(settingsParser.getSourceDir() + "\\" + sourceName),
                    Paths.get(settingsParser.getDestDir() + "\\" + destName), StandardCopyOption.REPLACE_EXISTING);

            System.out.println(sourceName + "   ----->   " + destName);
        }
    }

    private String originalNameOrUndefined(File file) {

        StringBuilder stringBuilder = new StringBuilder(file.getName());
        String extension = getExtension(file);

        while (!Character.isAlphabetic(stringBuilder.charAt(0))) {
            stringBuilder.deleteCharAt(0);
        }

        if (stringBuilder.toString().equalsIgnoreCase(extension)) {
            stringBuilder = new StringBuilder("Undefined file " + UUID.randomUUID() + "." + extension);
        }

        return stringBuilder.toString();
    }

    private String getExtension(File file) {
        String name = file.getName();
        return name.substring(name.lastIndexOf(".") + 1);
    }

}
