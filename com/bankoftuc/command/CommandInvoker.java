package com.bankoftuc.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Command Pattern - Command Invoker.
 * Manages command execution and maintains history for undo operations.
 */
public class CommandInvoker {
    
    private Stack<Command> executedCommands;
    private Stack<Command> undoneCommands;
    private List<Command> commandHistory;
    private int maxHistorySize;
    
    public CommandInvoker() {
        this.executedCommands = new Stack<>();
        this.undoneCommands = new Stack<>();
        this.commandHistory = new ArrayList<>();
        this.maxHistorySize = 100; // Keep last 100 commands
    }
    
    /**
     * Execute a command
     */
    public boolean executeCommand(Command command) {
        boolean success = command.execute();
        
        if (success) {
            executedCommands.push(command);
            undoneCommands.clear(); // Clear redo stack after new command
            
            // Add to history
            commandHistory.add(command);
            if (commandHistory.size() > maxHistorySize) {
                commandHistory.remove(0);
            }
            
            System.out.println("[EXECUTED] " + command.getDescription());
        } else {
            System.out.println("[FAILED] " + command.getDescription());
        }
        
        return success;
    }
    
    /**
     * Undo the last executed command
     */
    public boolean undo() {
        if (executedCommands.isEmpty()) {
            System.out.println("Nothing to undo");
            return false;
        }
        
        Command command = executedCommands.peek();
        
        if (!command.isUndoable()) {
            System.out.println("Last command cannot be undone: " + command.getDescription());
            return false;
        }
        
        boolean success = command.undo();
        
        if (success) {
            executedCommands.pop();
            undoneCommands.push(command);
            System.out.println("[UNDONE] " + command.getDescription());
        } else {
            System.out.println("[UNDO FAILED] " + command.getDescription());
        }
        
        return success;
    }
    
    /**
     * Redo the last undone command
     */
    public boolean redo() {
        if (undoneCommands.isEmpty()) {
            System.out.println("Nothing to redo");
            return false;
        }
        
        Command command = undoneCommands.pop();
        boolean success = command.execute();
        
        if (success) {
            executedCommands.push(command);
            System.out.println("[REDONE] " + command.getDescription());
        } else {
            System.out.println("[REDO FAILED] " + command.getDescription());
        }
        
        return success;
    }
    
    /**
     * Check if undo is available
     */
    public boolean canUndo() {
        return !executedCommands.isEmpty() && executedCommands.peek().isUndoable();
    }
    
    /**
     * Check if redo is available
     */
    public boolean canRedo() {
        return !undoneCommands.isEmpty();
    }
    
    /**
     * Get the last executed command description
     */
    public String getLastCommandDescription() {
        if (executedCommands.isEmpty()) {
            return "No commands executed";
        }
        return executedCommands.peek().getDescription();
    }
    
    /**
     * Get command history
     */
    public List<String> getCommandHistory() {
        List<String> history = new ArrayList<>();
        for (Command cmd : commandHistory) {
            history.add(cmd.getDescription());
        }
        return history;
    }
    
    /**
     * Get recent command history
     */
    public List<String> getRecentHistory(int count) {
        List<String> history = getCommandHistory();
        int start = Math.max(0, history.size() - count);
        return history.subList(start, history.size());
    }
    
    /**
     * Clear all history
     */
    public void clearHistory() {
        executedCommands.clear();
        undoneCommands.clear();
        commandHistory.clear();
    }
    
    /**
     * Get count of executed commands
     */
    public int getExecutedCount() {
        return executedCommands.size();
    }
    
    /**
     * Set maximum history size
     */
    public void setMaxHistorySize(int size) {
        this.maxHistorySize = size;
    }
}
