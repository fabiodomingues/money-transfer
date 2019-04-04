package com.xbank.moneytransfer.infrastructure;

import com.xbank.moneytransfer.application.account.AccountRepository;
import com.xbank.moneytransfer.application.transfer.TransferRepository;

public final class RepositoryFactory {

	private static AccountRepository accountRepository;
	private static TransferRepository transferRepository;

	public static AccountRepository getAccountRepository() {
		return accountRepository == null ? (accountRepository = new InMemoryAccountRepository()) : accountRepository;
	}

	public static TransferRepository getTransferRepository() {
		return transferRepository == null ? (transferRepository = new InMemoryTransferRepository()) : transferRepository;
	}
}
