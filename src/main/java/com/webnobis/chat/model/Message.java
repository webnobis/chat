package com.webnobis.chat.model;

/**
 * Chat message
 *
 * @author steffen
 */
public record Message(long timestamp, String name, String text) {

    /**
     * Builds the chat line
     *
     * @return chat line
     */
    public String toString() {
        return String.join(": ", name(), text());
    }
}
