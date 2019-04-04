package com.xbank.moneytransfer.application.account;

import com.xbank.moneytransfer.application.transfer.register.AccountBalanceNegativeException;
import com.xbank.moneytransfer.application.transfer.register.AccountCodeAlreadyUsedException;
import com.xbank.moneytransfer.domain.Account;

import java.math.BigDecimal;
import java.util.Optional;

public class AccountHandler {

	private AccountRepository accountRepository;

	public AccountHandler(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	public void createAccount(String accountCode, Double initialBalance) {
		checkPositiveInitialBalance(initialBalance);
		checkAccountCode(accountCode);

		BigDecimal initialAccountBalance = BigDecimal.valueOf(initialBalance);
		accountRepository.add(new Account(accountCode, initialAccountBalance));
	}

	public Optional<Account> retrieveAccount(String accountCode) {
		return this.accountRepository.withId(accountCode);
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
