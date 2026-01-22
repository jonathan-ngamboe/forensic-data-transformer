package exporter;

import model.ChatMessage;

import tools.jackson.dataformat.csv.CsvMapper;
import tools.jackson.dataformat.csv.CsvSchema;

import java.nio.file.Path;
import java.util.stream.Stream;

public class CsvChatExporter implements DataExporter<ChatMessage> {
    private final CsvMapper csvMapper;
    private final CsvSchema schema;

    public CsvChatExporter() {
        csvMapper = new CsvMapper();
        schema    = csvMapper.schemaFor(ChatMessage.class).withHeader();
    }

    @Override
    public void export(Stream<ChatMessage> messages, Path outputPath) {
        if(messages == null || outputPath == null) return;

        var outputFile = outputPath.toFile();

        try(var sequenceWriter = csvMapper.writer(schema).writeValues(outputFile)) {
            messages.forEach(sequenceWriter::write);
        }
    }
}