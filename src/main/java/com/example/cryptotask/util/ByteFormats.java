package com.example.cryptotask.util;

import java.util.Base64;

public final class ByteFormats {
    private ByteFormats() {
    }

    public static String shortBase64(byte[] data) {
        String encoded = Base64.getEncoder().encodeToString(data);
        return encoded.length() <= 96 ? encoded : encoded.substring(0, 96) + "...";
    }

    public static String toHex(byte[] data) {
        StringBuilder builder = new StringBuilder(data.length * 2);
        for (byte b : data) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }
}
