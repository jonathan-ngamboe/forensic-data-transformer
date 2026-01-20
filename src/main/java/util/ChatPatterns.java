package util;

import java.util.regex.Pattern;

/**
 * Central repository for Regex patterns used in log parsing
 * to directly capture values of interest.
 */
public final class ChatPatterns {

    private ChatPatterns() {}

    /**
     * Matches the block start identifier (e.g., "APD93823").
     * Captures the ID in Group 1.
     * Handles the optional trailing comma seen in the source file.
     */
    public static final Pattern BLOCK_START = Pattern.compile("^(APD\\d+),?.*");

    /**
     * Matches the Conversation ID metadata line.
     * Captures the UUID value in Group 1.
     */
    public static final Pattern CONVERSATION_KEY = Pattern.compile("^Conversation Identifier:,(.*)");

    /**
     * Matches the Platform Call ID metadata line.
     * Captures the ID value in Group 1.
     */
    public static final Pattern PLATFORM_KEY = Pattern.compile("^Platform Call ID:,(.*)");

    /**
     * Matches the Date metadata line.
     * Captures the date string in Group 1.
     */
    public static final Pattern DATE_KEY = Pattern.compile("^Date and time:,(.*)");

    /**
     * Matches a message line starting with an email address.
     * Group 1: The Sender (Email)
     * Group 2: The Raw Message (rest of the line, including potential quotes)
     */
    public static final Pattern MESSAGE_LINE = Pattern.compile("^([\\w-\\.]+@[\\w-]+\\.[\\w-]{2,4}),(.*)");

}