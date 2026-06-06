package com.example.cryptotask.crypto;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

public final class SymmetricCipher {
    private final SecureRandom secureRandom;

    public SymmetricCipher() {
        this(new SecureRandom());
    }

    public SymmetricCipher(SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
    }

    public SymmetricCipherResult encrypt(String algorithm, SecretKey key, byte[] plain)
            throws GeneralSecurityException {
        AlgorithmCatalog.requireSymmetricAlgorithm(algorithm);
        Cipher cipher = Cipher.getInstance(transformation(algorithm));
        byte[] iv = new byte[cipher.getBlockSize()];
        secureRandom.nextBytes(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
        return new SymmetricCipherResult(iv, cipher.doFinal(plain));
    }

    public byte[] decrypt(String algorithm, SecretKey key, byte[] iv, byte[] cipherText)
            throws GeneralSecurityException {
        AlgorithmCatalog.requireSymmetricAlgorithm(algorithm);
        Cipher cipher = Cipher.getInstance(transformation(algorithm));
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        return cipher.doFinal(cipherText);
    }

    private static String transformation(String algorithm) {
        return algorithm + "/CBC/PKCS5Padding";
    }
}
