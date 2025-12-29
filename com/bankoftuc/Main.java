package com.bankoftuc;

import com.bankoftuc.ui.BankingCLI;
import java.util.Scanner;

/**
 * Main entry point for the Bank of TUC eBanking System.
 * 
 * Bank of TUC - eBanking System
 * A simulation of a modern electronic banking system designed for 
 * managing customers, businesses, and administrators through a 
 * secure digital environment.
 * 
 * Features:
 * - User Management (Individual, Business, Admin)
 * - Account Management (Personal & Business Accounts)
 * - Transactions (Deposits, Withdrawals, Transfers)
 * - SEPA and SWIFT International Transfers
 * - Bill Management and Payment
 * - Standing Orders (Recurring Payments & Transfers)
 * - Time Simulation for Demo/Testing
 * 
 * Supports both Command Line Interface (CLI) and Graphical User Interface (GUI)
 * 
 * @author Bank of TUC Development Team
 * @version 1.0
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println();
        System.out.println("  ____              _              __   _____ _   _  ____");
        System.out.println(" | __ )  __ _ _ __ | | __    ___  / _| |_   _| | | |/ ___|");
        System.out.println(" |  _ \\ / _` | '_ \\| |/ /   / _ \\ |_     | | | | | | |    ");
        System.out.println(" | |_) | (_| | | | |   <   | (_) |  _|   | | | |_| | |___ ");
        System.out.println(" |____/ \\__,_|_| |_|_|\\_\\   \\___/|_|     |_|  \\___/ \\____|");
        System.out.println();
        System.out.println("              eBanking System v1.0");
        System.out.println("         Technical University of Crete");
        System.out.println();
        
        // Check command line arguments
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("--gui") || args[0].equalsIgnoreCase("-g")) {
                startGUI(args);
                return;
            } else if (args[0].equalsIgnoreCase("--cli") || args[0].equalsIgnoreCase("-c")) {
                startCLI();
                return;
            } else if (args[0].equalsIgnoreCase("--help") || args[0].equalsIgnoreCase("-h")) {
                printHelp();
                return;
            }
        }
        
        // Interactive mode selection
        System.out.println("Select Interface Mode:");
        System.out.println("  [1] Command Line Interface (CLI)");
        System.out.println("  [2] Graphical User Interface (GUI) - Requires JavaFX");
        System.out.println();
        System.out.print("Enter choice (1 or 2): ");
        
        Scanner scanner = new Scanner(System.in);
        String choice = scanner.nextLine().trim();
        
        if (choice.equals("2") || choice.equalsIgnoreCase("gui")) {
            startGUI(args);
        } else {
            startCLI();
        }
    }
    
    /**
     * Start the Command Line Interface
     */
    private static void startCLI() {
        System.out.println("\nStarting Command Line Interface...\n");
        BankingCLI cli = new BankingCLI();
        cli.start();
    }
    
    /**
     * Start the Graphical User Interface
     */
    private static void startGUI(String[] args) {
        System.out.println("\nStarting Graphical User Interface...\n");
        try {
            // Use reflection to avoid compile-time dependency on JavaFX
            Class<?> guiClass = Class.forName("com.bankoftuc.gui.BankingGUI");
            java.lang.reflect.Method mainMethod = guiClass.getMethod("main", String[].class);
            mainMethod.invoke(null, (Object) args);
        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: GUI class not found. Make sure BankingGUI.java is compiled.");
            System.err.println("Falling back to CLI...\n");
            startCLI();
        } catch (Exception e) {
            System.err.println("ERROR: Could not start GUI. JavaFX may not be available.");
            System.err.println("To run GUI, you need:");
            System.err.println("  1. JavaFX SDK installed");
            System.err.println("  2. Add JavaFX modules to module path");
            System.err.println();
            System.err.println("Example command:");
            System.err.println("  java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml com.bankoftuc.gui.BankingGUI");
            System.err.println();
            System.err.println("Falling back to CLI...\n");
            startCLI();
        }
    }
    
    /**
     * Print help information
     */
    private static void printHelp() {
        System.out.println("Usage: java com.bankoftuc.Main [OPTIONS]");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  --cli, -c    Start with Command Line Interface");
        System.out.println("  --gui, -g    Start with Graphical User Interface (requires JavaFX)");
        System.out.println("  --help, -h   Show this help message");
        System.out.println();
        System.out.println("If no option is provided, you will be prompted to choose.");
        System.out.println();
        System.out.println("GUI Requirements:");
        System.out.println("  - JavaFX SDK must be installed separately (Java 11+)");
        System.out.println("  - Run with: java --module-path /path/to/javafx/lib --add-modules javafx.controls com.bankoftuc.gui.BankingGUI");
    }
}
