package com.example.cryptotask.ui;

import com.example.cryptotask.crypto.AlgorithmCatalog;
import com.example.cryptotask.crypto.CryptoException;
import com.example.cryptotask.crypto.CryptoPackage;
import com.example.cryptotask.crypto.CryptoPackageCodec;
import com.example.cryptotask.crypto.CryptoWorkflow;
import com.example.cryptotask.crypto.KeyMaterial;
import com.example.cryptotask.crypto.VerificationResult;
import com.example.cryptotask.crypto.WorkflowRequest;
import com.example.cryptotask.crypto.WorkflowResult;
import com.example.cryptotask.util.ByteFormats;
import com.example.cryptotask.util.OutputFormatter;

import javax.crypto.SecretKey;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyPair;
import java.util.Base64;

public final class CryptoTaskFrame extends JFrame {
    private final JComboBox<String> inputModeBox = new JComboBox<>(new String[]{"字符串", "文件"});
    private final JComboBox<String> symmetricBox = new JComboBox<>(
            AlgorithmCatalog.symmetricAlgorithms().toArray(String[]::new));
    private final JComboBox<String> hashBox = new JComboBox<>(
            AlgorithmCatalog.hashAlgorithms().toArray(String[]::new));
    private final JComboBox<Integer> rsaSizeBox = new JComboBox<>(
            AlgorithmCatalog.rsaKeySizes().toArray(Integer[]::new));
    private final JTextField seedField = new JTextField();
    private final JTextField fileField = new JTextField();
    private final JTextArea inputArea = new JTextArea(8, 45);
    private final JTextArea keyArea = new JTextArea(9, 45);
    private final JTextArea logArea = new JTextArea(14, 45);
    private final JTextArea outputArea = new JTextArea(8, 45);
    private final JCheckBox autoGenerateKeyBox =
            new JCheckBox("每次执行前按当前算法/种子生成对称密钥", true);

    private final KeyMaterial keyMaterial = new KeyMaterial();
    private final CryptoWorkflow cryptoWorkflow = new CryptoWorkflow();
    private final CryptoPackageCodec packageCodec = new CryptoPackageCodec();
    private KeyPair senderKeyPair;
    private KeyPair receiverKeyPair;
    private SecretKey symmetricKey;
    private String symmetricKeyAlgorithm = AlgorithmCatalog.AES;
    private File selectedFile;
    private CryptoPackage lastPackage;
    private byte[] lastDecrypted;

    public CryptoTaskFrame() {
        super("加密与数字签名综合实验");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        buildUi();
        initializeDefaults();
        pack();
        setLocationRelativeTo(null);
    }

    private void buildUi() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setContentPane(root);

        JLabel title = new JLabel("A 端签名并加密，B 端解密并验签");
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        root.add(title, BorderLayout.NORTH);

        JPanel left = new JPanel(new BorderLayout(8, 8));
        left.add(buildOptionsPanel(), BorderLayout.NORTH);
        left.add(buildInputPanel(), BorderLayout.CENTER);
        left.add(buildButtonPanel(), BorderLayout.SOUTH);

        JPanel right = new JPanel(new BorderLayout(8, 8));
        configureTextAreas();
        JSplitPane rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                boxedScroll("密钥信息", keyArea), boxedScroll("过程日志", logArea));
        rightSplit.setResizeWeight(0.38);
        right.add(rightSplit, BorderLayout.CENTER);
        right.add(boxedScroll("B 端解密结果", outputArea), BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        split.setResizeWeight(0.45);
        root.add(split, BorderLayout.CENTER);
        setMinimumSize(new Dimension(1120, 720));
    }

    private void configureTextAreas() {
        keyArea.setEditable(false);
        keyArea.setLineWrap(true);
        keyArea.setWrapStyleWord(true);
        logArea.setEditable(false);
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
    }

    private JPanel buildOptionsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("参数选择"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;

        addRow(panel, c, 0, "输入类型", inputModeBox);
        addRow(panel, c, 1, "对称算法", symmetricBox);
        addRow(panel, c, 2, "Hash 算法", hashBox);
        addRow(panel, c, 3, "RSA 密钥长度", rsaSizeBox);
        addRow(panel, c, 4, "对称密钥种子", seedField);

        c.gridx = 1;
        c.gridy = 5;
        c.weightx = 1;
        panel.add(autoGenerateKeyBox, c);
        return panel;
    }

    private void addRow(JPanel panel, GridBagConstraints c, int row, String label, java.awt.Component field) {
        c.gridx = 0;
        c.gridy = row;
        c.weightx = 0;
        panel.add(new JLabel(label), c);
        c.gridx = 1;
        c.weightx = 1;
        panel.add(field, c);
    }

    private JPanel buildInputPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("明文输入"));
        inputArea.setText("这是一段待签名并加密的测试明文。");
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);

        JPanel filePanel = new JPanel(new BorderLayout(6, 0));
        fileField.setEditable(false);
        JButton chooseFileButton = new JButton("选择文件");
        chooseFileButton.addActionListener(e -> chooseInputFile());
        filePanel.add(fileField, BorderLayout.CENTER);
        filePanel.add(chooseFileButton, BorderLayout.EAST);

        panel.add(filePanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(inputArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton rsaButton = new JButton("生成 RSA 密钥对");
        JButton symmetricButton = new JButton("生成对称密钥");
        JButton runButton = new JButton("执行完整流程");
        JButton savePackageButton = new JButton("保存密文包");
        JButton openPackageButton = new JButton("打开密文包并验签");
        JButton savePlainButton = new JButton("保存解密结果");

        rsaButton.addActionListener(e -> generateRsaPairs());
        symmetricButton.addActionListener(e -> generateSymmetricKey());
        runButton.addActionListener(e -> runFullProcess());
        savePackageButton.addActionListener(e -> saveLastPackage());
        openPackageButton.addActionListener(e -> openPackageAndVerify());
        savePlainButton.addActionListener(e -> saveLastDecrypted());

        panel.add(rsaButton);
        panel.add(symmetricButton);
        panel.add(runButton);
        panel.add(savePackageButton);
        panel.add(openPackageButton);
        panel.add(savePlainButton);
        return panel;
    }

    private JScrollPane boxedScroll(String title, JTextArea area) {
        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createTitledBorder(title));
        return scroll;
    }

    private void initializeDefaults() {
        generateRsaPairs();
        generateSymmetricKey();
    }

    private void chooseInputFile() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile();
            fileField.setText(selectedFile.getAbsolutePath());
            inputModeBox.setSelectedItem("文件");
        }
    }

    private void generateRsaPairs() {
        try {
            int keySize = (Integer) rsaSizeBox.getSelectedItem();
            senderKeyPair = keyMaterial.generateRsaKeyPair(keySize);
            receiverKeyPair = keyMaterial.generateRsaKeyPair(keySize);
            appendLog("已生成 A/B 两组 RSA 密钥对，长度 " + keySize + " 位。");
            refreshKeyArea();
        } catch (Exception ex) {
            showError("生成 RSA 密钥对失败", ex);
        }
    }

    private void generateSymmetricKey() {
        try {
            String algorithm = (String) symmetricBox.getSelectedItem();
            symmetricKeyAlgorithm = algorithm;
            String seed = seedField.getText().trim();
            symmetricKey = keyMaterial.generateSymmetricKey(algorithm, seed);
            appendLog("已生成 " + algorithm + " 对称密钥"
                    + (seed.isEmpty() ? "（随机）" : "（由种子派生）") + "。");
            refreshKeyArea();
        } catch (Exception ex) {
            showError("生成对称密钥失败", ex);
        }
    }

    private void runFullProcess() {
        try {
            ensureKeysReady();
            if (shouldRegenerateSymmetricKey()) {
                generateSymmetricKey();
            }
            WorkflowRequest request = new WorkflowRequest(
                    readPlainInput(),
                    (String) symmetricBox.getSelectedItem(),
                    (String) hashBox.getSelectedItem(),
                    symmetricKey,
                    senderKeyPair,
                    receiverKeyPair);
            WorkflowResult result = cryptoWorkflow.run(request);
            lastPackage = result.cryptoPackage();
            lastDecrypted = result.verificationResult().plainText();
            outputArea.setText(OutputFormatter.format(lastDecrypted, isFileMode(), selectedFile));
            appendWorkflowLog(result);
        } catch (Exception ex) {
            showError("执行完整流程失败", ex);
        }
    }

    private boolean shouldRegenerateSymmetricKey() {
        return autoGenerateKeyBox.isSelected()
                || !symmetricKeyAlgorithm.equals(symmetricBox.getSelectedItem());
    }

    private void ensureKeysReady() {
        if (senderKeyPair == null || receiverKeyPair == null) {
            generateRsaPairs();
        }
        if (symmetricKey == null) {
            generateSymmetricKey();
        }
    }

    private byte[] readPlainInput() throws Exception {
        if (isFileMode()) {
            if (selectedFile == null) {
                throw new IllegalStateException("请先选择要加密和签名的文件。");
            }
            return Files.readAllBytes(selectedFile.toPath());
        }
        return inputArea.getText().getBytes(StandardCharsets.UTF_8);
    }

    private boolean isFileMode() {
        return "文件".equals(inputModeBox.getSelectedItem());
    }

    private void appendWorkflowLog(WorkflowResult result) {
        String symmetricAlgorithm = result.cryptoPackage().symmetricAlgorithm();
        String hashAlgorithm = result.cryptoPackage().hashAlgorithm();
        appendLog("");
        appendLog("A: 计算 H(M) = " + ByteFormats.toHex(result.messageHash()));
        appendLog("A: 使用 RK_A 对 H(M) 加密得到数字签名，长度 " + result.signature().length + " 字节。");
        appendLog("A: 组合 M || 签名，组合后长度 " + result.combinedLength() + " 字节。");
        appendLog("A: 使用 " + symmetricAlgorithm + " 和 K 加密 M || 签名，密文长度 "
                + result.encryptedPayloadLength() + " 字节。");
        appendLog("A: 使用 UK_B 加密 K，密钥密文长度 " + result.encryptedKeyLength() + " 字节。");
        appendLog("B: 使用 RK_B 解出 K，再用 K 解出 M || 签名。");
        appendLog("B: 使用 UK_A 解出签名中的 Hash，并与重新计算的 " + hashAlgorithm + " 比较。");
        appendLog(result.verificationResult().verified() ? "验签结果：通过。" : "验签结果：失败。");
    }

    private void saveLastPackage() {
        if (lastPackage == null) {
            JOptionPane.showMessageDialog(this, "请先执行完整流程生成密文包。");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("message.crypto"));
        chooser.setFileFilter(new FileNameExtensionFilter("Crypto package (*.crypto)", "crypto"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                Files.write(chooser.getSelectedFile().toPath(), packageCodec.encode(lastPackage));
                appendLog("已保存密文包：" + chooser.getSelectedFile().getAbsolutePath());
            } catch (Exception ex) {
                showError("保存密文包失败", ex);
            }
        }
    }

    private void openPackageAndVerify() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Crypto package (*.crypto)", "crypto"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                CryptoPackage cryptoPackage = packageCodec.decode(
                        Files.readAllBytes(chooser.getSelectedFile().toPath()));
                VerificationResult result = cryptoWorkflow.decryptAndVerify(
                        cryptoPackage, senderKeyPair, receiverKeyPair);
                lastPackage = cryptoPackage;
                lastDecrypted = result.plainText();
                outputArea.setText(OutputFormatter.format(lastDecrypted, isFileMode(), selectedFile));
                appendLog("已打开密文包：" + chooser.getSelectedFile().getAbsolutePath());
                appendLog(result.verified() ? "验签结果：通过。" : "验签结果：失败。");
            } catch (CryptoException ex) {
                showError("打开密文包或验签失败", ex);
            } catch (Exception ex) {
                showError("读取密文包失败", ex);
            }
        }
    }

    private void saveLastDecrypted() {
        if (lastDecrypted == null) {
            JOptionPane.showMessageDialog(this, "暂无解密结果。");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(selectedFile == null
                ? "decrypted.txt" : "decrypted-" + selectedFile.getName()));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                Files.write(chooser.getSelectedFile().toPath(), lastDecrypted);
                appendLog("已保存解密结果：" + chooser.getSelectedFile().getAbsolutePath());
            } catch (Exception ex) {
                showError("保存解密结果失败", ex);
            }
        }
    }

    private void refreshKeyArea() {
        StringBuilder builder = new StringBuilder();
        if (symmetricKey != null) {
            builder.append("对称算法: ").append(symmetricKeyAlgorithm).append('\n');
            builder.append("K(Base64): ")
                    .append(Base64.getEncoder().encodeToString(symmetricKey.getEncoded()))
                    .append("\n\n");
        }
        if (senderKeyPair != null) {
            builder.append("A 私钥 RK_A(Base64): ")
                    .append(ByteFormats.shortBase64(senderKeyPair.getPrivate().getEncoded())).append('\n');
            builder.append("A 公钥 UK_A(Base64): ")
                    .append(ByteFormats.shortBase64(senderKeyPair.getPublic().getEncoded())).append("\n\n");
        }
        if (receiverKeyPair != null) {
            builder.append("B 私钥 RK_B(Base64): ")
                    .append(ByteFormats.shortBase64(receiverKeyPair.getPrivate().getEncoded())).append('\n');
            builder.append("B 公钥 UK_B(Base64): ")
                    .append(ByteFormats.shortBase64(receiverKeyPair.getPublic().getEncoded())).append('\n');
        }
        keyArea.setText(builder.toString());
    }

    private void appendLog(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private void showError(String title, Exception ex) {
        appendLog(title + "：" + ex.getMessage());
        JOptionPane.showMessageDialog(this, ex.getMessage(), title, JOptionPane.ERROR_MESSAGE);
    }
}
