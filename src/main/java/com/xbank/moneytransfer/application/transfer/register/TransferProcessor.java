package com.xbank.moneytransfer.application.transfer.register;

import com.xbank.moneytransfer.application.account.AccountNotFoundException;
import com.xbank.moneytransfer.application.account.AccountRepository;
import com.xbank.moneytransfer.application.transfer.TransferRepository;
import com.xbank.moneytransfer.domain.Account;
import com.xbank.moneytransfer.domain.TransferRegistry;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public class TransferProcessor {

	private AccountRepository accountRepository;
	private TransferRepository transferRepository;

	public TransferProcessor(AccountRepository accountRepository, TransferRepository transferRepository) {
		this.accountRepository = accountRepository;
		this.transferRepository = transferRepository;
	}

	public UUID transfer(String sourceAccountCode, String destinationAccountCode, double value) {
		TransferRegistry transferRegistry = createTransfer(sourceAccountCode, destinationAccountCode, value);
		execute(transferRegistry);
		return transferRegistry.getId();
	}

	private TransferRegistry createTransfer(String sourceAccountCode, String destinationAccountCode, double value) {
		TransferRegistry transferRegistry = new TransferRegistry(UUID.randomUUID());
		transferRegistry.setSourceAccount(loadAccount(sourceAccountCode));
		transferRegistry.setDestinationAccount(loadAccount(destinationAccountCode));
		transferRegistry.setAmount(BigDecimal.valueOf(value));
		transferRepository.add(transferRegistry);
		return transferRegistry;
	}

	private Account loadAccount(String account) {
		Optional<Account> accountOptional = accountRepository.withId(account);
		if (!accountOptional.isPresent())  {
			throw new AccountNotFoundException(account);
		}

		return accountOptional.get();
	}

	private void execute(TransferRegistry transferRegistry) {
		if (isTransferCannotBeCompleted(transferRegistry)) {
			cancelTransfer(transferRegistry);
			return;
		}

		transferRegistry.getSourceAccount().debit(transferRegistry.getAmount());
		transferRegistry.getDestinationAccount().credit(transferRegistry.getAmount());

		updateAccountsBalance(transferRegistry);
		completeTransfer(transferRegistry);
	}

	private boolean isTransferCannotBeCompleted(TransferRegistry transferRegistry) {
		return transferRegistry.getSourceAccount().getBalance().compareTo(transferRegistry.getAmount()) < 0;
	}

	private void cancelTransfer(TransferRegistry transferRegistry) {
		transferRegistry.cancel();
		transferRepository.update(transferRegistry);
	}

	private void updateAccountsBalance(TransferRegistry transferRegistry) {
		accountRepository.update(transferRegistry.getSourceAccount(), transferRegistry.getDestinationAccount());
	}

	private void completeTransfer(TransferRegistry transferRegistry) {
		transferRegistry.complete();
		transferRepository.update(transferRegistry);
	}

}
