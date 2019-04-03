package com.xbank.moneytransfer.infrastructure;

import com.xbank.moneytransfer.application.AccountRepository;
import com.xbank.moneytransfer.domain.Account;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryAccountRepository implements AccountRepository {

 	private Map<String, Account> accountsByCode;

	InMemoryAccountRepository() {
		this.accountsByCode = new HashMap<>();
	}

	@Override
	public Optional<Account> withId(String id) {
		return Optional.ofNullable(accountsByCode.get(id));
	}

	@Override
	public void update(Account sourceAccount, Account destinationAccount) {
		//persist for non-in-memory-repositories
	}

	@Override
	public void add(Account account) {
		accountsByCode.put(account.getAccountCode(), account);
	}
}
