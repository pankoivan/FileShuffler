import exceptions.DirectoryCreationException;
import exceptions.FileShufflingException;
import exceptions.SettingsParsingException;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.function.Predicate;

public class FileShuffler {

    private final SettingsParser settingsParser;

    private final List<File> files;

    private final int initialSize;

    private final String formatPattern;

    public FileShuffler() throws FileShufflingException, SettingsParsingException {
        this(new SettingsParser());
    }

    public FileShuffler(SettingsParser settingsParser) throws FileShufflingException {
        try {
            this.settingsParser = settingsParser;

            files = new ArrayList<>(Arrays.asList(Objects.requireNonNull(
                    new File(this.settingsParser.getSourceDir()).listFiles())));
            initialSize = files.size();

            formatPattern = "%0" + String.valueOf(files.size()).length() + "d";
            
        } catch (NullPointerException e) {
            throw new FileShufflingException("File shuffler cannot be created because of errors", e);
        }
    }

    public void shuffle() throws FileShufflingException {
        try {
            createDestDirectoryIfNecessary();
            removeIfNecessary();
            cyclicShuffle();
            moveOrRename();

            System.out.println("\n\n\nПереименовано файлов: " + files.size());
            System.out.println("Удалено файлов: " + (initialSize - files.size()));

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

    private void removeIfNecessary() throws IOException {
        if (settingsParser.isWithExtensions()) {
            if (settingsParser.isExcluded()) {
                remove(file -> settingsParser.getExtensions().contains(getExtensionInLowerCase(file)));
            } else {
                remove(file -> !settingsParser.getExtensions().contains(getExtensionInLowerCase(file)));
            }
        }
    }

    private void remove(Predicate<File> predicate) throws IOException {

        Iterator<File> iterator = files.iterator();
        while (iterator.hasNext()) {

            File current = iterator.next();
            if (predicate.test(current)) {

                Files.delete(current.toPath());
                iterator.remove();
                System.out.println(current.getName() + "   ----->   DELETED");
            }
        }
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
        String extension = getExtensionInLowerCase(file);

        while (!Character.isAlphabetic(stringBuilder.charAt(0))) {
            stringBuilder.deleteCharAt(0);
        }

        if (stringBuilder.toString().equalsIgnoreCase(extension)) {
            stringBuilder = new StringBuilder("Undefined file " + UUID.randomUUID() + "." + extension);
        }

        return stringBuilder.toString();
    }

    private String getExtensionInLowerCase(File file) {
        String name = file.getName();
        return name.substring(name.lastIndexOf(".") + 1).toLowerCase();
    }

}
