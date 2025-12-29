package com.bankoftuc.command;

/**
 * Command Pattern - Command Interface.
 * Defines the contract for all banking operations.
 * Supports execute and undo operations.
 */
public interface Command {
    
    /**
     * Execute the command
     * @return true if execution was successful
     */
    boolean execute();
    
    /**
     * Undo the command (reverse the operation)
     * @return true if undo was successful
     */
    boolean undo();
    
    /**
     * Get description of the command
     */
    String getDescription();
    
    /**
     * Check if the command can be undone
     */
    boolean isUndoable();
}
