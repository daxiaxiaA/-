package com.example.cryptotask.crypto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class SignaturePayloadCodec {
    public byte[] combine(byte[] plain, byte[] signature) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(bytes)) {
            writeField(out, "明文", plain);
            writeField(out, "签名", signature);
        }
        return bytes.toByteArray();
    }

    public PlainAndSignature split(byte[] data) throws IOException {
        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(data))) {
            byte[] plain = readField(in, "明文", data.length);
            byte[] signature = readField(in, "签名", data.length);
            if (in.available() != 0) {
                throw new IOException("M || 签名结构包含多余数据。");
            }
            return new PlainAndSignature(plain, signature);
        }
    }

    private static void writeField(DataOutputStream out, String name, byte[] value) throws IOException {
        if (value == null) {
            throw new IOException(name + "不能为空。");
        }
        out.writeInt(value.length);
        out.write(value);
    }

    private static byte[] readField(DataInputStream in, String name, int maxLength) throws IOException {
        int length = in.readInt();
        if (length < 0 || length > maxLength) {
            throw new IOException(name + "长度非法。");
        }
        byte[] value = in.readNBytes(length);
        if (value.length != length) {
            throw new IOException(name + "内容不完整。");
        }
        return value;
    }
}
