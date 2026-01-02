# Bank of TUC - Windows Makefile
# ================================

# JavaFX Configuration
JAVAFX_PATH = C:/Users/dimit/Downloads/openjfx-25.0.1_windows-x64_bin-sdk/javafx-sdk-25.0.1/lib

# JUnit Configuration (download once, reuse)
JUNIT_JAR = lib/junit-platform-console-standalone-1.10.2.jar
JUNIT_URL = https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.2/junit-platform-console-standalone-1.10.2.jar

# Java commands
JAVAC = javac
JAVA = java

# Directories
BIN_DIR = bin
LIB_DIR = lib

# JavaFX modules needed
JAVAFX_MODULES = javafx.controls,javafx.fxml

.PHONY: cli gui clean help info run cli-compile gui-compile test test-compile download-junit

# ============== CLI TARGETS ==============

cli-compile:
	@echo [COMPILE] Compiling CLI version...
	@if not exist $(BIN_DIR) mkdir $(BIN_DIR)
	$(JAVAC) -d $(BIN_DIR) com/bankoftuc/*.java com/bankoftuc/model/*.java com/bankoftuc/manager/*.java com/bankoftuc/util/*.java com/bankoftuc/dao/*.java com/bankoftuc/factory/*.java com/bankoftuc/builder/*.java com/bankoftuc/command/*.java com/bankoftuc/bridge/*.java com/bankoftuc/ui/*.java
	@echo [DONE] CLI compilation complete!

cli: cli-compile
	@echo [RUN] Starting CLI...
	$(JAVA) -cp $(BIN_DIR) com.bankoftuc.Main

run: cli

# ============== GUI TARGETS ==============

gui-compile:
	@echo [COMPILE] Compiling GUI version with JavaFX...
	@if not exist $(BIN_DIR) mkdir $(BIN_DIR)
	$(JAVAC) --module-path "$(JAVAFX_PATH)" --add-modules $(JAVAFX_MODULES) -d $(BIN_DIR) com/bankoftuc/*.java com/bankoftuc/model/*.java com/bankoftuc/manager/*.java com/bankoftuc/util/*.java com/bankoftuc/dao/*.java com/bankoftuc/factory/*.java com/bankoftuc/builder/*.java com/bankoftuc/command/*.java com/bankoftuc/bridge/*.java com/bankoftuc/ui/*.java com/bankoftuc/gui/*.java
	@echo [DONE] GUI compilation complete!

gui: gui-compile
	@echo [RUN] Starting GUI...
	$(JAVA) --module-path "$(JAVAFX_PATH)" --add-modules $(JAVAFX_MODULES) -cp $(BIN_DIR) com.bankoftuc.gui.BankingGUI

# ============== TEST TARGETS ==============

download-junit:
	@echo [DOWNLOAD] Checking JUnit...
	@if not exist $(LIB_DIR) mkdir $(LIB_DIR)
	@if not exist $(JUNIT_JAR) ( \
		echo [DOWNLOAD] Downloading JUnit 5... && \
		powershell -Command "Invoke-WebRequest -Uri '$(JUNIT_URL)' -OutFile '$(JUNIT_JAR)'" \
	) else ( \
		echo [OK] JUnit already downloaded \
	)

test-compile: download-junit
	@echo [COMPILE] Compiling source and tests...
	@if not exist $(BIN_DIR) mkdir $(BIN_DIR)
	$(JAVAC) -cp "$(JUNIT_JAR)" -d $(BIN_DIR) com/bankoftuc/*.java com/bankoftuc/model/*.java com/bankoftuc/manager/*.java com/bankoftuc/util/*.java com/bankoftuc/dao/*.java com/bankoftuc/factory/*.java com/bankoftuc/builder/*.java com/bankoftuc/command/*.java com/bankoftuc/bridge/*.java com/bankoftuc/ui/*.java com/bankoftuc/test/*.java
	@echo [DONE] Compilation complete!

test: test-compile
	@echo.
	@echo ========================================
	@echo    Running Unit Tests
	@echo ========================================
	@echo.
	$(JAVA) -jar $(JUNIT_JAR) --class-path "$(BIN_DIR)" --scan-class-path --details=tree

test-verbose: test-compile
	$(JAVA) -jar $(JUNIT_JAR) --class-path "$(BIN_DIR)" --scan-class-path --details=verbose

# ============== UTILITY TARGETS ==============

clean:
	@echo [CLEAN] Removing compiled files...
	@if exist $(BIN_DIR) rmdir /s /q $(BIN_DIR)
	@echo [DONE] Clean complete!

clean-data:
	@echo [CLEAN] Removing data files...
	@if exist data\*.csv del /q data\*.csv
	@echo [DONE] Data cleaned!

clean-all: clean clean-data
	@if exist $(LIB_DIR) rmdir /s /q $(LIB_DIR)

info:
	@echo ========================================
	@echo    Bank of TUC - Project Info
	@echo ========================================
	@echo JavaFX Path: $(JAVAFX_PATH)
	@echo JUnit JAR: $(JUNIT_JAR)

help:
	@echo ========================================
	@echo    Bank of TUC - Available Commands
	@echo ========================================
	@echo.
	@echo CLI Commands (no JavaFX required):
	@echo   make cli          - Compile and run CLI
	@echo   make cli-compile  - Compile CLI only
	@echo   make run          - Same as make cli
	@echo.
	@echo GUI Commands (requires JavaFX):
	@echo   make gui          - Compile and run GUI
	@echo   make gui-compile  - Compile GUI only
	@echo.
	@echo Test Commands:
	@echo   make test         - Run all unit tests
	@echo   make test-verbose - Run tests with verbose output
	@echo   make test-compile - Compile tests only
	@echo.
	@echo Utility Commands:
	@echo   make clean        - Remove compiled files
	@echo   make clean-data   - Remove CSV data files
	@echo   make clean-all    - Remove everything including JUnit
	@echo   make info         - Show project info
	@echo   make help         - Show this help