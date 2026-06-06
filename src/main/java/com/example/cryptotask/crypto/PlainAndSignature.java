package com.example.cryptotask.crypto;

import java.util.Arrays;

public final class PlainAndSignature {
    private final byte[] plainText;
    private final byte[] signature;

    public PlainAndSignature(byte[] plainText, byte[] signature) {
        this.plainText = copy(plainText);
        this.signature = copy(signature);
    }

    public byte[] plainText() {
        return copy(plainText);
    }

    public byte[] signature() {
        return copy(signature);
    }

    private static byte[] copy(byte[] data) {
        if (data == null) {
            throw new IllegalArgumentException("数据不能为空。");
        }
        return Arrays.copyOf(data, data.length);
    }
}
