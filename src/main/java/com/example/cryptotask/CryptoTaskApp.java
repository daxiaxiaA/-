package com.example.cryptotask;

import com.example.cryptotask.selftest.SelfTestRunner;
import com.example.cryptotask.ui.CryptoTaskFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public final class CryptoTaskApp {
    private CryptoTaskApp() {
    }

    public static void main(String[] args) {
        if (args.length > 0 && "--self-test".equals(args[0])) {
            SelfTestRunner.runFromCommandLine();
            return;
        }
        SwingUtilities.invokeLater(CryptoTaskApp::showWindow);
    }

    private static void showWindow() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Swing can continue with its default look and feel.
        }
        new CryptoTaskFrame().setVisible(true);
    }
}
