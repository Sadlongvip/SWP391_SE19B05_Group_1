package com.luxestay.hotel.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "roles")
public class Roles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String role_name;
    private String role_description;
    //=====================================================
    @OneToMany(mappedBy = "roles")
    private List<Account> accounts;

    // Static memory roles - predefined system roles
    public static final Roles CUSTOMER = new Roles(1, "Customer", "Regular customer who can book rooms, view services, and manage their own bookings");
    public static final Roles EMPLOYEE = new Roles(2, "Employee", "Hotel staff member who can assist customers, manage bookings, and access employee-level features");
    public static final Roles ADMIN = new Roles(3, "Admin", "System administrator with full access to manage accounts, employees, rooms, and all hotel operations including editing or deactivating accounts and employees");

    public Roles() {
    }

    // Constructor for static roles
    public Roles(int id, String role_name, String role_description) {
        this.id = id;
        this.role_name = role_name;
        this.role_description = role_description;
    }
    //=====================================================
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRole_name() {
        return role_name;
    }

    public void setRole_name(String role_name) {
        this.role_name = role_name;
    }

    public String getRole_description() {
        return role_description;
    }

    public void setRole_description(String role_description) {
        this.role_description = role_description;
    }

    // Utility methods for working with static roles
    public static List<Roles> getAllStaticRoles() {
        return List.of(CUSTOMER, EMPLOYEE, ADMIN);
    }

    public static Roles getRoleById(int id) {
        switch (id) {
            case 1: return CUSTOMER;
            case 2: return EMPLOYEE;
            case 3: return ADMIN;
            default: return null;
        }
    }

    public static Roles getRoleByName(String roleName) {
        switch (roleName.toLowerCase()) {
            case "customer": return CUSTOMER;
            case "employee": return EMPLOYEE;
            case "admin": return ADMIN;
            default: return null;
        }
    }

    public boolean isAdmin() {
        return this.id == ADMIN.id;
    }

    public boolean isEmployee() {
        return this.id == EMPLOYEE.id;
    }

    public boolean isCustomer() {
        return this.id == CUSTOMER.id;
    }

    @Override
    public String toString() {
        return "Roles{" +
                "id=" + id +
                ", role_name='" + role_name + '\'' +
                ", role_description='" + role_description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Roles roles = (Roles) obj;
        return id == roles.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
