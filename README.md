# Bank of TUC - eBanking System

## Overview
A complete eBanking simulation system developed for PLH302 - Information Systems Design & Development at Technical University of Crete.

## Features
- **User Management**: Individual, Business, and Admin users
- **Account Management**: Personal and Business accounts with interest calculation
- **Transactions**: Deposits, Withdrawals, Internal Transfers
- **International Transfers**: SEPA (European) and SWIFT (International) via external API
- **Bill Management**: Businesses can issue bills, individuals can pay
- **Standing Orders**: Automated recurring payments and transfers
- **Time Simulation**: Day-by-day simulation for testing

## Design Patterns Implemented
1. **Singleton** - BankSystem (central coordinator)
2. **Factory** - UserFactory, AccountFactory
3. **Builder** - TransactionBuilder, BillBuilder
4. **Command** - DepositCommand, WithdrawCommand, TransferCommand, PayBillCommand
5. **Bridge** - TransferImplementor, SepaImplementor, SwiftImplementor
6. **DAO** - GenericDAO, UserDAO, AccountDAO, TransactionDAO

## Data Storage
All data is stored in CSV files in the `data/` folder:
- `users.csv` - User accounts
- `accounts.csv` - Bank accounts
- `transactions.csv` - Transaction history
- `bills.csv` - Bills
- `standing_orders.csv` - Standing orders
- `co_owners.csv` - Account co-owners
- `system.csv` - System date

## Running the Application

### Command Line Interface (CLI)
```bash
# Compile
javac -d bin $(find com -name "*.java" ! -path "*/gui/*")

# Run CLI
java -cp bin com.bankoftuc.Main --cli
```

### Graphical User Interface (GUI)
The GUI requires JavaFX which is not bundled with Java 11+.

#### Step 1: Download JavaFX
Download JavaFX SDK from: https://openjfx.io/

#### Step 2: Compile with JavaFX
```bash
# Set JavaFX path
export JAVAFX_PATH=/path/to/javafx-sdk/lib

# Compile everything including GUI
javac --module-path $JAVAFX_PATH --add-modules javafx.controls,javafx.fxml -d bin $(find com -name "*.java")
```

#### Step 3: Run GUI
```bash
java --module-path $JAVAFX_PATH --add-modules javafx.controls,javafx.fxml -cp bin com.bankoftuc.gui.BankingGUI
```

### macOS with Homebrew
```bash
brew install openjfx

# Find JavaFX path
JAVAFX_PATH=$(brew --prefix openjfx)/libexec/lib

# Compile and run
javac --module-path $JAVAFX_PATH --add-modules javafx.controls -d bin $(find com -name "*.java")
java --module-path $JAVAFX_PATH --add-modules javafx.controls -cp bin com.bankoftuc.gui.BankingGUI
```

### Windows
```batch
set JAVAFX_PATH=C:\path\to\javafx-sdk\lib

javac --module-path %JAVAFX_PATH% --add-modules javafx.controls -d bin com\bankoftuc\*.java com\bankoftuc\**\*.java

java --module-path %JAVAFX_PATH% --add-modules javafx.controls -cp bin com.bankoftuc.gui.BankingGUI
```

## Demo Credentials
| Username | Password | Role |
|----------|----------|------|
| admin | admin123 | Administrator |
| john | pass123 | Individual |
| maria | pass123 | Individual |
| nikos | pass123 | Individual |
| techcorp | pass123 | Business |
| utility | pass123 | Business |

## GUI Features
- **Login Screen**: Secure authentication
- **Individual Dashboard**: Account overview, deposits, withdrawals, transfers, SEPA/SWIFT, bills, statements
- **Business Dashboard**: Account management, issue bills, view issued bills
- **Admin Panel**: User management, view all accounts/transactions, time simulation

## API Integration
SEPA and SWIFT transfers use an external API:
- **URL**: http://147.27.70.44:3020
- **Success Rate**: 75%
- **SEPA Fee**: €1.50
- **SWIFT Fee**: €25.00

## Project Structure
```
com/bankoftuc/
├── Main.java           # Entry point (CLI/GUI selection)
├── model/              # Entity classes
├── manager/            # Business logic + BankTransferAPI
├── ui/                 # CLI interface
├── gui/                # JavaFX GUI (requires JavaFX)
├── factory/            # Factory pattern
├── builder/            # Builder pattern
├── command/            # Command pattern
├── bridge/             # Bridge pattern
├── dao/                # Data Access Objects
└── util/               # Utilities
```

## Authors
Bank of TUC Development Team - PLH302

## License
Educational Use - Technical University of Crete
