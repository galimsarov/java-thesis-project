package main.service;

import main.model.helper.Account;

import java.util.List;

public interface UserService {
    List<Account> getAccounts();
}
