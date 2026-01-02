# Bank of TUC - eBanking System

## Επισκόπηση
Ολοκληρωμένο σύστημα προσομοίωσης eBanking για το μάθημα ΠΛΗ302 - Σχεδίαση και Ανάπτυξη Πληροφοριακών Συστημάτων, Πολυτεχνείο Κρήτης.

---

## Γρήγορη Εκκίνηση (Quick Start)

### Προαπαιτούμενα
- **Java 21+** (ή Java 25)
- **JavaFX SDK** (ίδια έκδοση με Java)
- **Make** (προαιρετικό, για εύκολη εκτέλεση)

### Βήμα 1: Εγκατάσταση Java
Κατέβασε και εγκατέστησε Java από: https://adoptium.net/

Επιβεβαίωσε την εγκατάσταση:
```bash
java -version
```

### Βήμα 2: Εγκατάσταση JavaFX
1. Πήγαινε στο https://openjfx.io/
2. Κατέβασε το **JavaFX SDK** (όχι jmods) για το λειτουργικό σου
3. Κάνε extract σε έναν φάκελο (π.χ. `C:\javafx-sdk-21\`)

> ⚠️ **Σημαντικό**: Η έκδοση JavaFX πρέπει να ταιριάζει με την έκδοση Java!

### Βήμα 3: Εγκατάσταση Make (Windows μόνο)
Άνοιξε PowerShell ως Administrator και τρέξε:
```powershell
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
choco install make
```

### Βήμα 4: Ρύθμιση JavaFX Path
Άνοιξε το `Makefile` και άλλαξε τη γραμμή:
```makefile
JAVAFX_PATH = C:/path/to/javafx-sdk/lib
```
στο path που έκανες extract το JavaFX (χρησιμοποίησε `/` όχι `\`).

### Βήμα 5: Εκτέλεση
```bash
make gui
```

---

## Εντολές Make

| Εντολή | Περιγραφή |
|--------|-----------|
| `make gui` | Compile και εκτέλεση GUI |
| `make cli` | Compile και εκτέλεση CLI |
| `make test` | Εκτέλεση unit tests |
| `make clean` | Διαγραφή compiled αρχείων |
| `make help` | Εμφάνιση όλων των εντολών |

---

## Demo Credentials

| Username | Password | Ρόλος |
|----------|----------|-------|
| admin | pass123 | Διαχειριστής |
| ngioldasis | pass123 | Φυσικό Πρόσωπο |
| pappas | pass123 | Φυσικό Πρόσωπο |
| gchalkiadakis | pass123 | Φυσικό Πρόσωπο |
| vodafone | pass123 | Επιχείρηση |
| cosmote | pass123 | Επιχείρηση |
| dei | pass123 | Επιχείρηση |

---

## Λειτουργίες Συστήματος

### Φυσικά Πρόσωπα (Individual Users)
- 💰 Κατάθεση / Ανάληψη
- 🔄 Μεταφορά σε άλλο λογαριασμό (Internal Transfer)
- 🇪🇺 SEPA Transfer (Ευρωπαϊκή μεταφορά)
- 🌍 SWIFT Transfer (Διεθνής μεταφορά)
- 📄 Πληρωμή λογαριασμών
- 📋 Πάγιες εντολές (Standing Orders)
- 📊 Ιστορικό συναλλαγών

### Επιχειρήσεις (Business Users)
- 📄 Έκδοση λογαριασμών προς πελάτες
- 📋 Προβολή εκδοθέντων λογαριασμών
- 💰 Διαχείριση λογαριασμού

### Διαχειριστές (Admin Users)
- 👥 Διαχείριση χρηστών
- 💳 Προβολή όλων των λογαριασμών
- 📊 Προβολή όλων των συναλλαγών
- ⏰ Προσομοίωση χρόνου (Time Simulation)

---

## API Integration

Το σύστημα χρησιμοποιεί εξωτερικό API για SEPA/SWIFT transfers:

| Παράμετρος | Τιμή |
|------------|------|
| URL | http://147.27.70.44:3020 |
| SEPA Fee | €1.50 |
| SWIFT Fee | €25.00 |
| Success Rate | 75% |

> 📍 Το API είναι προσβάσιμο μόνο από το δίκτυο του Πολυτεχνείου Κρήτης.

---

## Design Patterns

| Pattern | Υλοποίηση | Περιγραφή |
|---------|-----------|-----------|
| **Singleton** | BankSystem | Κεντρικός συντονιστής συστήματος |
| **Factory** | UserFactory, AccountFactory | Δημιουργία χρηστών και λογαριασμών |
| **Builder** | TransactionBuilder, BillBuilder | Κατασκευή σύνθετων αντικειμένων |
| **Command** | DepositCommand, WithdrawCommand, TransferCommand | Ενθυλάκωση ενεργειών |
| **Bridge** | SepaImplementor, SwiftImplementor | Διαχωρισμός abstraction/implementation |
| **DAO** | UserDAO, AccountDAO, TransactionDAO | Πρόσβαση σε δεδομένα |

---

## Δομή Project

```
com/bankoftuc/
├── Main.java              # Entry point
├── model/                 # Entities (User, Account, Transaction, Bill)
├── manager/               # Business Logic + BankTransferAPI
├── factory/               # Factory Pattern
├── builder/               # Builder Pattern
├── command/               # Command Pattern
├── bridge/                # Bridge Pattern (SEPA/SWIFT)
├── dao/                   # Data Access Objects
├── ui/                    # CLI Interface
├── gui/                   # JavaFX GUI
├── util/                  # Utilities
└── test/                  # Unit Tests
data/
├── users.csv              # Χρήστες
├── accounts.csv           # Λογαριασμοί
├── transactions.csv       # Συναλλαγές
├── bills.csv              # Λογαριασμοί προς πληρωμή
├── standing_orders.csv    # Πάγιες εντολές
├── co_owners.csv          # Συνδικαιούχοι
└── system.csv             # Ημερομηνία συστήματος
```

---

## Αντιμετώπιση Προβλημάτων

### "JavaFX runtime components are missing"
→ Βεβαιώσου ότι το `JAVAFX_PATH` στο Makefile είναι σωστό.

### "module not found: javafx.controls"
→ Έλεγξε ότι η έκδοση JavaFX ταιριάζει με την έκδοση Java.

### SEPA/SWIFT transfer fails
→ Το API έχει 25% failure rate. Δοκίμασε ξανά!
→ Βεβαιώσου ότι είσαι στο δίκτυο του Πολυτεχνείου.

### "Deposit amount must be positive" στα Standing Orders
→ Χρησιμοποίησε τα σωστά test data με amounts στα bill payments.

---

## Εκτέλεση χωρίς Make

### Windows (PowerShell)
```powershell
# Compile
javac --module-path "C:\javafx-sdk-21\lib" --add-modules javafx.controls,javafx.fxml -d bin (Get-ChildItem -Recurse -Filter "*.java" | ForEach-Object { $_.FullName })

# Run
java --module-path "C:\javafx-sdk-21\lib" --add-modules javafx.controls,javafx.fxml -cp bin com.bankoftuc.gui.BankingGUI
```

### macOS/Linux
```bash
# Compile
javac --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -d bin $(find com -name "*.java")

# Run
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -cp bin com.bankoftuc.gui.BankingGUI
```

### macOS με Homebrew
```bash
brew install openjfx
JAVAFX_PATH=$(brew --prefix openjfx)/libexec/lib
javac --module-path $JAVAFX_PATH --add-modules javafx.controls,javafx.fxml -d bin $(find com -name "*.java")
java --module-path $JAVAFX_PATH --add-modules javafx.controls,javafx.fxml -cp bin com.bankoftuc.gui.BankingGUI
```

---

## Συγγραφείς
Bank of TUC Development Team - ΠΛΗ302, Πολυτεχνείο Κρήτης

## Άδεια
Εκπαιδευτική χρήση - Πολυτεχνείο Κρήτης
