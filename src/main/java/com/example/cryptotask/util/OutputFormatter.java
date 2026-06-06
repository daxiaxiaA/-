package com.example.cryptotask.util;

import java.io.File;
import java.nio.charset.StandardCharsets;

public final class OutputFormatter {
    private OutputFormatter() {
    }

    public static String format(byte[] bytes, boolean fileMode, File selectedFile) {
        if (fileMode) {
            String name = selectedFile == null ? "未知文件" : selectedFile.getName();
            return "已解密文件内容：" + name + "\n字节数：" + bytes.length
                    + "\n可点击“保存解密结果”导出。";
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
