package com.example.cryptotask.crypto;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;

public final class KeyMaterial {
    private final SecureRandom secureRandom;

    public KeyMaterial() {
        this(new SecureRandom());
    }

    public KeyMaterial(SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
    }

    public KeyPair generateRsaKeyPair(int keySize) throws GeneralSecurityException {
        AlgorithmCatalog.requireRsaKeySize(keySize);
        KeyPairGenerator generator = KeyPairGenerator.getInstance(AlgorithmCatalog.RSA);
        generator.initialize(keySize, secureRandom);
        return generator.generateKeyPair();
    }

    public SecretKey generateSymmetricKey(String algorithm, String seed) throws GeneralSecurityException {
        AlgorithmCatalog.requireSymmetricAlgorithm(algorithm);
        if (seed == null || seed.isBlank()) {
            return generateRandomSymmetricKey(algorithm);
        }
        return deriveSymmetricKey(algorithm, seed);
    }

    public SecretKey generateRandomSymmetricKey(String algorithm) throws GeneralSecurityException {
        AlgorithmCatalog.requireSymmetricAlgorithm(algorithm);
        KeyGenerator generator = KeyGenerator.getInstance(algorithm);
        generator.init(AlgorithmCatalog.generatedKeyBits(algorithm), secureRandom);
        return generator.generateKey();
    }

    public SecretKey deriveSymmetricKey(String algorithm, String seed) throws GeneralSecurityException {
        AlgorithmCatalog.requireSymmetricAlgorithm(algorithm);
        byte[] digest = MessageDigest.getInstance(AlgorithmCatalog.SHA_256)
                .digest(seed.getBytes(StandardCharsets.UTF_8));
        byte[] keyBytes = Arrays.copyOf(digest, AlgorithmCatalog.symmetricKeyBytes(algorithm));
        return new SecretKeySpec(keyBytes, algorithm);
    }
}
