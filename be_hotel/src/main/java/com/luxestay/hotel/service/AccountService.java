package com.luxestay.hotel.service;

import com.luxestay.hotel.dao.AccountRepository;
import com.luxestay.hotel.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    public List<Account> getAllAccounts(){
        return accountRepository.findAll();
    }

    public Account getAccountById(int id){
        return accountRepository.findById(id).get();
    }

    public void addAccount(Account account){
        // Set default status to active when creating new account
        account.setIs_active(1);
        accountRepository.save(account);
    }

    public void deleteAccountById(int id){
        // Soft delete: set is_active to 0 instead of removing from database
        Account account = accountRepository.findById(id).orElse(null);
        if (account != null) {
            account.setIs_active(0);
            accountRepository.save(account);
        }
    }

    public void updateAccount(Account account){
        accountRepository.save(account);
    }

}
