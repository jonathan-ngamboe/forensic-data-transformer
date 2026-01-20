package exporter;

import model.ChatMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class JsonChatExporterTest {

    private DataExporter<ChatMessage> exporter;

    @BeforeEach
    void setUp() {
        exporter = new JsonChatExporter();
    }

    @Test
    @DisplayName("Should generate a valid JSON array structure")
    void export_ShouldCreateValidJsonArray(@TempDir Path tempDir) throws IOException {
        Path outputFile = tempDir.resolve("output.json");
        Stream<ChatMessage> messages = Stream.of(
                new ChatMessage("APD1", "UUID1", "Call1", "2023-01-01", "tom@test.com", "Hello"),
                new ChatMessage("APD1", "UUID2", "Call1", "2023-01-01", "jerry@test.com", "Hi")
        );

        exporter.export(messages, outputFile);

        String content = Files.readString(outputFile);

        assertTrue(content.trim().startsWith("["), "JSON must start with '['");
        assertTrue(content.trim().endsWith("]"), "JSON must end with ']'");

        assertTrue(content.contains("\"id\": \"APD1\""));
        assertTrue(content.contains("\"sender\": \"tom@test.com\""));

        assertTrue(content.contains("},"), "Objects inside array should be separated by commas");
    }

    @Test
    @DisplayName("Should handle JSON escaping logic (quotes and backslashes)")
    void export_ShouldEscapeSpecialCharacters(@TempDir Path tempDir) throws IOException {
        Path outputFile = tempDir.resolve("escape.json");

        String trickyMessage = "She said \"Hello\" \\ Bye";

        Stream<ChatMessage> messages = Stream.of(
                new ChatMessage("APD2", "ID", "P", "D", "S", trickyMessage)
        );

        exporter.export(messages, outputFile);

        String content = Files.readString(outputFile);

        String expectedSnippet = "\"message\": \"She said \\\"Hello\\\" \\\\ Bye\"";

        assertTrue(content.contains(expectedSnippet),
                "Special characters like quotes and backslashes must be escaped correctly");
    }

    @Test
    @DisplayName("Should produce an empty array for empty stream")
    void export_ShouldHandleEmptyStream(@TempDir Path tempDir) throws IOException {
        Path outputFile = tempDir.resolve("empty.json");
        Stream<ChatMessage> emptyStream = Stream.empty();

        exporter.export(emptyStream, outputFile);

        String content = Files.readString(outputFile).trim();
        assertEquals("[]", content, "Empty stream should produce an empty JSON array");
    }
}