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
        employeeRepository.save(employee);
    }

    public void updateEmployee(Employee employee){
        employeeRepository.save(employee);
    }

    public void deleteEmployee(int id){
        employeeRepository.deleteById(id);
    }
}
