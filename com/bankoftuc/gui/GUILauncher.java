package com.bankoftuc.gui;

/**
 * Separate launcher for the GUI application.
 * Use this if you want to start the GUI directly.
 * 
 * Run with: java com.bankoftuc.gui.GUILauncher
 */
public class GUILauncher {
    public static void main(String[] args) {
        System.out.println("Launching Bank of TUC - GUI Mode...");
        BankingGUI.main(args);
    }
}
