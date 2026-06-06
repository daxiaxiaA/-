package com.example.cryptotask.crypto;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.MessageDigest;

public final class CryptoWorkflow {
    private final HashService hashService;
    private final RsaCipher rsaCipher;
    private final SymmetricCipher symmetricCipher;
    private final SignaturePayloadCodec payloadCodec;

    public CryptoWorkflow() {
        this(new HashService(), new RsaCipher(), new SymmetricCipher(), new SignaturePayloadCodec());
    }

    public CryptoWorkflow(HashService hashService, RsaCipher rsaCipher,
                          SymmetricCipher symmetricCipher, SignaturePayloadCodec payloadCodec) {
        this.hashService = hashService;
        this.rsaCipher = rsaCipher;
        this.symmetricCipher = symmetricCipher;
        this.payloadCodec = payloadCodec;
    }

    public WorkflowResult run(WorkflowRequest request) throws CryptoException {
        try {
            request.validate();
            byte[] hash = hashService.digest(request.hashAlgorithm(), request.plainText());
            byte[] signature = rsaCipher.privateEncrypt(hash, request.senderKeys().getPrivate());
            byte[] combined = payloadCodec.combine(request.plainText(), signature);
            SymmetricCipherResult encryptedPayload = symmetricCipher.encrypt(
                    request.symmetricAlgorithm(), request.symmetricKey(), combined);
            byte[] encryptedKey = rsaCipher.publicEncrypt(
                    request.symmetricKey().getEncoded(), request.receiverKeys().getPublic());
            CryptoPackage cryptoPackage = new CryptoPackage(
                    request.symmetricAlgorithm(),
                    request.hashAlgorithm(),
                    encryptedPayload.iv(),
                    encryptedKey,
                    encryptedPayload.cipherText());
            VerificationResult verification = decryptAndVerify(
                    cryptoPackage, request.senderKeys(), request.receiverKeys());
            return new WorkflowResult(
                    cryptoPackage,
                    verification,
                    hash,
                    signature,
                    combined.length,
                    encryptedPayload.cipherText().length,
                    encryptedKey.length);
        } catch (GeneralSecurityException | IOException | RuntimeException ex) {
            throw new CryptoException("完整签名、加密、解密和验签流程执行失败。", ex);
        }
    }

    public VerificationResult decryptAndVerify(CryptoPackage cryptoPackage,
                                               KeyPair senderKeys,
                                               KeyPair receiverKeys) throws CryptoException {
        try {
            byte[] keyBytes = rsaCipher.privateDecrypt(cryptoPackage.encryptedKey(), receiverKeys.getPrivate());
            SecretKey receivedKey = new SecretKeySpec(keyBytes, cryptoPackage.symmetricAlgorithm());
            byte[] combined = symmetricCipher.decrypt(
                    cryptoPackage.symmetricAlgorithm(),
                    receivedKey,
                    cryptoPackage.iv(),
                    cryptoPackage.encryptedPayload());
            PlainAndSignature plainAndSignature = payloadCodec.split(combined);
            byte[] expectedHash = hashService.digest(
                    cryptoPackage.hashAlgorithm(), plainAndSignature.plainText());
            byte[] signatureHash = rsaCipher.publicDecrypt(
                    plainAndSignature.signature(), senderKeys.getPublic());
            boolean verified = MessageDigest.isEqual(expectedHash, signatureHash);
            return new VerificationResult(
                    plainAndSignature.plainText(),
                    expectedHash,
                    signatureHash,
                    verified);
        } catch (GeneralSecurityException | IOException | RuntimeException ex) {
            throw new CryptoException("密文包解密或验签失败。", ex);
        }
    }
}
