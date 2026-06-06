package com.example.cryptotask.crypto;

import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.util.Arrays;

public final class WorkflowRequest {
    private final byte[] plainText;
    private final String symmetricAlgorithm;
    private final String hashAlgorithm;
    private final SecretKey symmetricKey;
    private final KeyPair senderKeys;
    private final KeyPair receiverKeys;

    public WorkflowRequest(byte[] plainText,
                           String symmetricAlgorithm,
                           String hashAlgorithm,
                           SecretKey symmetricKey,
                           KeyPair senderKeys,
                           KeyPair receiverKeys) {
        this.plainText = plainText == null ? null : Arrays.copyOf(plainText, plainText.length);
        this.symmetricAlgorithm = symmetricAlgorithm;
        this.hashAlgorithm = hashAlgorithm;
        this.symmetricKey = symmetricKey;
        this.senderKeys = senderKeys;
        this.receiverKeys = receiverKeys;
    }

    public void validate() {
        if (plainText == null || plainText.length == 0) {
            throw new IllegalArgumentException("明文不能为空。");
        }
        AlgorithmCatalog.requireSymmetricAlgorithm(symmetricAlgorithm);
        AlgorithmCatalog.requireHashAlgorithm(hashAlgorithm);
        if (symmetricKey == null) {
            throw new IllegalArgumentException("对称密钥不能为空。");
        }
        if (!symmetricAlgorithm.equals(symmetricKey.getAlgorithm())) {
            throw new IllegalArgumentException("对称密钥算法与界面选择不一致。");
        }
        if (senderKeys == null || senderKeys.getPrivate() == null || senderKeys.getPublic() == null) {
            throw new IllegalArgumentException("发送方 A 的 RSA 密钥对未生成。");
        }
        if (receiverKeys == null || receiverKeys.getPrivate() == null || receiverKeys.getPublic() == null) {
            throw new IllegalArgumentException("接收方 B 的 RSA 密钥对未生成。");
        }
    }

    public byte[] plainText() {
        return Arrays.copyOf(plainText, plainText.length);
    }

    public String symmetricAlgorithm() {
        return symmetricAlgorithm;
    }

    public String hashAlgorithm() {
        return hashAlgorithm;
    }

    public SecretKey symmetricKey() {
        return symmetricKey;
    }

    public KeyPair senderKeys() {
        return senderKeys;
    }

    public KeyPair receiverKeys() {
        return receiverKeys;
    }
}
