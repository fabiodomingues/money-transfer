package com.xbank.moneytransfer.controller;

import com.xbank.moneytransfer.application.AccountBalanceNegativeException;
import com.xbank.moneytransfer.application.AccountCodeAlreadyUsedException;
import com.xbank.moneytransfer.application.AccountRepository;
import com.xbank.moneytransfer.domain.Account;

import java.math.BigDecimal;

public class AccountService {

	private AccountRepository accountRepository;

	public AccountService(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	public void createAccount(String accountCode, Double initialBalance) {
		checkPositiveInitialBalance(initialBalance);
		checkAccountCode(accountCode);

		BigDecimal initialAccountBalance = BigDecimal.valueOf(initialBalance);
		accountRepository.add(new Account(accountCode, initialAccountBalance));
	}

	private void checkAccountCode(String accountCode) {
		if (accountRepository.withId(accountCode).isPresent()) {
			throw new AccountCodeAlreadyUsedException();
		}
	}

	private void checkPositiveInitialBalance(Double initialBalance) {
		if (initialBalance < 0) {
			throw new AccountBalanceNegativeException();
		}
	}
}
