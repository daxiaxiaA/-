package com.example.cryptotask.crypto;

import java.util.Arrays;

public final class VerificationResult {
    private final byte[] plainText;
    private final byte[] expectedHash;
    private final byte[] signatureHash;
    private final boolean verified;

    public VerificationResult(byte[] plainText, byte[] expectedHash, byte[] signatureHash, boolean verified) {
        this.plainText = copy(plainText);
        this.expectedHash = copy(expectedHash);
        this.signatureHash = copy(signatureHash);
        this.verified = verified;
    }

    public byte[] plainText() {
        return copy(plainText);
    }

    public byte[] expectedHash() {
        return copy(expectedHash);
    }

    public byte[] signatureHash() {
        return copy(signatureHash);
    }

    public boolean verified() {
        return verified;
    }

    private static byte[] copy(byte[] value) {
        if (value == null) {
            throw new IllegalArgumentException("验签结果字段不能为空。");
        }
        return Arrays.copyOf(value, value.length);
    }
}
