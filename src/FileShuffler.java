import exceptions.DirectoryCreationException;
import exceptions.FileShufflerException;
import exceptions.SettingsParserException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.function.Predicate;

public class FileShuffler {

    private final SettingsParser settingsParser;

    private final List<File> files;

    private final int initialSize;

    private final String formatPattern;

    public FileShuffler() throws SettingsParserException, FileShufflerException {
        this(new SettingsParser());
    }

    public FileShuffler(SettingsParser settingsParser) throws FileShufflerException {
        try {
            this.settingsParser = settingsParser;
            files = new ArrayList<>(List.of(Objects.requireNonNull(new File(this.settingsParser.getSourceDir()).listFiles())));
            initialSize = files.size();
            formatPattern = "%0" + String.valueOf(initialSize).length() + "d";
        } catch (NullPointerException e) {
            throw new FileShufflerException("Перемешиватель файлов не может быть правильно создан", e);
        }
    }

    public void shuffleFiles() throws FileShufflerException {
        try {
            createDestDirectoryIfNecessary();
            removeFilesIfNecessary();
            shuffleFilesInLoop();
            moveOrRenameFiles();
            System.out.println("\n\n\nПереименовано файлов: " + files.size());
            System.out.println("Удалено файлов: " + (initialSize - files.size()));
        } catch (DirectoryCreationException | IOException e) {
            throw new FileShufflerException("Файлы не могут быть правильно перемешаны", e);
        }
    }

    private void createDestDirectoryIfNecessary() throws DirectoryCreationException {
        if (!settingsParser.getSourceDir().equalsIgnoreCase(settingsParser.getDestDir())
                && !Files.exists(Paths.get(settingsParser.getDestDir()))
                && !new File(settingsParser.getDestDir()).mkdir()
        ) {
            throw new DirectoryCreationException("Ошибка при создании выходной папки \"%s\"".formatted(settingsParser.getDestDir()));
        }
    }

    private void removeFilesIfNecessary() throws IOException {
        if (settingsParser.isWithExtensions()) {
            if (settingsParser.isExcludedExtensions()) {
                removeFile(file -> settingsParser.getExtensions().contains(getFileExtensionInLowerCase(file)));
            } else {
                removeFile(file -> !settingsParser.getExtensions().contains(getFileExtensionInLowerCase(file)));
            }
        }
    }

    private void removeFile(Predicate<File> predicate) throws IOException {
        Iterator<File> iterator = files.iterator();
        while (iterator.hasNext()) {
            File current = iterator.next();
            if (predicate.test(current)) {
                Files.delete(current.toPath());
                iterator.remove();
                System.out.println(current.getName() + "   ----->   УДАЛЁН");
            }
        }
    }

    private void shuffleFilesInLoop() {
        for (int i = 0; i < settingsParser.getShufflesCount(); ++i) {
            Collections.shuffle(files);
        }
    }

    private void moveOrRenameFiles() throws IOException {
        for (int i = 0; i < files.size(); ++i) {
            String sourceName = files.get(i).getName();
            String destName = String.format(formatPattern, i + 1) + " " + originalFilenameOrUndefined(files.get(i));
            Files.move(
                    Paths.get(settingsParser.getSourceDir() + "\\" + sourceName),
                    Paths.get(settingsParser.getDestDir() + "\\" + destName),
                    StandardCopyOption.REPLACE_EXISTING
            );
            System.out.println(sourceName + "   ----->   " + destName);
        }
    }

    private String originalFilenameOrUndefined(File file) {
        StringBuilder stringBuilder = new StringBuilder(file.getName());
        String extension = getFileExtensionInLowerCase(file);
        while (!Character.isAlphabetic(stringBuilder.charAt(0))) {
            stringBuilder.deleteCharAt(0);
        }
        if (stringBuilder.toString().equalsIgnoreCase(extension)) {
            stringBuilder = new StringBuilder("Undefined file " + UUID.randomUUID() + "." + extension);
        }
        return stringBuilder.toString();
    }

    private String getFileExtensionInLowerCase(File file) {
        String name = file.getName();
        return name.substring(name.lastIndexOf(".") + 1).toLowerCase();
    }

}
