package models;

import java.time.LocalDateTime;

/**
 * User model representing a system user (customer or admin).
 */
public class User {
    public enum Role {
        CUSTOMER("customer"),
        ADMIN("admin");

        private final String value;

        Role(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Role fromString(String text) {
            for (Role r : Role.values()) {
                if (r.value.equalsIgnoreCase(text)) {
                    return r;
                }
            }
            return CUSTOMER;
        }
    }

    private int id;
    private String username;
    private String passwordHash;
    private String email;
    private String phone;
    private Role role;
    private LocalDateTime createdAt;

    public User() {
        this.role = Role.CUSTOMER;
    }

    public User(String username, String passwordHash, String email, String phone, Role role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "', role=" + role + "}";
    }
}
