package com.bankoftuc.util;

import java.util.Scanner;

/**
 * Utility class for console UI formatting and input handling.
 */
public class ConsoleUI {
    
    private static final String SEPARATOR = "═══════════════════════════════════════════════════════════════";
    private static final String THIN_SEPARATOR = "───────────────────────────────────────────────────────────────";
    
    private Scanner scanner;
    
    public ConsoleUI() {
        this.scanner = new Scanner(System.in);
    }
    
    public void printHeader(String title) {
        System.out.println();
        System.out.println(SEPARATOR);
        System.out.println("  " + title);
        System.out.println(SEPARATOR);
    }
    
    public void printSubHeader(String title) {
        System.out.println();
        System.out.println(THIN_SEPARATOR);
        System.out.println("  " + title);
        System.out.println(THIN_SEPARATOR);
    }
    
    public void printSeparator() {
        System.out.println(THIN_SEPARATOR);
    }
    
    public void printMenuOption(int number, String description) {
        System.out.printf("  [%d] %s%n", number, description);
    }
    
    public void printMenuOption(String key, String description) {
        System.out.printf("  [%s] %s%n", key, description);
    }
    
    public void printSuccess(String message) {
        System.out.println("[OK] " + message);
    }
    
    public void printError(String message) {
        System.out.println("[ERROR] " + message);
    }
    
    public void printWarning(String message) {
        System.out.println("[WARNING] " + message);
    }
    
    public void printInfo(String message) {
        System.out.println("[INFO] " + message);
    }
    
    public void printBlank() {
        System.out.println();
    }
    
    public String readString(String prompt) {
        System.out.print(prompt + ": ");
        return scanner.nextLine().trim();
    }
    
    public String readPassword(String prompt) {
        System.out.print(prompt + ": ");
        return scanner.nextLine().trim();
    }
    
    public int readInt(String prompt) {
        while (true) {
            System.out.print(prompt + ": ");
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                printError("Please enter a valid number");
            }
        }
    }
    
    public int readIntInRange(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt);
            if (value >= min && value <= max) {
                return value;
            }
            printError("Please enter a number between " + min + " and " + max);
        }
    }
    
    public double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt + ": ");
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                printError("Please enter a valid number");
            }
        }
    }
    
    public double readPositiveDouble(String prompt) {
        while (true) {
            double value = readDouble(prompt);
            if (value > 0) {
                return value;
            }
            printError("Please enter a positive number");
        }
    }
    
    public boolean readYesNo(String prompt) {
        while (true) {
            System.out.print(prompt + " (y/n): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y") || input.equals("yes")) {
                return true;
            } else if (input.equals("n") || input.equals("no")) {
                return false;
            }
            printError("Please enter 'y' or 'n'");
        }
    }
    
    public void waitForEnter() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    public void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
    
    public void printTableRow(String... columns) {
        StringBuilder sb = new StringBuilder();
        for (String col : columns) {
            sb.append(String.format("%-20s", col));
        }
        System.out.println(sb.toString());
    }
    
    public void close() {
        scanner.close();
    }
}
