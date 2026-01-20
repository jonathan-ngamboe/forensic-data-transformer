package parser;

import model.ChatMessage;
import java.util.stream.Stream;

/**
 * Defines the contract for parsing raw data into structured objects.
 * Designed to handle various log formats via different implementations.
 */
public interface DataParser {

    /**
     * Transforms a stream of raw text lines into a stream of structured ChatMessages.
     *
     * @param rawLines The lazy stream of strings coming from the file loader.
     * @return A stream of ChatMessage records.
     */
    Stream<ChatMessage> parse(Stream<String> rawLines);
}