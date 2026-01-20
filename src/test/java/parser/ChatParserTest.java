package parser;

import model.ChatMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ChatParserTest {

    private ChatParser parser;

    @BeforeEach
    void setUp() {
        parser = new ChatParser();
    }

    @Test
    @DisplayName("Should extract standard messages correctly mapping all 6 fields")
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

        assertEquals("APD93823", msg.id(), "The ID field should contain the Block Identifier (APD...)");
        assertEquals("8755-UUID", msg.conversationId(), "The ConversationID field should contain the UUID");
        assertEquals("Call-123", msg.platformId(), "The PlatformID field should contain the Call ID");
        assertEquals("10/10/19 4:10:12 PM", msg.timestamp());
        assertEquals("anonym@anonym.fr", msg.sender());
        assertEquals("Good luck with the exercise!", msg.message());
    }

    @Test
    @DisplayName("Should handle interrupted blocks (Context Reset)")
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

        assertEquals(1, results.size(), "Should only capture the message from the valid/active block");

        ChatMessage msg = results.getFirst();
        assertEquals("APD002", msg.id(), "Should adhere to the latest Block ID");
        assertEquals("UUID-2", msg.conversationId(), "Should adhere to the latest Conversation UUID");
        assertEquals("Call-002", msg.platformId());
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

        assertTrue(messageContent.contains("Hello, world"), "Message content should preserve internal commas");
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
        assertEquals("APD123", results.getFirst().id(), "Should find the correct Block ID despite noise");
    }
}