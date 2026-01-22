package exporter;


import model.ChatMessage;

import tools.jackson.databind.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class JsonChatExporterTest {

    private DataExporter<ChatMessage> exporter;
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        exporter = new JsonChatExporter();
        mapper = new ObjectMapper();
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

        JsonNode rootNode = mapper.readTree(outputFile.toFile());

        assertTrue(rootNode.isArray(), "Root should be a JSON array");
        assertEquals(2, rootNode.size(), "Should contain 2 elements");

        JsonNode firstElement = rootNode.get(0);
        assertEquals("APD1", firstElement.get("id").asString());
        assertEquals("tom@test.com", firstElement.get("sender").asString());
    }

    @Test
    @DisplayName("Should handle JSON escaping logic")
    void export_ShouldEscapeSpecialCharacters(@TempDir Path tempDir) throws IOException {
        Path outputFile = tempDir.resolve("escape.json");
        String trickyMessage = "She said \"Hello\" \\ Bye";

        Stream<ChatMessage> messages = Stream.of(
                new ChatMessage("APD2", "ID", "P", "D", "S", trickyMessage)
        );

        exporter.export(messages, outputFile);

        JsonNode rootNode = mapper.readTree(outputFile.toFile());
        String savedMessage = rootNode.get(0).get("message").asString();

        assertEquals(trickyMessage, savedMessage);
    }

    @Test
    @DisplayName("Should produce an empty array for empty stream")
    void export_ShouldHandleEmptyStream(@TempDir Path tempDir) throws IOException {
        Path outputFile = tempDir.resolve("empty.json");
        Stream<ChatMessage> emptyStream = Stream.empty();

        exporter.export(emptyStream, outputFile);

        JsonNode root = mapper.readTree(outputFile.toFile());

        assertTrue(root.isArray());
        assertTrue(root.isEmpty(), "Array should be empty");
    }
}