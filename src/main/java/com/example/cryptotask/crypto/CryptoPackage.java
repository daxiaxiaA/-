package com.example.cryptotask.crypto;

import java.util.Arrays;

public final class CryptoPackage {
    private final String symmetricAlgorithm;
    private final String hashAlgorithm;
    private final byte[] iv;
    private final byte[] encryptedKey;
    private final byte[] encryptedPayload;

    public CryptoPackage(String symmetricAlgorithm, String hashAlgorithm, byte[] iv,
                         byte[] encryptedKey, byte[] encryptedPayload) {
        AlgorithmCatalog.requireSymmetricAlgorithm(symmetricAlgorithm);
        AlgorithmCatalog.requireHashAlgorithm(hashAlgorithm);
        this.symmetricAlgorithm = symmetricAlgorithm;
        this.hashAlgorithm = hashAlgorithm;
        this.iv = copy(iv);
        this.encryptedKey = copy(encryptedKey);
        this.encryptedPayload = copy(encryptedPayload);
    }

    public String symmetricAlgorithm() {
        return symmetricAlgorithm;
    }

    public String hashAlgorithm() {
        return hashAlgorithm;
    }

    public byte[] iv() {
        return copy(iv);
    }

    public byte[] encryptedKey() {
        return copy(encryptedKey);
    }

    public byte[] encryptedPayload() {
        return copy(encryptedPayload);
    }

    public int payloadSize() {
        return encryptedPayload.length;
    }

    public int encryptedKeySize() {
        return encryptedKey.length;
    }

    private static byte[] copy(byte[] value) {
        if (value == null) {
            throw new IllegalArgumentException("密文包字段不能为空。");
        }
        return Arrays.copyOf(value, value.length);
    }
}
