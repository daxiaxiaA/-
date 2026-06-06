package com.example.cryptotask.crypto;

import java.util.Arrays;
import java.util.List;

public final class AlgorithmCatalog {
    public static final String AES = "AES";
    public static final String DES = "DES";
    public static final String SHA_256 = "SHA-256";
    public static final String SHA_1 = "SHA-1";
    public static final String MD5 = "MD5";
    public static final String RSA = "RSA";
    public static final String RSA_TRANSFORMATION = "RSA/ECB/PKCS1Padding";

    private static final List<String> SYMMETRIC_ALGORITHMS = List.of(AES, DES);
    private static final List<String> HASH_ALGORITHMS = List.of(SHA_256, SHA_1, MD5);
    private static final List<Integer> RSA_KEY_SIZES = List.of(2048, 1024, 512);

    private AlgorithmCatalog() {
    }

    public static List<String> symmetricAlgorithms() {
        return SYMMETRIC_ALGORITHMS;
    }

    public static List<String> hashAlgorithms() {
        return HASH_ALGORITHMS;
    }

    public static List<Integer> rsaKeySizes() {
        return RSA_KEY_SIZES;
    }

    public static void requireSymmetricAlgorithm(String algorithm) {
        if (!SYMMETRIC_ALGORITHMS.contains(algorithm)) {
            throw new IllegalArgumentException("不支持的对称加密算法：" + algorithm);
        }
    }

    public static void requireHashAlgorithm(String algorithm) {
        if (!HASH_ALGORITHMS.contains(algorithm)) {
            throw new IllegalArgumentException("不支持的 Hash 算法：" + algorithm);
        }
    }

    public static void requireRsaKeySize(int keySize) {
        if (keySize < 200) {
            throw new IllegalArgumentException("RSA 密钥长度不得小于 200 位。");
        }
        if (!RSA_KEY_SIZES.contains(keySize)) {
            throw new IllegalArgumentException("不支持的 RSA 密钥长度：" + keySize
                    + "，可选值为 " + Arrays.toString(RSA_KEY_SIZES.toArray()));
        }
    }

    public static int symmetricKeyBytes(String algorithm) {
        requireSymmetricAlgorithm(algorithm);
        if (AES.equals(algorithm)) {
            return 16;
        }
        return 8;
    }

    public static int generatedKeyBits(String algorithm) {
        requireSymmetricAlgorithm(algorithm);
        if (AES.equals(algorithm)) {
            return 128;
        }
        return 56;
    }
}
