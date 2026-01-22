package cli;

import exporter.CsvChatExporter;
import exporter.DataExporter;
import exporter.JsonChatExporter;
import loader.CsvLoader;
import loader.FileLoader;
import model.ChatMessage;
import parser.ChatParser;
import parser.DataParser;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;
import java.util.concurrent.Callable;

@Command(
        name = "forensic-data-transformer",
        mixinStandardHelpOptions = true,
        version = "1.0.0",
        description = "Transforms corrupted data into an exploitable format for legal analysis."
)
public class ForensicDataTransformerCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "The raw forensic collection file (Source).")
    private Path inputFile;

    @Parameters(index = "1", description = "The destination path for the structured report.")
    private Path outputFile;

    @Option(names = {"-f", "--format"}, description = "Target format: ${COMPLETION-CANDIDATES} (Default: CSV).")
    private OutputFormat format = OutputFormat.CSV;

    private enum OutputFormat {
        CSV, JSON
    }

    @Override
    public Integer call() {
        if (!inputFile.toFile().exists()) {
            System.err.println("Error: Evidence file not found at -> " + inputFile);
            return 1;
        }

        System.out.println("=== Deloitte Forensic | Data Transformation Tool ===");
        System.out.println("Processing evidence: " + inputFile.getFileName());
        System.out.println("Target format: " + format);

        long start = System.currentTimeMillis();

        FileLoader<String> loader = new CsvLoader();
        DataParser parser = new ChatParser();

        DataExporter<ChatMessage> exporter = switch (format) {
            case CSV -> new CsvChatExporter();
            case JSON -> new JsonChatExporter();
        };

        try (var lines = loader.load(inputFile)) {

            exporter.export(parser.parse(lines), outputFile);

            long duration = System.currentTimeMillis() - start;
            System.out.println("Transformation complete in " + duration + "ms.");
            System.out.println("Report generated at: " + outputFile.toAbsolutePath());

            return 0;

        } catch (Exception e) {
            System.err.println("CRITICAL FAILURE: Unable to process evidence file.");
            System.err.println("Reason: " + e.getMessage());
            e.printStackTrace();
            return 2;
        }
    }
}