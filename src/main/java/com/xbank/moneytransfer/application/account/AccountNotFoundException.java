package com.xbank.moneytransfer.application.account;

public class AccountNotFoundException extends RuntimeException {

	private final String accountNotFound;

	public AccountNotFoundException(String accountNotFound) {
		this.accountNotFound = accountNotFound;
	}

	@Override
	public String getMessage() {
		return "Account " + accountNotFound + " not found.";
	}
}
