package system;

import main.java.project.UI.ConsoleUI;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class SystemTests {

    private static final String PATH_TO_JUST_TEXT_FILE = "test/files/justFile.txt";
    private static final String PATH_TO_ARCHIVE_FILE = "test/files/archiveFile.uz";
    private static final String JUST_NAME_TEXT_FILE = "test/files/justFile1";
    private static final String ARCHIVE_NAME = "test/files/archiveFile1";
    private static final String ARCHIVE_FILE =
            "Мама3| мы4|ла ра7|му." +
            "Серьезно?" +
            "Да мне3| так Сере6|га сказал3|" +
            ".Аа3| почему3| мы говорим как Ма4|сквичи?" +
            "Просто мы3| осо4|бен6|ые.";
    private static final String JUST_FILE =
            "Мамааа мыыыыла раааааааму.Серьезно?" +
            "Да мнеее так Сереееееега сказаллл." +
            "Аааа почемууу мы говорим как Маааасквичи?" +
            "Просто мыыы осооообенннннные.";

    @Test
    void packing() {
        justAction(ARCHIVE_NAME, PATH_TO_JUST_TEXT_FILE, PATH_TO_ARCHIVE_FILE, ARCHIVE_FILE, "z");
    }

    @Test
    void unpacking() {
        justAction(JUST_NAME_TEXT_FILE, PATH_TO_ARCHIVE_FILE, PATH_TO_JUST_TEXT_FILE, JUST_FILE, "u");
    }

    private void justAction(String fileName, String firstPath, String secondPath, String textOfFile, String key) {
        ConsoleUI.testMode(String.format("pack-rle -%s -out %s %s", key, fileName, firstPath));
        ConsoleUI.create();
        assertTrue(ConsoleUI.isCompleted());

        List<String> result;
        try {
            result = Files.readAllLines(Paths.get(secondPath), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalArgumentException("Something went wrong");
        }
        assertEquals(textOfFile, result.get(0));
        fileKiller(JUST_NAME_TEXT_FILE + ".txt");
        fileKiller(ARCHIVE_NAME + ".uz");
    }

    @Test
    void easyPacking() {
        easyAction(PATH_TO_JUST_TEXT_FILE, JUST_NAME_TEXT_FILE, ARCHIVE_FILE, ".uz");
    }

    @Test
    void easyUnpacking() {
        easyAction(PATH_TO_ARCHIVE_FILE, ARCHIVE_NAME, JUST_FILE, ".txt");
    }

    private void easyAction(String path, String firstName, String secondName, String expansion) {
        ConsoleUI.testMode(String.format("pack-rle %s", path));
        ConsoleUI.create();
        assertTrue(ConsoleUI.isCompleted());

        List<String> result;
        try {
            result = Files.readAllLines(Paths.get(firstName + expansion), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalArgumentException("Something went wrong = " + e.getMessage());
        }
        assertEquals(secondName, result.get(0));
        fileKiller(firstName + expansion);
    }

    @Test
    void wrongCommand() {
        List<String> wrongCommands = Arrays.asList("", "pack-rle", "pack-rle dsfsd", "pack-rle -out dsfdf");
        wrongCommands.forEach(command -> assertThrows(IllegalArgumentException.class, () -> {
            ConsoleUI.testMode(command);
            ConsoleUI.create();
        }));
    }

    @Test
    void wrongData() {
        ConsoleUI.testMode("pack-rle -z -out test/files/archiveWrongData test/files/wrongData.txt");
        ConsoleUI.create();
        ConsoleUI.testMode("pack-rle -u -out test/files/unpackWrongData test/files/archiveWrongData.uz");
        ConsoleUI.create();
        String packData = "Аляляля? Лалала. В частности, Мама3&| мы4&|ла ра7&|му.";
        String originalData = "Аляляля? Лалала. В частности, Мама3| мы4|ла ра7|му.";
        List<String> puckingResult;
        List<String> unpackingResult;
        try {
            puckingResult = Files.readAllLines(Paths.get("test/files/archiveWrongData.uz"),
                    StandardCharsets.UTF_8);
            unpackingResult = Files.readAllLines(Paths.get("test/files/unpackWrongData.txt"),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalArgumentException("Something went wrong = " + e.getMessage());
        }
        assertEquals(puckingResult.get(0), packData);
        assertEquals(unpackingResult.get(0), originalData);
    }

    private void fileKiller(String fileName) {
        File file = new File(fileName);
        file.delete();
    }
}
