package com.luxestay.hotel.dao;

import java.util.List;

import com.luxestay.hotel.model.Account;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class AccountDao {
    @PersistenceContext
    private EntityManager em;

    public void addAccount(Account account){
        em.persist(account);
    }

    public void updateAccount(Account account){
        em.merge(account);
    }
    
    public void deleteAccount(int id){
        Account account = em.find(Account.class, id);
        if(account != null) em.remove(account);
    }
    public Account getAccountById(int id){
        return em.find(Account.class, id);
    }
    
    public List<Account> getAllAccounts(){
        return em.createQuery("SELECT a FROM Account a", Account.class).getResultList();
    }
}
