package com.example.cryptotask.selftest;

import com.example.cryptotask.crypto.AlgorithmCatalog;
import com.example.cryptotask.crypto.CryptoPackage;
import com.example.cryptotask.crypto.CryptoPackageCodec;
import com.example.cryptotask.crypto.CryptoWorkflow;
import com.example.cryptotask.crypto.KeyMaterial;
import com.example.cryptotask.crypto.VerificationResult;
import com.example.cryptotask.crypto.WorkflowRequest;
import com.example.cryptotask.crypto.WorkflowResult;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.util.Arrays;

public final class SelfTestRunner {
    private SelfTestRunner() {
    }

    public static void runFromCommandLine() {
        try {
            SelfTestReport report = runAll();
            System.out.println(report.toConsoleText());
        } catch (Exception ex) {
            System.err.println("SELF_TEST_FAILED: " + ex.getMessage());
            ex.printStackTrace(System.err);
            System.exit(1);
        }
    }

    public static SelfTestReport runAll() throws Exception {
        KeyMaterial keyMaterial = new KeyMaterial();
        CryptoWorkflow workflow = new CryptoWorkflow();
        CryptoPackageCodec codec = new CryptoPackageCodec();
        SelfTestReport report = new SelfTestReport();
        for (String symmetricAlgorithm : AlgorithmCatalog.symmetricAlgorithms()) {
            for (String hashAlgorithm : AlgorithmCatalog.hashAlgorithms()) {
                runCase(report, workflow, codec, keyMaterial, symmetricAlgorithm, hashAlgorithm);
            }
        }
        return report;
    }

    private static void runCase(SelfTestReport report,
                                CryptoWorkflow workflow,
                                CryptoPackageCodec codec,
                                KeyMaterial keyMaterial,
                                String symmetricAlgorithm,
                                String hashAlgorithm) throws Exception {
        byte[] plain = ("自检明文|" + symmetricAlgorithm + "|" + hashAlgorithm)
                .getBytes(StandardCharsets.UTF_8);
        KeyPair sender = keyMaterial.generateRsaKeyPair(2048);
        KeyPair receiver = keyMaterial.generateRsaKeyPair(2048);
        SecretKey symmetricKey = keyMaterial.generateSymmetricKey(
                symmetricAlgorithm, "self-test-seed-" + symmetricAlgorithm);
        WorkflowRequest request = new WorkflowRequest(
                plain, symmetricAlgorithm, hashAlgorithm, symmetricKey, sender, receiver);
        WorkflowResult result = workflow.run(request);
        assertTrue(result.verificationResult().verified(), "验签未通过");
        assertTrue(Arrays.equals(plain, result.verificationResult().plainText()), "解密明文不一致");
        byte[] encoded = codec.encode(result.cryptoPackage());
        CryptoPackage decoded = codec.decode(encoded);
        VerificationResult reopened = workflow.decryptAndVerify(decoded, sender, receiver);
        assertTrue(reopened.verified(), "重新打开密文包后验签未通过");
        assertTrue(Arrays.equals(plain, reopened.plainText()), "重新打开密文包后明文不一致");
        report.addPassedCase(symmetricAlgorithm, hashAlgorithm, encoded.length);
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
