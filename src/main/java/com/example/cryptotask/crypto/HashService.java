package com.example.cryptotask.crypto;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;

public final class HashService {
    public byte[] digest(String algorithm, byte[] data) throws GeneralSecurityException {
        AlgorithmCatalog.requireHashAlgorithm(algorithm);
        return MessageDigest.getInstance(algorithm).digest(data);
    }
}
