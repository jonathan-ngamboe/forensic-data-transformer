package model;

/**
 * Represents a normalized, flattened chat message ready for analysis.
 */
public record ChatMessage(
        String conversationId,
        String platformId,
        String timestamp,
        String sender,
        String message
) {}