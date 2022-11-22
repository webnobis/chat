package com.webnobis.chat.model;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Optional;

/**
 * Password
 *
 * @author steffen
 */
public final class Password implements Serializable {

    /**
     * Used algorithm: SHA-512
     */
    public static final String ALGORITHM = "SHA-512";

    private final byte[] password;

    /**
     * Creates the hashed password from the real password
     *
     * @param password real password
     */
    public Password(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            byte[] bytes = Optional.ofNullable(password).map(String::getBytes).orElse(new byte[1]);
            digest.update(bytes);
            this.password = digest.digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(ALGORITHM.concat(" not supported"), e);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        return Arrays.equals(password, ((Password) other).password);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(password);
    }
}
