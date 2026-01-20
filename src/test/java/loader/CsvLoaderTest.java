package loader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CsvLoaderTest {

    private CsvLoader csvLoader;

    @BeforeEach
    void setUp() {
        csvLoader = new CsvLoader();
    }

    @Test
    @DisplayName("Should load lines correctly from a valid file")
    void load_ShouldReturnStreamOfLines_WhenFileExists(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("test_data.csv");
        List<String> expectedLines = List.of(
                "Header,Date,Value",
                "ID1,2023-01-01,100",
                "ID2,2023-01-02,200"
        );
        Files.write(file, expectedLines);

        try (Stream<String> resultStream = csvLoader.load(file)) {

            List<String> actualLines = resultStream.toList();
            assertEquals(expectedLines, actualLines, "The content read should match the file content");
        }
    }

    @Test
    @DisplayName("Should handle empty files gracefully")
    void load_ShouldReturnEmptyStream_WhenFileIsEmpty(@TempDir Path tempDir) throws IOException {
        Path emptyFile = tempDir.resolve("empty.csv");
        Files.createFile(emptyFile);

        try (Stream<String> resultStream = csvLoader.load(emptyFile)) {

            long count = resultStream.count();
            assertEquals(0, count, "Stream should be empty for an empty file");
        }
    }

    @Test
    @DisplayName("Should throw IOException when file does not exist")
    void load_ShouldThrowIOException_WhenFileDoesNotExist(@TempDir Path tempDir) {
        Path nonExistentFile = tempDir.resolve("ghost.csv");

        assertThrows(IOException.class, () -> {
            csvLoader.load(nonExistentFile);
        }, "Should throw IOException if the file path is invalid");
    }

    @Test
    @DisplayName("Should throw IOException when path is a directory")
    void load_ShouldThrowIOException_WhenPathIsDirectory(@TempDir Path tempDir) {

        assertThrows(IOException.class, () -> {
            csvLoader.load(tempDir);
        }, "Should throw IOException if the path points to a directory instead of a file");
    }
}