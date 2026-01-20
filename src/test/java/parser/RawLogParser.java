package parser;

import model.ChatMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class RawLogParserTest {

    private RawLogParser parser;

    @BeforeEach
    void setUp() {
        parser = new RawLogParser();
    }

    @Test
    @DisplayName("Should extract standard messages correctly")
    void parse_ShouldReturnMessages_WhenBlockIsComplete() {
        Stream<String> input = Stream.of(
                "APD93823,",
                "Conversation Identifier:,8755-UUID",
                "Platform Call ID:,Call-123",
                "Date and time:,10/10/19 4:10:12 PM",
                "anonym@anonym.fr,Good luck with the exercise!"
        );

        List<ChatMessage> results = parser.parse(input).toList();

        assertEquals(1, results.size());
        ChatMessage msg = results.getFirst();

        assertEquals("APD93823", msg.conversationId());
        assertEquals("875515930-8419-5982-c553", msg.platformId());
        assertEquals("10/10/19 4:10:12 PM", msg.timestamp());
        assertEquals("anonym@anonym.fr", msg.sender());
        assertEquals("Good luck with the exercise!", msg.message());
    }

    @Test
    @DisplayName("Should handle interrupted blocks")
    void parse_ShouldResetContext_WhenNewBlockStartsAbruptly() {
        Stream<String> input = Stream.of(
                "APD001,",
                "Conversation Identifier:,UUID-1",
                "APD002,",
                "Conversation Identifier:,UUID-2",
                "Platform Call ID:,Call-002",
                "Date and time:,11/11/19 5:00:00 PM",
                "user@test.com,Message du Bloc 2"
        );

        List<ChatMessage> results = parser.parse(input).toList();

        assertEquals(1, results.size(), "Should only output messages from the complete block");
        assertEquals("APD002", results.getFirst().conversationId(), "Context should adhere to the latest block ID");
        assertEquals("UUID-2", results.getFirst().platformId());
    }

    @Test
    @DisplayName("Should handle quoted messages containing commas")
    void parse_ShouldHandleQuotedMessages_WithCommas() {
        Stream<String> input = Stream.of(
                "APD999,",
                "Conversation Identifier:,UUID-9",
                "Platform Call ID:,Call-9",
                "Date and time:,10/10/19",
                "test@mail.com,\"Hello, world\""
        );

        List<ChatMessage> results = parser.parse(input).toList();

        assertEquals(1, results.size());
        String messageContent = results.getFirst().message();
        assertTrue(messageContent.contains("Hello, world"), "Message should not be split at the internal comma");
    }

    @Test
    @DisplayName("Should ignore garbage or empty lines")
    void parse_ShouldIgnoreIrrelevantLines() {
        Stream<String> input = Stream.of(
                "",
                "   ",
                "SomeRandomGarbageHeader",
                "APD123,", // Début valide
                "Conversation Identifier:,ID1",
                "Date and time:,Now",
                "sender@mail.com,Hello"
        );

        List<ChatMessage> results = parser.parse(input).toList();

        assertEquals(1, results.size());
        assertEquals("APD123", results.getFirst().conversationId());
    }
}