package com.webnobis.chat.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class PasswordTest {

    private static final String PASSWORD = "k}#!:m.4\"hWdo;rV7_@BLAo*~{N/6,Y|MF\"";

    @Test
    void testEquals() {
        assertEquals(new Password(PASSWORD), new Password(PASSWORD));
        assertNotEquals(new Password(PASSWORD), new Password(null));
    }

    @Test
    void testHashCode() {
        assertEquals(new Password(PASSWORD).hashCode(), new Password(PASSWORD).hashCode());
    }
}