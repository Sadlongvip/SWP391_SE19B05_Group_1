package com.luxestay.hotel.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.luxestay.hotel.dao.AccountRepository;
import com.luxestay.hotel.dao.EmployeeRepository;
import com.luxestay.hotel.dto.LoginRequest;
import com.luxestay.hotel.dto.LoginResponse;
import com.luxestay.hotel.model.Account;
import com.luxestay.hotel.model.Employee;
import com.luxestay.hotel.util.JwtUtil;

@Service
public class AuthService {
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    public LoginResponse authenticateUser(LoginRequest loginRequest) {
        String role = loginRequest.getRole();
        
        switch (role.toLowerCase()) {
            case "user":
                return authenticateCustomer(loginRequest);
            case "staff":
                return authenticateStaff(loginRequest);
            case "admin":
                return authenticateAdmin(loginRequest);
            default:
                throw new RuntimeException("Invalid role: " + role);
        }
    }
    
    private LoginResponse authenticateCustomer(LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("Email is required for customer login");
        }
        
        if (password == null || password.trim().isEmpty()) {
            throw new RuntimeException("Password is required for customer login");
        }
        
        Account account = accountRepository.findAll().stream()
                .filter(acc -> email.equalsIgnoreCase(acc.getEmail()) && acc.getIs_active() == 1)
                .findFirst()
                .orElse(null);
        
        if (account == null) {
            throw new RuntimeException("Account not found with email: " + email);
        }
        
        // Validate password
        if (!password.equals(account.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        
        String token = jwtUtil.generateToken(account.getEmail(), "user", account.getId());
        
        return new LoginResponse(token, "user", account.getId(), account.getUserName(), "email");
    }
    
    private LoginResponse authenticateStaff(LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        
        if ((email == null || email.trim().isEmpty()) && (username == null || username.trim().isEmpty())) {
            throw new RuntimeException("Email or username is required for staff login");
        }
        
        // Staff can login with either username OR password (not both required)
        Employee employee = employeeRepository.findAll().stream()
                .filter(emp -> {
                    Account acc = emp.getAccount();
                    if (acc == null || acc.getIs_active() != 1 || emp.getStatus() != 1) {
                        return false;
                    }
                    
                    boolean emailMatch = email != null && email.equalsIgnoreCase(acc.getEmail());
                    boolean usernameMatch = username != null && username.equals(acc.getUserName());
                    
                    return (emailMatch || usernameMatch);
                })
                .findFirst()
                .orElse(null);
        
        if (employee == null) {
            throw new RuntimeException("Staff account not found");
        }
        
        // If password is provided, validate it; otherwise just check username/email
        if (password != null && !password.trim().isEmpty()) {
            if (!password.equals(employee.getAccount().getPassword())) {
                throw new RuntimeException("Invalid password");
            }
        }
        
        String token = jwtUtil.generateToken(employee.getAccount().getEmail(), "staff", employee.getId());
        
        return new LoginResponse(token, "staff", employee.getId(), 
                employee.getAccount().getUserName(), "password");
    }
    
    private LoginResponse authenticateAdmin(LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        
        if ((email == null || email.trim().isEmpty()) && (username == null || username.trim().isEmpty())) {
            throw new RuntimeException("Email or username is required for admin login");
        }
        
        // Admin can login with either username OR password (not both required)
        boolean isAdmin = false;
        
        // Check username match
        if (username != null && "admin".equals(username)) {
            isAdmin = true;
        }
        
        // Check email match
        if (email != null && "admin@gmail.com".equals(email)) {
            isAdmin = true;
        }
        
        // If password is provided, validate it
        if (password != null && !password.trim().isEmpty()) {
            if (!"admin123".equals(password)) {
                throw new RuntimeException("Invalid password");
            }
        }
        
        if (!isAdmin) {
            throw new RuntimeException("Invalid admin credentials");
        }
        
        String token = jwtUtil.generateToken("admin", "admin", 0);
        
        return new LoginResponse(token, "admin", 0, "Administrator", "password");
    }
    
    public LoginResponse authenticateGoogleUser(String email, String name) {
        // Check if user exists, if not create a new account
        Account account = accountRepository.findAll().stream()
                .filter(acc -> email.equalsIgnoreCase(acc.getEmail()))
                .findFirst()
                .orElse(null);
        
        if (account == null) {
            // Create new account for Google user
            account = new Account();
            account.setEmail(email);
            account.setUserName(name);
            account.setIs_active(1);
            // Set a default role (you may need to adjust this based on your role system)
            accountRepository.save(account);
        }
        
        String token = jwtUtil.generateToken(account.getEmail(), "user", account.getId());
        
        return new LoginResponse(token, "user", account.getId(), account.getUserName(), "google");
    }
}
