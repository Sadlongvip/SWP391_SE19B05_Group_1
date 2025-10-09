package com.luxestay.hotel.dao;

import java.util.List;

import com.luxestay.hotel.model.Staff;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class StaffDao {
    @PersistenceContext
    private EntityManager em;

    public void addStaff(Staff staff){
        em.persist(staff);
    }

    public void updateStaff(Staff staff){
        em.merge(staff);
    }
    
    public void deleteStaff(int id){
        Staff staff = em.find(Staff.class, id);
        if(staff != null) em.remove(staff);
    }
    
    public Staff getStaffById(int id){
        return em.find(Staff.class, id);
    }
    
    public List<Staff> getAllStaffs(){
        return em.createQuery("SELECT s FROM Staff s", Staff.class).getResultList();
    }
}
