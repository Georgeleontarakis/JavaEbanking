package com.bankoftuc.dao;

import com.bankoftuc.model.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.Field;

/**
 * DAO Pattern - CSV Implementation of User DAO.
 * Persists User entities to CSV file.
 */
public class UserDAOCsv implements UserDAO {
    
    private static final String FILE_PATH = "data/users.csv";
    private static final String HEADER = "type,id,username,passwordHash,phoneNumber,failedLoginAttempts,locked,fullName,address,vatNumber,businessName,adminLevel";
    
    private List<User> users;
    
    public UserDAOCsv() {
        this.users = new ArrayList<>();
        loadFromFile();
    }
    
    public UserDAOCsv(List<User> users) {
        this.users = users;
    }
    
    @Override
    public User save(User entity) {
        // Check if already exists
        if (findById(entity.getId()).isPresent()) {
            return update(entity);
        }
        users.add(entity);
        saveToFile();
        return entity;
    }
    
    @Override
    public Optional<User> findById(String id) {
        return users.stream()
            .filter(u -> u.getId().equals(id))
            .findFirst();
    }
    
    @Override
    public List<User> findAll() {
        return new ArrayList<>(users);
    }
    
    @Override
    public User update(User entity) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(entity.getId())) {
                users.set(i, entity);
                saveToFile();
                return entity;
            }
        }
        return save(entity);
    }
    
    @Override
    public void delete(User entity) {
        deleteById(entity.getId());
    }
    
    @Override
    public void deleteById(String id) {
        users.removeIf(u -> u.getId().equals(id));
        saveToFile();
    }
    
    @Override
    public boolean existsById(String id) {
        return findById(id).isPresent();
    }
    
    @Override
    public long count() {
        return users.size();
    }
    
    @Override
    public Optional<User> findByUsername(String username) {
        return users.stream()
            .filter(u -> u.getUsername().equals(username))
            .findFirst();
    }
    
    @Override
    public List<User> findByType(String userType) {
        List<User> result = new ArrayList<>();
        for (User user : users) {
            if (userType.equals("INDIVIDUAL") && user instanceof IndividualUser) {
                result.add(user);
            } else if (userType.equals("BUSINESS") && user instanceof BusinessUser) {
                result.add(user);
            } else if (userType.equals("ADMIN") && user instanceof AdminUser) {
                result.add(user);
            }
        }
        return result;
    }
    
    @Override
    public boolean usernameExists(String username) {
        return findByUsername(username).isPresent();
    }
    
    @Override
    public List<User> findAllActive() {
        List<User> result = new ArrayList<>();
        for (User user : users) {
            if (!user.isLocked()) {
                result.add(user);
            }
        }
        return result;
    }
    
    @Override
    public List<User> findAllLocked() {
        List<User> result = new ArrayList<>();
        for (User user : users) {
            if (user.isLocked()) {
                result.add(user);
            }
        }
        return result;
    }
    
    /**
     * Load users from CSV file
     */
    private void loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // Skip header
            
            while ((line = reader.readLine()) != null) {
                User user = parseUser(line);
                if (user != null) {
                    users.add(user);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
    }
    
    /**
     * Save users to CSV file
     */
    public void saveToFile() {
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs();
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println(HEADER);
            
            for (User user : users) {
                writer.println(formatUser(user));
            }
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }
    
    /**
     * Parse a CSV line into a User object
     */
    private User parseUser(String line) {
        String[] fields = parseCsvLine(line);
        if (fields.length < 7) return null;
        
        String type = fields[0];
        String id = fields[1];
        String username = fields[2];
        String passwordHash = fields[3];
        String phoneNumber = fields[4];
        int failedAttempts = Integer.parseInt(fields[5]);
        boolean locked = Boolean.parseBoolean(fields[6]);
        
        User user = null;
        
        switch (type) {
            case "INDIVIDUAL":
                String fullName = fields.length > 7 ? fields[7] : "";
                String address = fields.length > 8 ? fields[8] : "";
                String vatNumber = fields.length > 9 ? fields[9] : "";
                IndividualUser individual = new IndividualUser(id, username, "temp", fullName, address, phoneNumber, vatNumber);
                user = individual;
                break;
                
            case "BUSINESS":
                String busVat = fields.length > 9 ? fields[9] : "";
                String busName = fields.length > 10 ? fields[10] : "";
                BusinessUser business = new BusinessUser(id, username, "temp", busName, phoneNumber, busVat);
                user = business;
                break;
                
            case "ADMIN":
                int adminLevel = 1;
                if (fields.length > 11) {
                    try {
                        adminLevel = Integer.parseInt(fields[11]);
                    } catch (NumberFormatException e) {
                        adminLevel = 1;
                    }
                }
                AdminUser admin = new AdminUser(id, username, "temp", phoneNumber, adminLevel);
                user = admin;
                break;
        }
        
        if (user != null) {
            // Set password hash directly via reflection
            try {
                Field hashField = User.class.getDeclaredField("passwordHash");
                hashField.setAccessible(true);
                hashField.set(user, passwordHash);
            } catch (Exception e) {
                // Fallback - password will be "temp"
            }
            
            user.setLocked(locked);
            // Set failed attempts using recordFailedLogin
            for (int i = 0; i < failedAttempts; i++) {
                user.recordFailedLogin();
            }
            // Reset locked status if needed (since recordFailedLogin might lock)
            if (!locked) {
                user.setLocked(false);
            }
        }
        
        return user;
    }
    
    /**
     * Format a User object as CSV line
     */
    private String formatUser(User user) {
        StringBuilder sb = new StringBuilder();
        
        String type = "";
        if (user instanceof IndividualUser) type = "INDIVIDUAL";
        else if (user instanceof BusinessUser) type = "BUSINESS";
        else if (user instanceof AdminUser) type = "ADMIN";
        
        // Get password hash via reflection
        String passwordHash = "";
        try {
            Field hashField = User.class.getDeclaredField("passwordHash");
            hashField.setAccessible(true);
            passwordHash = (String) hashField.get(user);
        } catch (Exception e) {
            passwordHash = "";
        }
        
        sb.append(type).append(",");
        sb.append(escapeCsv(user.getId())).append(",");
        sb.append(escapeCsv(user.getUsername())).append(",");
        sb.append(escapeCsv(passwordHash)).append(",");
        sb.append(escapeCsv(user.getPhoneNumber())).append(",");
        sb.append(user.getFailedLoginAttempts()).append(",");
        sb.append(user.isLocked()).append(",");
        
        // Type-specific fields
        if (user instanceof IndividualUser) {
            IndividualUser ind = (IndividualUser) user;
            sb.append(escapeCsv(ind.getFullName())).append(",");
            sb.append(escapeCsv(ind.getAddress())).append(",,,");
        } else if (user instanceof BusinessUser) {
            BusinessUser bus = (BusinessUser) user;
            sb.append(","); // fullName
            sb.append(","); // address (BusinessUser doesn't have address field)
            sb.append(escapeCsv(bus.getVatNumber())).append(",");
            sb.append(escapeCsv(bus.getBusinessName())).append(",");
        } else if (user instanceof AdminUser) {
            AdminUser adm = (AdminUser) user;
            sb.append(",,,,").append(adm.getAdminLevel());
        }
        
        return sb.toString();
    }
    
    /**
     * Parse CSV line respecting quotes
     */
    private String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString());
        
        return fields.toArray(new String[0]);
    }
    
    /**
     * Escape CSV special characters
     */
    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
