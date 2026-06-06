package com.example.cryptotask.crypto;

import java.util.Arrays;

public final class SymmetricCipherResult {
    private final byte[] iv;
    private final byte[] cipherText;

    public SymmetricCipherResult(byte[] iv, byte[] cipherText) {
        this.iv = copy(iv);
        this.cipherText = copy(cipherText);
    }

    public byte[] iv() {
        return copy(iv);
    }

    public byte[] cipherText() {
        return copy(cipherText);
    }

    private static byte[] copy(byte[] value) {
        if (value == null) {
            throw new IllegalArgumentException("加密结果字段不能为空。");
        }
        return Arrays.copyOf(value, value.length);
    }
}
