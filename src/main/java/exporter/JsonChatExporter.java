package exporter;

import model.ChatMessage;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Exporter implementation that writes ChatMessage objects as a JSON Array.
 * The output is pretty-printed (indented).
 */
public class JsonChatExporter implements DataExporter<ChatMessage>{
    private final ObjectMapper jsonMapper;

    public JsonChatExporter() {
        jsonMapper = JsonMapper.builder()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .build();
    }

    @Override
    public void export(Stream<ChatMessage> messages, Path outputPath) {
        if(messages == null || outputPath == null) return;

        var outputFile = outputPath.toFile();

        try(var sequenceWriter = jsonMapper.writer().writeValues(outputFile)) {
            sequenceWriter.init(true); // Wrap in Array
            messages.forEach(sequenceWriter::write);
        }
    }
}
