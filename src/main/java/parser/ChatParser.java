package parser;

import model.ChatMessage;

import static util.ChatPatterns.*;

import java.util.regex.Matcher;
import java.util.stream.Stream;

/**
 * Parses raw log lines into structured ChatMessage objects.
 */
public class ChatParser implements DataParser {

    /**
     * Transforms a stream of raw strings into a stream of ChatMessage objects.
     *
     * @param rawLines The input stream of log lines.
     * @return A stream of parsed ChatMessage objects, or an empty stream if input is null.
     */
    @Override
    public Stream<ChatMessage> parse(Stream<String> rawLines) {
        if (rawLines == null) return Stream.empty();

        ParsingContext context = new ParsingContext();

        return rawLines.mapMulti((line, consumer) -> {
            if (line == null || line.isBlank()) return;

            Matcher matcher;

            if ((matcher = BLOCK_START.matcher(line)).matches()) {
                context.reset();
                context.blockId = matcher.group(1);

            } else if ((matcher = CONVERSATION_KEY.matcher(line)).matches()) {
                context.conversationId = matcher.group(1);

            } else if ((matcher = PLATFORM_KEY.matcher(line)).matches()) {
                context.platformId = matcher.group(1);

            } else if ((matcher = DATE_KEY.matcher(line)).matches()) {
                context.timestamp = matcher.group(1);

            } else if ((matcher = MESSAGE_LINE.matcher(line)).matches()) {
                var sender = matcher.group(1);
                var message = matcher.group(2);

                var msg = new ChatMessage(
                        context.blockId,
                        context.conversationId,
                        context.platformId,
                        context.timestamp,
                        sender,
                        cleanMessage(message)
                );

                consumer.accept(msg);
            }
        });
    }

    /**
     * Sanitizes the message content by handling CSV-style escaping.
     * Removes surrounding quotes and unescapes double double-quotes.
     */
    private String cleanMessage(String message) {
        if (message.startsWith("\"") && message.endsWith("\"")) {
            return message.substring(1, message.length() - 1).replace("\"\"", "\"");
        }
        return message;
    }

    /**
     * Internal helper class to maintain parsing state across stream elements.
     */
    private static class ParsingContext {
        String blockId;
        String conversationId;
        String platformId;
        String timestamp;

        void reset() {
            this.blockId = null;
            this.conversationId = null;
            this.platformId = null;
            this.timestamp = null;
        }
    }
}