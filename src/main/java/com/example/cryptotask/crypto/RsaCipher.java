package com.example.cryptotask.crypto;

import javax.crypto.Cipher;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;

public final class RsaCipher {
    public byte[] privateEncrypt(byte[] data, PrivateKey key) throws GeneralSecurityException {
        return transform(Cipher.ENCRYPT_MODE, key, data);
    }

    public byte[] publicDecrypt(byte[] data, PublicKey key) throws GeneralSecurityException {
        return transform(Cipher.DECRYPT_MODE, key, data);
    }

    public byte[] publicEncrypt(byte[] data, PublicKey key) throws GeneralSecurityException {
        return transform(Cipher.ENCRYPT_MODE, key, data);
    }

    public byte[] privateDecrypt(byte[] data, PrivateKey key) throws GeneralSecurityException {
        return transform(Cipher.DECRYPT_MODE, key, data);
    }

    private byte[] transform(int mode, java.security.Key key, byte[] data) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(AlgorithmCatalog.RSA_TRANSFORMATION);
        cipher.init(mode, key);
        return cipher.doFinal(data);
    }
}
