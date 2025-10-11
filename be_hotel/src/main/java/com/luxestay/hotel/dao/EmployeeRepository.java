package com.luxestay.hotel.dao;

import com.luxestay.hotel.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
}
