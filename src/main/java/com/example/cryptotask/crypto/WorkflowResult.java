package com.example.cryptotask.crypto;

import java.util.Arrays;

public final class WorkflowResult {
    private final CryptoPackage cryptoPackage;
    private final VerificationResult verificationResult;
    private final byte[] messageHash;
    private final byte[] signature;
    private final int combinedLength;
    private final int encryptedPayloadLength;
    private final int encryptedKeyLength;

    public WorkflowResult(CryptoPackage cryptoPackage,
                          VerificationResult verificationResult,
                          byte[] messageHash,
                          byte[] signature,
                          int combinedLength,
                          int encryptedPayloadLength,
                          int encryptedKeyLength) {
        this.cryptoPackage = cryptoPackage;
        this.verificationResult = verificationResult;
        this.messageHash = copy(messageHash);
        this.signature = copy(signature);
        this.combinedLength = combinedLength;
        this.encryptedPayloadLength = encryptedPayloadLength;
        this.encryptedKeyLength = encryptedKeyLength;
    }

    public CryptoPackage cryptoPackage() {
        return cryptoPackage;
    }

    public VerificationResult verificationResult() {
        return verificationResult;
    }

    public byte[] messageHash() {
        return copy(messageHash);
    }

    public byte[] signature() {
        return copy(signature);
    }

    public int combinedLength() {
        return combinedLength;
    }

    public int encryptedPayloadLength() {
        return encryptedPayloadLength;
    }

    public int encryptedKeyLength() {
        return encryptedKeyLength;
    }

    private static byte[] copy(byte[] value) {
        if (value == null) {
            throw new IllegalArgumentException("工作流结果字段不能为空。");
        }
        return Arrays.copyOf(value, value.length);
    }
}
