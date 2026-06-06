package com.example.cryptotask.crypto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class CryptoPackageCodec {
    private static final String PACKAGE_MAGIC = "CRYPTO_TASK_V1";
    private static final int MAX_FIELD_BYTES = 100 * 1024 * 1024;

    public byte[] encode(CryptoPackage cryptoPackage) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(bytes)) {
            out.writeUTF(PACKAGE_MAGIC);
            out.writeUTF(cryptoPackage.symmetricAlgorithm());
            out.writeUTF(cryptoPackage.hashAlgorithm());
            writeBytes(out, cryptoPackage.iv());
            writeBytes(out, cryptoPackage.encryptedKey());
            writeBytes(out, cryptoPackage.encryptedPayload());
        }
        return bytes.toByteArray();
    }

    public CryptoPackage decode(byte[] data) throws IOException {
        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(data))) {
            String magic = in.readUTF();
            if (!PACKAGE_MAGIC.equals(magic)) {
                throw new IOException("不是本程序生成的密文包。");
            }
            String symmetricAlgorithm = in.readUTF();
            String hashAlgorithm = in.readUTF();
            byte[] iv = readBytes(in, "IV");
            byte[] encryptedKey = readBytes(in, "加密后的对称密钥");
            byte[] encryptedPayload = readBytes(in, "加密后的 M || 签名");
            if (in.available() != 0) {
                throw new IOException("密文包包含多余数据。");
            }
            return new CryptoPackage(symmetricAlgorithm, hashAlgorithm, iv, encryptedKey, encryptedPayload);
        }
    }

    private static void writeBytes(DataOutputStream out, byte[] data) throws IOException {
        out.writeInt(data.length);
        out.write(data);
    }

    private static byte[] readBytes(DataInputStream in, String fieldName) throws IOException {
        int length = in.readInt();
        if (length < 0 || length > MAX_FIELD_BYTES) {
            throw new IOException(fieldName + "字段长度非法。");
        }
        byte[] data = in.readNBytes(length);
        if (data.length != length) {
            throw new IOException(fieldName + "字段内容不完整。");
        }
        return data;
    }
}
