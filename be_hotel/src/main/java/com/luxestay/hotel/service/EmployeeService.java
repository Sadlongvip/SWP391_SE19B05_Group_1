package com.luxestay.hotel.service;

import com.luxestay.hotel.dao.EmployeeRepository;
import com.luxestay.hotel.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    public List<Employee> getAllEmployees(){
        return employeeRepository.findAll();
    }

    public Employee getEmployeeById(int id){
        return employeeRepository.findById(id).get();
    }

    public void addEmployee(Employee employee){
        // Set default status to active when creating new employee
        employee.setStatus(1);
        employeeRepository.save(employee);
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
