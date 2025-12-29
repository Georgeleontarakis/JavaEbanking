package com.bankoftuc.gui;

// JavaFX Imports 
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*; // Imports Label, Button, TableView, etc.
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Callback;

// Project Imports
import com.bankoftuc.manager.BankSystem;
import com.bankoftuc.model.*;

// Java Utility Imports
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

/**
 * Main JavaFX Application for Bank of TUC eBanking System.
 * Provides graphical user interface as required by specifications.
 */
public class BankingGUI extends Application {
    
    private BankSystem bankSystem;
    private User currentUser;
    private Stage primaryStage;
    
    // Styling constants
    private static final String STYLE_BUTTON = "-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;";
    private static final String STYLE_BUTTON_DANGER = "-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;";
    private static final String STYLE_BUTTON_SUCCESS = "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;";
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.bankSystem = BankSystem.getInstance();
        
        primaryStage.setTitle("Bank of TUC - eBanking System");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        
        showLoginScreen();
        
        primaryStage.show();
    }
    
    /**
     * Show the login screen
     */
    private void showLoginScreen() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #1a237e, #283593);");
        
        // Title
        Label titleLabel = new Label("Bank of TUC");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        titleLabel.setStyle("-fx-text-fill: white;");
        
        Label subtitleLabel = new Label("eBanking System v1.0");
        subtitleLabel.setFont(Font.font("Arial", 18));
        subtitleLabel.setStyle("-fx-text-fill: #90CAF9;");
        
        // Login form
        VBox loginBox = new VBox(15);
        loginBox.setAlignment(Pos.CENTER);
        loginBox.setMaxWidth(350);
        loginBox.setPadding(new Insets(30));
        loginBox.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        
        Label loginTitle = new Label("Login");
        loginTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setStyle("-fx-font-size: 14px; -fx-padding: 10;");
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle("-fx-font-size: 14px; -fx-padding: 10;");
        
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");
        
        Button loginButton = new Button("Login");
        loginButton.setStyle(STYLE_BUTTON);
        loginButton.setMaxWidth(Double.MAX_VALUE);
        
        Label systemDateLabel = new Label("System Date: " + bankSystem.getCurrentDate());
        systemDateLabel.setStyle("-fx-text-fill: gray;");
        
        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            
            if (username.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Please enter username and password");
                return;
            }
            
            boolean success = bankSystem.getAuthManager().login(username, password);
            if (success) {
                currentUser = bankSystem.getAuthManager().getCurrentUser();
                showMainScreen();
            } else {
                errorLabel.setText("Invalid username or password");
                passwordField.clear();
            }
        });
        
        // Enter key to login
        passwordField.setOnAction(e -> loginButton.fire());
        
        loginBox.getChildren().addAll(loginTitle, usernameField, passwordField, errorLabel, loginButton, systemDateLabel);
        
        root.getChildren().addAll(titleLabel, subtitleLabel, loginBox);
        
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
    }
    
    /**
     * Show main screen based on user type
     */
    private void showMainScreen() {
        if (currentUser instanceof IndividualUser) {
            showIndividualDashboard((IndividualUser) currentUser);
        } else if (currentUser instanceof BusinessUser) {
            showBusinessDashboard((BusinessUser) currentUser);
        } else if (currentUser instanceof AdminUser) {
            showAdminDashboard((AdminUser) currentUser);
        }
    }
    
    /**
     * Show Individual User Dashboard
     */
    private void showIndividualDashboard(IndividualUser user) {
        BorderPane root = new BorderPane();
        
        // Top bar
        HBox topBar = createTopBar("Individual Banking - " + user.getFullName());
        root.setTop(topBar);
        
        // Left menu
        VBox menu = new VBox(5);
        menu.setPadding(new Insets(10));
        menu.setStyle("-fx-background-color: #f5f5f5;");
        menu.setPrefWidth(200);
        
        Button btnOverview = createMenuButton("📊 Account Overview");
        Button btnDeposit = createMenuButton("💰 Deposit");
        Button btnWithdraw = createMenuButton("💸 Withdraw");
        Button btnTransfer = createMenuButton("🔄 Transfer");
        Button btnSepa = createMenuButton("🇪🇺 SEPA Transfer");
        Button btnSwift = createMenuButton("🌍 SWIFT Transfer");
        Button btnBills = createMenuButton("📄 View Bills");
        Button btnPayBill = createMenuButton("💳 Pay Bill");
        Button btnStatements = createMenuButton("📋 Statements");
        Button btnStandingOrders = createMenuButton("⏰ Standing Orders");
        
        menu.getChildren().addAll(btnOverview, new Separator(), btnDeposit, btnWithdraw, 
            btnTransfer, new Separator(), btnSepa, btnSwift, new Separator(),
            btnBills, btnPayBill, new Separator(), btnStatements, btnStandingOrders);
        
        root.setLeft(menu);
        
        // Content area
        StackPane content = new StackPane();
        content.setPadding(new Insets(20));
        root.setCenter(content);
        
        // Button actions
        btnOverview.setOnAction(e -> showAccountOverview(content, user));
        btnDeposit.setOnAction(e -> showDepositForm(content, user));
        btnWithdraw.setOnAction(e -> showWithdrawForm(content, user));
        btnTransfer.setOnAction(e -> showTransferForm(content, user));
        btnSepa.setOnAction(e -> showSepaForm(content, user));
        btnSwift.setOnAction(e -> showSwiftForm(content, user));
        btnBills.setOnAction(e -> showBills(content, user));
        btnPayBill.setOnAction(e -> showPayBillForm(content, user));
        btnStatements.setOnAction(e -> showStatements(content, user));
        btnStandingOrders.setOnAction(e -> showStandingOrders(content, user));
        
        // Show overview by default
        showAccountOverview(content, user);
        
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setScene(scene);
    }
    
    /**
     * Show Business User Dashboard
     */
    private void showBusinessDashboard(BusinessUser user) {
        BorderPane root = new BorderPane();
        
        // Top bar
        HBox topBar = createTopBar("Business Banking - " + user.getBusinessName());
        root.setTop(topBar);
        
        // Left menu
        VBox menu = new VBox(5);
        menu.setPadding(new Insets(10));
        menu.setStyle("-fx-background-color: #f5f5f5;");
        menu.setPrefWidth(200);
        
        Button btnOverview = createMenuButton("📊 Account Overview");
        Button btnDeposit = createMenuButton("💰 Deposit");
        Button btnWithdraw = createMenuButton("💸 Withdraw");
        Button btnTransfer = createMenuButton("🔄 Transfer");
        Button btnSepa = createMenuButton("🇪🇺 SEPA Transfer");
        Button btnSwift = createMenuButton("🌍 SWIFT Transfer");
        Button btnIssueBill = createMenuButton("📝 Issue Bill");
        Button btnViewBills = createMenuButton("📄 Issued Bills");
        Button btnStatements = createMenuButton("📋 Statements");
        
        menu.getChildren().addAll(btnOverview, new Separator(), btnDeposit, btnWithdraw, 
            btnTransfer, new Separator(), btnSepa, btnSwift, new Separator(),
            btnIssueBill, btnViewBills, new Separator(), btnStatements);
        
        root.setLeft(menu);
        
        // Content area
        StackPane content = new StackPane();
        content.setPadding(new Insets(20));
        root.setCenter(content);
        
        // Button actions
        btnOverview.setOnAction(e -> showBusinessOverview(content, user));
        btnDeposit.setOnAction(e -> showBusinessDeposit(content, user));
        btnWithdraw.setOnAction(e -> showBusinessWithdraw(content, user));
        btnTransfer.setOnAction(e -> showBusinessTransfer(content, user));
        btnSepa.setOnAction(e -> showBusinessSepa(content, user));
        btnSwift.setOnAction(e -> showBusinessSwift(content, user));
        btnIssueBill.setOnAction(e -> showIssueBillForm(content, user));
        btnViewBills.setOnAction(e -> showIssuedBills(content, user));
        btnStatements.setOnAction(e -> showBusinessStatements(content, user));
        
        // Show overview by default
        showBusinessOverview(content, user);
        
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setScene(scene);
    }
    
    /**
     * Show Admin Dashboard
     */
    private void showAdminDashboard(AdminUser user) {
        BorderPane root = new BorderPane();
        
        // Top bar
        HBox topBar = createTopBar("Admin Panel - " + user.getUsername());
        root.setTop(topBar);
        
        // Left menu
        VBox menu = new VBox(5);
        menu.setPadding(new Insets(10));
        menu.setStyle("-fx-background-color: #f5f5f5;");
        menu.setPrefWidth(200);
        
        Button btnUsers = createMenuButton("👥 Manage Users");
        Button btnAccounts = createMenuButton("🏦 View Accounts");
        Button btnTransactions = createMenuButton("📊 All Transactions");
        Button btnSimulate = createMenuButton("⏩ Time Simulation");
        Button btnSystemInfo = createMenuButton("ℹ️ System Info");
        
        menu.getChildren().addAll(btnUsers, btnAccounts, btnTransactions, 
            new Separator(), btnSimulate, btnSystemInfo);
        
        root.setLeft(menu);
        
        // Content area
        StackPane content = new StackPane();
        content.setPadding(new Insets(20));
        root.setCenter(content);
        
        // Button actions
        btnUsers.setOnAction(e -> showUserManagement(content));
        btnAccounts.setOnAction(e -> showAllAccounts(content));
        btnTransactions.setOnAction(e -> showAllTransactions(content));
        btnSimulate.setOnAction(e -> showTimeSimulation(content));
        btnSystemInfo.setOnAction(e -> showSystemInfo(content));
        
        // Show users by default
        showUserManagement(content);
        
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setScene(scene);
    }
    
    /**
     * Create top navigation bar
     */
    private HBox createTopBar(String title) {
        HBox topBar = new HBox(20);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(15, 25, 15, 25));
        topBar.setStyle("-fx-background-color: #1a237e;");
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setStyle("-fx-text-fill: white;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label dateLabel = new Label("Date: " + bankSystem.getCurrentDate());
        dateLabel.setStyle("-fx-text-fill: #90CAF9;");
        
        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        logoutBtn.setOnAction(e -> {
            bankSystem.getAuthManager().logout();
            currentUser = null;
            showLoginScreen();
        });
        
        topBar.getChildren().addAll(titleLabel, spacer, dateLabel, logoutBtn);
        return topBar;
    }
    
    /**
     * Create menu button with consistent styling
     */
    private Button createMenuButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle("-fx-background-color: transparent; -fx-font-size: 13px; -fx-padding: 10;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #e3f2fd; -fx-font-size: 13px; -fx-padding: 10;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-font-size: 13px; -fx-padding: 10;"));
        return btn;
    }
    
    /**
     * Show account overview for individual user
     */
    private void showAccountOverview(StackPane content, IndividualUser user) {
        content.getChildren().clear();
        
        VBox vbox = new VBox(15);
        vbox.setAlignment(Pos.TOP_LEFT);
        
        Label title = new Label("Account Overview");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        var accounts = bankSystem.getAccountManager().getAccountsForIndividualUser(user);
        
        if (accounts.isEmpty()) {
            vbox.getChildren().addAll(title, new Label("You don't have any accounts."));
        } else {
            VBox accountsList = new VBox(10);
            
            for (PersonalAccount acc : accounts) {
                VBox accBox = new VBox(5);
                accBox.setPadding(new Insets(15));
                accBox.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-background-radius: 5;");
                
                Label ibanLabel = new Label("IBAN: " + acc.getIban());
                ibanLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                
                Label balanceLabel = new Label(String.format("Balance: €%.2f", acc.getBalance()));
                balanceLabel.setFont(Font.font("Arial", 18));
                balanceLabel.setStyle("-fx-text-fill: #2e7d32;");
                
                Label statusLabel = new Label("Status: " + acc.getStatus());
                Label interestLabel = new Label(String.format("Interest Rate: %.2f%%", acc.getInterestRate().doubleValue() * 100));
                
                accBox.getChildren().addAll(ibanLabel, balanceLabel, statusLabel, interestLabel);
                accountsList.getChildren().add(accBox);
            }
            
            vbox.getChildren().addAll(title, accountsList);
        }
        
        ScrollPane scrollPane = new ScrollPane(vbox);
        scrollPane.setFitToWidth(true);
        content.getChildren().add(scrollPane);
    }
    
    /**
     * Show deposit form
     */
    private void showDepositForm(StackPane content, IndividualUser user) {
        content.getChildren().clear();
        
        VBox vbox = new VBox(15);
        vbox.setMaxWidth(400);
        
        Label title = new Label("Deposit Money");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        var accounts = bankSystem.getAccountManager().getAccountsForIndividualUser(user);
        
        if (accounts.isEmpty()) {
            vbox.getChildren().addAll(title, new Label("You don't have any accounts."));
        } else {
            ComboBox<String> accountCombo = new ComboBox<>();
            for (PersonalAccount acc : accounts) {
                accountCombo.getItems().add(acc.getIban() + " (€" + String.format("%.2f", acc.getBalance()) + ")");
            }
            accountCombo.setPromptText("Select Account");
            accountCombo.setMaxWidth(Double.MAX_VALUE);
            
            TextField amountField = new TextField();
            amountField.setPromptText("Amount (EUR)");
            
            Label resultLabel = new Label();
            
            Button depositBtn = new Button("Deposit");
            depositBtn.setStyle(STYLE_BUTTON_SUCCESS);
            
            depositBtn.setOnAction(e -> {
                try {
                    int idx = accountCombo.getSelectionModel().getSelectedIndex();
                    if (idx < 0) {
                        resultLabel.setText("Please select an account");
                        resultLabel.setStyle("-fx-text-fill: red;");
                        return;
                    }
                    
                    double amount = Double.parseDouble(amountField.getText());
                    if (amount <= 0) {
                        resultLabel.setText("Amount must be positive");
                        resultLabel.setStyle("-fx-text-fill: red;");
                        return;
                    }
                    
                    PersonalAccount acc = accounts.get(idx);
                    bankSystem.getTransactionManager().deposit(acc, new java.math.BigDecimal(amount), "GUI Deposit");
                    bankSystem.saveToFile();
                    
                    resultLabel.setText(String.format("Successfully deposited €%.2f", amount));
                    resultLabel.setStyle("-fx-text-fill: green;");
                    amountField.clear();
                    
                    // Refresh combo box
                    accountCombo.getItems().clear();
                    for (PersonalAccount a : accounts) {
                        accountCombo.getItems().add(a.getIban() + " (€" + String.format("%.2f", a.getBalance()) + ")");
                    }
                    accountCombo.getSelectionModel().select(idx);
                    
                } catch (NumberFormatException ex) {
                    resultLabel.setText("Invalid amount");
                    resultLabel.setStyle("-fx-text-fill: red;");
                }
            });
            
            vbox.getChildren().addAll(title, new Label("Account:"), accountCombo, 
                new Label("Amount:"), amountField, depositBtn, resultLabel);
        }
        
        content.getChildren().add(vbox);
    }
    
    /**
     * Show withdraw form
     */
    private void showWithdrawForm(StackPane content, IndividualUser user) {
        content.getChildren().clear();
        
        VBox vbox = new VBox(15);
        vbox.setMaxWidth(400);
        
        Label title = new Label("Withdraw Money");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        var accounts = bankSystem.getAccountManager().getAccountsForIndividualUser(user);
        
        if (accounts.isEmpty()) {
            vbox.getChildren().addAll(title, new Label("You don't have any accounts."));
        } else {
            ComboBox<String> accountCombo = new ComboBox<>();
            for (PersonalAccount acc : accounts) {
                accountCombo.getItems().add(acc.getIban() + " (€" + String.format("%.2f", acc.getBalance()) + ")");
            }
            accountCombo.setPromptText("Select Account");
            accountCombo.setMaxWidth(Double.MAX_VALUE);
            
            TextField amountField = new TextField();
            amountField.setPromptText("Amount (EUR)");
            
            Label resultLabel = new Label();
            
            Button withdrawBtn = new Button("Withdraw");
            withdrawBtn.setStyle(STYLE_BUTTON_DANGER);
            
            withdrawBtn.setOnAction(e -> {
                try {
                    int idx = accountCombo.getSelectionModel().getSelectedIndex();
                    if (idx < 0) {
                        resultLabel.setText("Please select an account");
                        resultLabel.setStyle("-fx-text-fill: red;");
                        return;
                    }
                    
                    double amount = Double.parseDouble(amountField.getText());
                    PersonalAccount acc = accounts.get(idx);
                    
                    bankSystem.getTransactionManager().withdraw(acc, new java.math.BigDecimal(amount), "GUI Withdrawal");
                    bankSystem.saveToFile();
                    
                    resultLabel.setText(String.format("Successfully withdrew €%.2f", amount));
                    resultLabel.setStyle("-fx-text-fill: green;");
                    amountField.clear();
                    
                    // Refresh
                    accountCombo.getItems().clear();
                    for (PersonalAccount a : accounts) {
                        accountCombo.getItems().add(a.getIban() + " (€" + String.format("%.2f", a.getBalance()) + ")");
                    }
                    accountCombo.getSelectionModel().select(idx);
                    
                } catch (NumberFormatException ex) {
                    resultLabel.setText("Invalid amount");
                    resultLabel.setStyle("-fx-text-fill: red;");
                } catch (Exception ex) {
                    resultLabel.setText(ex.getMessage());
                    resultLabel.setStyle("-fx-text-fill: red;");
                }
            });
            
            vbox.getChildren().addAll(title, new Label("Account:"), accountCombo, 
                new Label("Amount:"), amountField, withdrawBtn, resultLabel);
        }
        
        content.getChildren().add(vbox);
    }
    
    /**
     * Show transfer form
     */
    private void showTransferForm(StackPane content, IndividualUser user) {
        content.getChildren().clear();
        
        VBox vbox = new VBox(15);
        vbox.setMaxWidth(400);
        
        Label title = new Label("Internal Transfer");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        var accounts = bankSystem.getAccountManager().getAccountsForIndividualUser(user);
        
        if (accounts.isEmpty()) {
            vbox.getChildren().addAll(title, new Label("You don't have any accounts."));
        } else {
            ComboBox<String> fromCombo = new ComboBox<>();
            for (PersonalAccount acc : accounts) {
                fromCombo.getItems().add(acc.getIban() + " (€" + String.format("%.2f", acc.getBalance()) + ")");
            }
            fromCombo.setPromptText("From Account");
            fromCombo.setMaxWidth(Double.MAX_VALUE);
            
            TextField toIbanField = new TextField();
            toIbanField.setPromptText("Destination IBAN");
            
            TextField amountField = new TextField();
            amountField.setPromptText("Amount (EUR)");
            
            TextField descField = new TextField();
            descField.setPromptText("Description");
            
            Label resultLabel = new Label();
            
            Button transferBtn = new Button("Transfer");
            transferBtn.setStyle(STYLE_BUTTON);
            
            transferBtn.setOnAction(e -> {
                try {
                    int idx = fromCombo.getSelectionModel().getSelectedIndex();
                    if (idx < 0) {
                        resultLabel.setText("Please select source account");
                        resultLabel.setStyle("-fx-text-fill: red;");
                        return;
                    }
                    
                    String toIban = toIbanField.getText().trim();
                    Account toAcc = bankSystem.getAccountManager().findByIban(toIban);
                    if (toAcc == null) {
                        resultLabel.setText("Destination account not found");
                        resultLabel.setStyle("-fx-text-fill: red;");
                        return;
                    }
                    
                    double amount = Double.parseDouble(amountField.getText());
                    PersonalAccount fromAcc = accounts.get(idx);
                    
                    bankSystem.getTransactionManager().transfer(fromAcc, toAcc, 
                        new java.math.BigDecimal(amount), descField.getText());
                    bankSystem.saveToFile();
                    
                    resultLabel.setText(String.format("Successfully transferred €%.2f", amount));
                    resultLabel.setStyle("-fx-text-fill: green;");
                    amountField.clear();
                    toIbanField.clear();
                    descField.clear();
                    
                } catch (NumberFormatException ex) {
                    resultLabel.setText("Invalid amount");
                    resultLabel.setStyle("-fx-text-fill: red;");
                } catch (Exception ex) {
                    resultLabel.setText(ex.getMessage());
                    resultLabel.setStyle("-fx-text-fill: red;");
                }
            });
            
            vbox.getChildren().addAll(title, new Label("From:"), fromCombo, 
                new Label("To IBAN:"), toIbanField, new Label("Amount:"), amountField,
                new Label("Description:"), descField, transferBtn, resultLabel);
        }
        
        content.getChildren().add(vbox);
    }
    
    // Placeholder methods for other screens
    private void showSepaForm(StackPane content, IndividualUser user) {
        showTransferFormExternal(content, user, "SEPA");
    }
    
    private void showSwiftForm(StackPane content, IndividualUser user) {
        showTransferFormExternal(content, user, "SWIFT");
    }
    
    /**
     * Show SEPA/SWIFT transfer form
     */
    private void showTransferFormExternal(StackPane content, IndividualUser user, String type) {
        content.getChildren().clear();
        
        VBox vbox = new VBox(10);
        vbox.setMaxWidth(450);
        
        Label title = new Label(type + " Transfer");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        String fee = type.equals("SEPA") ? "€1.50" : "€25.00";
        Label feeLabel = new Label("Fee: " + fee + " | Success Rate: 75%");
        feeLabel.setStyle("-fx-text-fill: #666;");
        
        var accounts = bankSystem.getAccountManager().getAccountsForIndividualUser(user);
        
        if (accounts.isEmpty()) {
            vbox.getChildren().addAll(title, new Label("You don't have any accounts."));
        } else {
            ComboBox<String> fromCombo = new ComboBox<>();
            for (PersonalAccount acc : accounts) {
                fromCombo.getItems().add(acc.getIban() + " (€" + String.format("%.2f", acc.getBalance()) + ")");
            }
            fromCombo.setPromptText("From Account");
            fromCombo.setMaxWidth(Double.MAX_VALUE);
            
            TextField nameField = new TextField();
            nameField.setPromptText(type.equals("SEPA") ? "Creditor Name" : "Beneficiary Name");
            
            TextField ibanField = new TextField();
            ibanField.setPromptText(type.equals("SEPA") ? "Creditor IBAN" : "Beneficiary Account");
            
            TextField bankCodeField = new TextField();
            bankCodeField.setPromptText(type.equals("SEPA") ? "Bank BIC (e.g., ETHNGRAA)" : "SWIFT Code (e.g., BARCGB22)");
            
            TextField bankNameField = new TextField();
            bankNameField.setPromptText("Bank Name");
            
            TextField amountField = new TextField();
            amountField.setPromptText("Amount (EUR)");
            
            TextField descField = new TextField();
            descField.setPromptText("Description");
            
            Label resultLabel = new Label();
            resultLabel.setWrapText(true);
            
            Button transferBtn = new Button("Execute " + type + " Transfer");
            transferBtn.setStyle(STYLE_BUTTON);
            
            transferBtn.setOnAction(e -> {
                try {
                    int idx = fromCombo.getSelectionModel().getSelectedIndex();
                    if (idx < 0) {
                        resultLabel.setText("Please select source account");
                        resultLabel.setStyle("-fx-text-fill: red;");
                        return;
                    }
                    
                    double amount = Double.parseDouble(amountField.getText());
                    PersonalAccount fromAcc = accounts.get(idx);
                    
                    resultLabel.setText("Processing " + type + " transfer...");
                    resultLabel.setStyle("-fx-text-fill: blue;");
                    
                    if (type.equals("SEPA")) {
                        bankSystem.getTransactionManager().sepaTransferFull(
                            fromAcc, ibanField.getText(), new java.math.BigDecimal(amount),
                            descField.getText(), nameField.getText(), bankCodeField.getText(),
                            bankNameField.getText(), "SHA");
                    } else {
                        bankSystem.getTransactionManager().swiftTransferFull(
                            fromAcc, ibanField.getText(), new java.math.BigDecimal(amount),
                            descField.getText(), "EUR", nameField.getText(), "",
                            bankNameField.getText(), bankCodeField.getText(), "", "SHA");
                    }
                    bankSystem.saveToFile();
                    
                    resultLabel.setText(String.format("%s transfer of €%.2f completed successfully!", type, amount));
                    resultLabel.setStyle("-fx-text-fill: green;");
                    
                } catch (NumberFormatException ex) {
                    resultLabel.setText("Invalid amount");
                    resultLabel.setStyle("-fx-text-fill: red;");
                } catch (Exception ex) {
                    resultLabel.setText("Transfer failed: " + ex.getMessage());
                    resultLabel.setStyle("-fx-text-fill: red;");
                }
            });
            
            vbox.getChildren().addAll(title, feeLabel, new Separator(),
                new Label("From Account:"), fromCombo,
                new Label(type.equals("SEPA") ? "Creditor Name:" : "Beneficiary Name:"), nameField,
                new Label(type.equals("SEPA") ? "Creditor IBAN:" : "Beneficiary Account:"), ibanField,
                new Label(type.equals("SEPA") ? "Bank BIC:" : "SWIFT Code:"), bankCodeField,
                new Label("Bank Name:"), bankNameField,
                new Label("Amount (EUR):"), amountField,
                new Label("Description:"), descField,
                transferBtn, resultLabel);
        }
        
        ScrollPane scrollPane = new ScrollPane(vbox);
        scrollPane.setFitToWidth(true);
        content.getChildren().add(scrollPane);
    }
    
    /**
     * Show bills for user
     */
    private void showBills(StackPane content, IndividualUser user) {
        content.getChildren().clear();
        
        VBox vbox = new VBox(15);
        
        Label title = new Label("Your Bills");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        var bills = bankSystem.getBillManager().getBillsForUser(user);
        
        if (bills.isEmpty()) {
            vbox.getChildren().addAll(title, new Label("You don't have any bills."));
        } else {
            TableView<Bill> table = new TableView<>();
            
            TableColumn<Bill, String> idCol = new TableColumn<>("ID");
            idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getId()));
            
            TableColumn<Bill, String> providerCol = new TableColumn<>("Provider");
            providerCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getProviderName()));
            
            TableColumn<Bill, String> amountCol = new TableColumn<>("Amount");
            amountCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                String.format("€%.2f", data.getValue().getAmount())));
            
            TableColumn<Bill, String> dueCol = new TableColumn<>("Due Date");
            dueCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getDueDate().toString()));
            
            TableColumn<Bill, String> rfCol = new TableColumn<>("RF Code");
            rfCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getRfCode()));
            
            TableColumn<Bill, String> statusCol = new TableColumn<>("Status");
            statusCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getStatus().toString()));
            
            table.getColumns().addAll(idCol, providerCol, amountCol, dueCol, rfCol, statusCol);
            table.getItems().addAll(bills);
            table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            
            vbox.getChildren().addAll(title, table);
            VBox.setVgrow(table, Priority.ALWAYS);
        }
        
        content.getChildren().add(vbox);
    }
    
    /**
     * Show pay bill form
     */
    private void showPayBillForm(StackPane content, IndividualUser user) {
        content.getChildren().clear();
        
        VBox vbox = new VBox(15);
        vbox.setMaxWidth(400);
        
        Label title = new Label("Pay Bill");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        Label feeLabel = new Label("Fee: €0.50 per payment");
        feeLabel.setStyle("-fx-text-fill: #666;");
        
        var accounts = bankSystem.getAccountManager().getAccountsForIndividualUser(user);
        var unpaidBills = bankSystem.getBillManager().getUnpaidBillsForUser(user);
        
        if (accounts.isEmpty()) {
            vbox.getChildren().addAll(title, new Label("You don't have any accounts."));
        } else if (unpaidBills.isEmpty()) {
            vbox.getChildren().addAll(title, new Label("You don't have any unpaid bills."));
        } else {
            ComboBox<String> accountCombo = new ComboBox<>();
            for (PersonalAccount acc : accounts) {
                accountCombo.getItems().add(acc.getIban() + " (€" + String.format("%.2f", acc.getBalance()) + ")");
            }
            accountCombo.setPromptText("Select Account");
            accountCombo.setMaxWidth(Double.MAX_VALUE);
            
            ComboBox<String> billCombo = new ComboBox<>();
            for (Bill bill : unpaidBills) {
                billCombo.getItems().add(bill.getRfCode() + " - " + bill.getProviderName() + 
                    " (€" + String.format("%.2f", bill.getAmount()) + ")");
            }
            billCombo.setPromptText("Select Bill");
            billCombo.setMaxWidth(Double.MAX_VALUE);
            
            Label resultLabel = new Label();
            
            Button payBtn = new Button("Pay Bill");
            payBtn.setStyle(STYLE_BUTTON_SUCCESS);
            
            payBtn.setOnAction(e -> {
                try {
                    int accIdx = accountCombo.getSelectionModel().getSelectedIndex();
                    int billIdx = billCombo.getSelectionModel().getSelectedIndex();
                    
                    if (accIdx < 0 || billIdx < 0) {
                        resultLabel.setText("Please select account and bill");
                        resultLabel.setStyle("-fx-text-fill: red;");
                        return;
                    }
                    
                    PersonalAccount acc = accounts.get(accIdx);
                    Bill bill = unpaidBills.get(billIdx);
                    
                    java.math.BigDecimal totalAmount = bill.getAmount().add(new java.math.BigDecimal("0.50"));
                    
                    if (acc.getBalance().compareTo(totalAmount) < 0) {
                        resultLabel.setText("Insufficient funds");
                        resultLabel.setStyle("-fx-text-fill: red;");
                        return;
                    }
                    
                    acc.withdraw(totalAmount);
                    bankSystem.getBillManager().markBillAsPaid(bill, java.time.LocalDateTime.now());
                    bankSystem.saveToFile();
                    
                    resultLabel.setText("Bill paid successfully!");
                    resultLabel.setStyle("-fx-text-fill: green;");
                    
                    // Refresh
                    showPayBillForm(content, user);
                    
                } catch (Exception ex) {
                    resultLabel.setText(ex.getMessage());
                    resultLabel.setStyle("-fx-text-fill: red;");
                }
            });
            
            vbox.getChildren().addAll(title, feeLabel, new Separator(),
                new Label("Pay From:"), accountCombo,
                new Label("Bill:"), billCombo,
                payBtn, resultLabel);
        }
        
        content.getChildren().add(vbox);
    }
    
    /**
     * Show account statements
     */
    private void showStatements(StackPane content, IndividualUser user) {
        content.getChildren().clear();
        
        VBox vbox = new VBox(15);
        
        Label title = new Label("Account Statements");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        var accounts = bankSystem.getAccountManager().getAccountsForIndividualUser(user);
        
        if (accounts.isEmpty()) {
            vbox.getChildren().addAll(title, new Label("You don't have any accounts."));
        } else {
            ComboBox<String> accountCombo = new ComboBox<>();
            for (PersonalAccount acc : accounts) {
                accountCombo.getItems().add(acc.getIban());
            }
            accountCombo.setPromptText("Select Account");
            
            TableView<Transaction> table = new TableView<>();
            
            TableColumn<Transaction, String> dateCol = new TableColumn<>("Date");
            dateCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getDateTime().toLocalDate().toString()));
            
            TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
            typeCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getType().toString()));
            
            TableColumn<Transaction, String> amountCol = new TableColumn<>("Amount");
            amountCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                String.format("€%.2f", data.getValue().getAmount())));
            
            TableColumn<Transaction, String> descCol = new TableColumn<>("Description");
            descCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getDescription()));
            descCol.setPrefWidth(300);
            
            table.getColumns().addAll(dateCol, typeCol, amountCol, descCol);
            table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            
            accountCombo.setOnAction(e -> {
                int idx = accountCombo.getSelectionModel().getSelectedIndex();
                if (idx >= 0) {
                    PersonalAccount acc = accounts.get(idx);
                    var transactions = bankSystem.getTransactionManager().getTransactionsForAccount(acc);
                    table.getItems().clear();
                    table.getItems().addAll(transactions);
                }
            });
            
            vbox.getChildren().addAll(title, accountCombo, table);
            VBox.setVgrow(table, Priority.ALWAYS);
        }
        
        content.getChildren().add(vbox);
    }
    
    /**
     * Show standing orders
     */
    private void showStandingOrders(StackPane content, IndividualUser user) {
        content.getChildren().clear();
        
        VBox vbox = new VBox(15);
        
        Label title = new Label("Standing Orders");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        var orders = bankSystem.getStandingOrderManager().getStandingOrdersForUser(user);
        
        if (orders.isEmpty()) {
            vbox.getChildren().addAll(title, new Label("You don't have any standing orders."));
        } else {
            for (StandingOrder order : orders) {
                VBox orderBox = new VBox(5);
                orderBox.setPadding(new Insets(10));
                orderBox.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5;");
                
                orderBox.getChildren().addAll(
                    new Label("Type: " + order.getType()),
                    new Label(String.format("Amount: €%.2f", order.getAmount())),
                    new Label("Frequency: Every " + order.getFrequencyMonths() + " month(s)"),
                    new Label("Next Execution: " + order.getNextExecutionDate()),
                    new Label("Status: " + order.getStatus())
                );
                
                vbox.getChildren().add(orderBox);
            }
        }
        
        ScrollPane scrollPane = new ScrollPane(vbox);
        scrollPane.setFitToWidth(true);
        content.getChildren().add(scrollPane);
    }
    
    // Business user methods
    private void showBusinessOverview(StackPane content, BusinessUser user) {
        content.getChildren().clear();
        VBox vbox = new VBox(15);
        
        Label title = new Label("Business Account Overview");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        var accounts = bankSystem.getAccountManager().getAccountsForBusinessUser(user);
        
        if (accounts.isEmpty()) {
            vbox.getChildren().addAll(title, new Label("You don't have any accounts."));
        } else {
            for (BusinessAccount acc : accounts) {
                VBox accBox = new VBox(5);
                accBox.setPadding(new Insets(15));
                accBox.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5;");
                
                accBox.getChildren().addAll(
                    new Label("IBAN: " + acc.getIban()),
                    new Label(String.format("Balance: €%.2f", acc.getBalance())),
                    new Label("Status: " + acc.getStatus()),
                    new Label(String.format("Monthly Fee: €%.2f", acc.getMonthlyMaintenanceFee()))
                );
                
                vbox.getChildren().add(accBox);
            }
        }
        
        vbox.getChildren().add(0, title);
        content.getChildren().add(vbox);
    }
    
    private void showBusinessDeposit(StackPane content, BusinessUser user) {
        // Similar to individual deposit
        content.getChildren().clear();
        Label label = new Label("Business Deposit - Similar to Individual");
        content.getChildren().add(label);
    }
    
    private void showBusinessWithdraw(StackPane content, BusinessUser user) {
        content.getChildren().clear();
        Label label = new Label("Business Withdraw - Similar to Individual");
        content.getChildren().add(label);
    }
    
    private void showBusinessTransfer(StackPane content, BusinessUser user) {
        content.getChildren().clear();
        Label label = new Label("Business Transfer - Similar to Individual");
        content.getChildren().add(label);
    }
    
    private void showBusinessSepa(StackPane content, BusinessUser user) {
        content.getChildren().clear();
        Label label = new Label("Business SEPA - Similar to Individual");
        content.getChildren().add(label);
    }
    
    private void showBusinessSwift(StackPane content, BusinessUser user) {
        content.getChildren().clear();
        Label label = new Label("Business SWIFT - Similar to Individual");
        content.getChildren().add(label);
    }
    
    private void showIssueBillForm(StackPane content, BusinessUser user) {
        content.getChildren().clear();
        
        VBox vbox = new VBox(15);
        vbox.setMaxWidth(400);
        
        Label title = new Label("Issue Bill to Customer");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        TextField customerField = new TextField();
        customerField.setPromptText("Customer Username");
        
        TextField amountField = new TextField();
        amountField.setPromptText("Amount (EUR)");
        
        DatePicker dueDatePicker = new DatePicker();
        dueDatePicker.setPromptText("Due Date");
        dueDatePicker.setValue(java.time.LocalDate.now().plusDays(30));
        
        Label resultLabel = new Label();
        
        Button issueBtn = new Button("Issue Bill");
        issueBtn.setStyle(STYLE_BUTTON);
        
        issueBtn.setOnAction(e -> {
            try {
                String customerUsername = customerField.getText().trim();
                User customer = bankSystem.getUserManager().findByUsername(customerUsername);
                
                if (customer == null || !(customer instanceof IndividualUser)) {
                    resultLabel.setText("Customer not found or not an individual user");
                    resultLabel.setStyle("-fx-text-fill: red;");
                    return;
                }
                
                double amount = Double.parseDouble(amountField.getText());
                java.time.LocalDate dueDate = dueDatePicker.getValue();
                
                bankSystem.getBillManager().createBill(
                    (IndividualUser) customer, user, user.getBusinessName(),
                    new java.math.BigDecimal(amount), dueDate);
                bankSystem.saveToFile();
                
                resultLabel.setText("Bill issued successfully!");
                resultLabel.setStyle("-fx-text-fill: green;");
                customerField.clear();
                amountField.clear();
                
            } catch (NumberFormatException ex) {
                resultLabel.setText("Invalid amount");
                resultLabel.setStyle("-fx-text-fill: red;");
            }
        });
        
        vbox.getChildren().addAll(title, 
            new Label("Customer Username:"), customerField,
            new Label("Amount (EUR):"), amountField,
            new Label("Due Date:"), dueDatePicker,
            issueBtn, resultLabel);
        
        content.getChildren().add(vbox);
    }
    
    private void showIssuedBills(StackPane content, BusinessUser user) {
        content.getChildren().clear();
        
        VBox vbox = new VBox(15);
        
        Label title = new Label("Bills Issued by You");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        var bills = bankSystem.getBillManager().getBillsIssuedByBusiness(user);
        
        if (bills.isEmpty()) {
            vbox.getChildren().addAll(title, new Label("You haven't issued any bills."));
        } else {
            TableView<Bill> table = new TableView<>();
            
            TableColumn<Bill, String> idCol = new TableColumn<>("ID");
            idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getId()));
            
            TableColumn<Bill, String> customerCol = new TableColumn<>("Customer");
            customerCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getOwner().getUsername()));
            
            TableColumn<Bill, String> amountCol = new TableColumn<>("Amount");
            amountCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                String.format("€%.2f", data.getValue().getAmount())));
            
            TableColumn<Bill, String> statusCol = new TableColumn<>("Status");
            statusCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getStatus().toString()));
            
            table.getColumns().addAll(idCol, customerCol, amountCol, statusCol);
            table.getItems().addAll(bills);
            
            vbox.getChildren().addAll(title, table);
            VBox.setVgrow(table, Priority.ALWAYS);
        }
        
        content.getChildren().add(vbox);
    }
    
    private void showBusinessStatements(StackPane content, BusinessUser user) {
        content.getChildren().clear();
        Label label = new Label("Business Statements - Similar to Individual");
        content.getChildren().add(label);
    }
    
    // Admin methods
    private void showUserManagement(StackPane content) {
        content.getChildren().clear();
        
        VBox vbox = new VBox(15);
        
        Label title = new Label("User Management");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        var users = bankSystem.getUserManager().getAllUsers();
        
        TableView<User> table = new TableView<>();
        
        TableColumn<User, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getId()));
        
        TableColumn<User, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getUsername()));
        
        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getRole()));
        
        TableColumn<User, String> lockedCol = new TableColumn<>("Locked");
        lockedCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().isLocked() ? "Yes" : "No"));
        
        table.getColumns().addAll(idCol, usernameCol, roleCol, lockedCol);
        table.getItems().addAll(users);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Unlock button
        Button unlockBtn = new Button("Unlock Selected User");
        unlockBtn.setStyle(STYLE_BUTTON);
        unlockBtn.setOnAction(e -> {
            User selected = table.getSelectionModel().getSelectedItem();
            if (selected != null && selected.isLocked()) {
                selected.setLocked(false);
                selected.resetFailedAttempts();
                bankSystem.saveToFile();
                table.refresh();
            }
        });
        
        vbox.getChildren().addAll(title, table, unlockBtn);
        VBox.setVgrow(table, Priority.ALWAYS);
        
        content.getChildren().add(vbox);
    }
    
    private void showAllAccounts(StackPane content) {
        content.getChildren().clear();
        
        VBox vbox = new VBox(15);
        
        Label title = new Label("All Accounts");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        var accounts = bankSystem.getAccountManager().getAllAccounts();
        
        TableView<Account> table = new TableView<>();
        
        TableColumn<Account, String> ibanCol = new TableColumn<>("IBAN");
        ibanCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getIban()));
        
        TableColumn<Account, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getAccountType()));
        
        TableColumn<Account, String> balanceCol = new TableColumn<>("Balance");
        balanceCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            String.format("€%.2f", data.getValue().getBalance())));
        
        TableColumn<Account, String> ownerCol = new TableColumn<>("Owner");
        ownerCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().getPrimaryOwner().getUsername()));
        
        table.getColumns().addAll(ibanCol, typeCol, balanceCol, ownerCol);
        table.getItems().addAll(accounts);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        vbox.getChildren().addAll(title, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        
        content.getChildren().add(vbox);
    }
    
    private void showAllTransactions(StackPane content) {
        content.getChildren().clear();
        
        VBox vbox = new VBox(15);
        
        Label title = new Label("All Transactions");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        var transactions = bankSystem.getTransactionManager().getAllTransactions();
        
        TableView<Transaction> table = new TableView<>();
        
        TableColumn<Transaction, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().getDateTime().toLocalDate().toString()));
        
        TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().getType().toString()));
        
        TableColumn<Transaction, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            String.format("€%.2f", data.getValue().getAmount())));
        
        TableColumn<Transaction, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().getDescription()));
        
        table.getColumns().addAll(dateCol, typeCol, amountCol, descCol);
        table.getItems().addAll(transactions);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        vbox.getChildren().addAll(title, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        
        content.getChildren().add(vbox);
    }
    
    private void showTimeSimulation(StackPane content) {
        content.getChildren().clear();
        
        VBox vbox = new VBox(15);
        vbox.setMaxWidth(400);
        
        Label title = new Label("Time Simulation");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        Label currentDateLabel = new Label("Current System Date: " + bankSystem.getCurrentDate());
        
        TextField daysField = new TextField();
        daysField.setPromptText("Number of days to simulate");
        
        Label resultLabel = new Label();
        resultLabel.setWrapText(true);
        
        Button simulateBtn = new Button("Simulate Days");
        simulateBtn.setStyle(STYLE_BUTTON);
        
        simulateBtn.setOnAction(e -> {
            try {
                int days = Integer.parseInt(daysField.getText());
                if (days <= 0 || days > 365) {
                    resultLabel.setText("Please enter a number between 1 and 365");
                    resultLabel.setStyle("-fx-text-fill: red;");
                    return;
                }
                
                bankSystem.simulateDays(days);
                bankSystem.saveToFile();
                
                currentDateLabel.setText("Current System Date: " + bankSystem.getCurrentDate());
                resultLabel.setText("Simulated " + days + " days. Interest calculated, fees applied, standing orders executed.");
                resultLabel.setStyle("-fx-text-fill: green;");
                daysField.clear();
                
            } catch (NumberFormatException ex) {
                resultLabel.setText("Invalid number");
                resultLabel.setStyle("-fx-text-fill: red;");
            }
        });
        
        Button advanceOneDay = new Button("Advance 1 Day");
        advanceOneDay.setStyle(STYLE_BUTTON_SUCCESS);
        advanceOneDay.setOnAction(e -> {
            bankSystem.advanceDay();
            bankSystem.saveToFile();
            currentDateLabel.setText("Current System Date: " + bankSystem.getCurrentDate());
            resultLabel.setText("Advanced 1 day to " + bankSystem.getCurrentDate());
            resultLabel.setStyle("-fx-text-fill: green;");
        });

        Button resetBtn = new Button("Reset to Today");
        // Orange color to distinguish it
        resetBtn.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
        resetBtn.setOnAction(e -> {
            bankSystem.resetSystemDate();
            // Update the label immediately so you see the change
            currentDateLabel.setText("Current System Date: " + bankSystem.getCurrentDate());
            resultLabel.setText("System date reset to today.");
            resultLabel.setStyle("-fx-text-fill: green;");
        });

        vbox.getChildren().addAll(title, currentDateLabel, new Separator(),
            new Label("Days to simulate:"), daysField, simulateBtn, advanceOneDay, resetBtn, resultLabel);
        
        content.getChildren().add(vbox);
    }
    
    private void showSystemInfo(StackPane content) {
        content.getChildren().clear();
        
        VBox vbox = new VBox(15);
        
        Label title = new Label("System Information");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        var users = bankSystem.getUserManager().getAllUsers();
        var accounts = bankSystem.getAccountManager().getAllAccounts();
        var transactions = bankSystem.getTransactionManager().getAllTransactions();
        var bills = bankSystem.getBillManager().getAllBills();
        var standingOrders = bankSystem.getStandingOrderManager().getAllStandingOrders();
        
        vbox.getChildren().addAll(title,
            new Label("System Date: " + bankSystem.getCurrentDate()),
            new Separator(),
            new Label("Total Users: " + users.size()),
            new Label("Total Accounts: " + accounts.size()),
            new Label("Total Transactions: " + transactions.size()),
            new Label("Total Bills: " + bills.size()),
            new Label("Total Standing Orders: " + standingOrders.size()),
            new Separator(),
            new Label("Individual Users: " + users.stream().filter(u -> u instanceof IndividualUser).count()),
            new Label("Business Users: " + users.stream().filter(u -> u instanceof BusinessUser).count()),
            new Label("Admin Users: " + users.stream().filter(u -> u instanceof AdminUser).count())
        );
        
        content.getChildren().add(vbox);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
