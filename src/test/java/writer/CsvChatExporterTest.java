package writer;

import model.ChatMessage;

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

class CsvChatExporterTest {

    private CsvChatExporter writer;

    @BeforeEach
    void setUp() {
        writer = new CsvChatExporter();
    }

    @Test
    @DisplayName("Should create file with Header and Data")
    void write_ShouldCreateFile_WithHeaderAndRows(@TempDir Path tempDir) throws IOException {
        Path outputFile = tempDir.resolve("output.csv");
        Stream<ChatMessage> messages = Stream.of(
                new ChatMessage("APD1", "UUID1", "Call1", "2023-01-01", "tom@test.com", "Hello"),
                new ChatMessage("APD1", "UUID1", "Call1", "2023-01-01", "jerry@test.com", "Hi")
        );

        writer.write(messages, outputFile);

        List<String> lines = Files.readAllLines(outputFile);
        assertEquals(3, lines.size(), "Should have 1 header + 2 data rows");
        assertEquals("BlockID,ConversationID,PlatformID,Date,Sender,Message", lines.get(0), "Header mismatch");
        assertEquals("APD1,UUID1,Call1,2023-01-01,tom@test.com,Hello", lines.get(1));
    }

    @Test
    @DisplayName("Should handle special characters (CSV Escaping)")
    void write_ShouldEscapeCommasAndQuotes(@TempDir Path tempDir) throws IOException {
        Path outputFile = tempDir.resolve("escape_test.csv");

        Stream<ChatMessage> messages = Stream.of(
                new ChatMessage("APD2", "ID2", "P2", "Date", "Bond, James", "He said \"Stop\"")
        );

        writer.write(messages, outputFile);

        List<String> lines = Files.readAllLines(outputFile);
        String expectedRow = "APD2,ID2,P2,Date,\"Bond, James\",\"He said \"\"Stop\"\"\"";

        assertEquals(expectedRow, lines.get(1), "CSV escaping logic is incorrect");
    }

    @Test
    @DisplayName("Should handle null fields gracefully")
    void write_ShouldHandleNullFields(@TempDir Path tempDir) throws IOException {
        Path outputFile = tempDir.resolve("null_test.csv");
        Stream<ChatMessage> messages = Stream.of(
                new ChatMessage("APD3", null, "P3", null, "me@test.com", null)
        );

        writer.write(messages, outputFile);

        List<String> lines = Files.readAllLines(outputFile);
        assertEquals("APD3,,P3,,me@test.com,", lines.get(1));
    }

    @Test
    @DisplayName("Should write only header if stream is empty")
    void write_ShouldWriteOnlyHeader_WhenStreamIsEmpty(@TempDir Path tempDir) throws IOException {
        Path outputFile = tempDir.resolve("empty.csv");
        Stream<ChatMessage> emptyStream = Stream.empty();

        writer.write(emptyStream, outputFile);

        List<String> lines = Files.readAllLines(outputFile);
        assertEquals(1, lines.size());
        assertTrue(lines.getFirst().startsWith("BlockID"), "Header should still be present");
    }
}