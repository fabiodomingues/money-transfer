package com.xbank.moneytransfer.application.account;

import com.xbank.moneytransfer.domain.Account;

import java.util.Optional;

public interface AccountRepository {

	Optional<Account> withId(String id);

	void update(Account sourceAccount, Account destinationAccount);

	void add(Account account);
}
