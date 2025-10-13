package com.luxestay.hotel.service;

import com.luxestay.hotel.dao.AccountRepository;
import com.luxestay.hotel.dao.EmployeeRepository;
import com.luxestay.hotel.model.Account;
import com.luxestay.hotel.model.Employee;
import com.luxestay.hotel.model.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private AccountRepository accountRepository;

    public List<Employee> getAllEmployees(){
        return employeeRepository.findAll();
    }

    public Employee getEmployeeById(int id){
        return employeeRepository.findById(id).get();
    }

    public void addEmployee(Employee employee){
        try {
            // Handle Account creation/association
            if (employee.getAccount_email() != null && !employee.getAccount_email().trim().isEmpty()) {
                // Check if account already exists
                Account existingAccount = accountRepository.findByEmail(employee.getAccount_email());
                if (existingAccount != null) {
                    // Use existing account but update password if provided
                    if (employee.getPassword() != null && !employee.getPassword().trim().isEmpty()) {
                        existingAccount.setPassword(employee.getPassword());
                        accountRepository.save(existingAccount);
                    }
                    employee.setAccount(existingAccount);
                } else {
                    // Create new account
                    Account newAccount = new Account();
                    newAccount.setEmail(employee.getAccount_email());
                    newAccount.setUserName(employee.getAccount_email().split("@")[0]); // Use email prefix as username
                    newAccount.setPassword(employee.getPassword() != null && !employee.getPassword().trim().isEmpty() 
                        ? employee.getPassword() : "defaultPassword123");
                    newAccount.setIs_active(1);
                    
                    // Set employee role using static role
                    newAccount.setRoles(Roles.EMPLOYEE);
                    
                    // Save account first
                    Account savedAccount = accountRepository.save(newAccount);
                    employee.setAccount(savedAccount);
                }
            } else {
                throw new RuntimeException("Account email is required for employee creation");
            }
            
            // Set default status to active when creating new employee
            employee.setStatus(1);
            employeeRepository.save(employee);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to create employee: " + e.getMessage(), e);
        }
    }

    public void updateEmployee(Employee employee) {
        Employee existingEmployee = employeeRepository.findById(employee.getId()).orElseThrow();
        
        // Update basic fields
        if (employee.getEmployeeCode() != null) {
            existingEmployee.setEmployeeCode(employee.getEmployeeCode());
        }
        if (employee.getPosition() != null) {
            existingEmployee.setPosition(employee.getPosition());
        }
        if (employee.getDepartment() != null) {
            existingEmployee.setDepartment(employee.getDepartment());
        }
        if (employee.getSalary() != null) {
            existingEmployee.setSalary(employee.getSalary());
        }
        if (employee.getStatus() != existingEmployee.getStatus()) {
            existingEmployee.setStatus(employee.getStatus());
        }
        
        // Update password if provided
        if (employee.getPassword() != null && !employee.getPassword().trim().isEmpty()) {
            Account account = existingEmployee.getAccount();
            if (account != null) {
                account.setPassword(employee.getPassword());
                accountRepository.save(account);
            }
        }
        
        // Save the updated employee
        employeeRepository.save(existingEmployee);
    }

    public void deleteEmployee(int id){
        // Soft delete: set status to 0 instead of removing from database
        Employee employee = employeeRepository.findById(id).orElse(null);
        if (employee != null) {
            employee.setStatus(0);
            employeeRepository.save(employee);
        }
    }
}
