package com.xbank.moneytransfer.application;

import com.xbank.moneytransfer.domain.Account;
import com.xbank.moneytransfer.domain.Transfer;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public class MoneyTransferService {

	private AccountRepository accountRepository;
	private TransferRepository transferRepository;

	public MoneyTransferService(AccountRepository accountRepository, TransferRepository transferRepository) {
		this.accountRepository = accountRepository;
		this.transferRepository = transferRepository;
	}

	public UUID transfer(String sourceAccountCode, String destinationAccountCode, double value) {
		Transfer transfer = createTransfer(sourceAccountCode, destinationAccountCode, value);
		startTransfer(transfer);
		return transfer.getId();
	}

	private Transfer createTransfer(String sourceAccountCode, String destinationAccountCode, double value) {
		Transfer transfer = new Transfer(UUID.randomUUID());
		transfer.setSourceAccount(loadAccount(sourceAccountCode));
		transfer.setDestinationAccount(loadAccount(destinationAccountCode));
		transfer.setAmount(BigDecimal.valueOf(value));
		transferRepository.add(transfer);
		return transfer;
	}

	private Account loadAccount(String account) {
		Optional<Account> accountOptional = accountRepository.withId(account);
		if (!accountOptional.isPresent())  {
			throw new AccountNotFoundException(account);
		}

		return accountOptional.get();
	}

	private void startTransfer(Transfer transfer) {
		if (isTransferCannotBeCompleted(transfer)) {
			cancelTransfer(transfer);
			throw new InsufficientFundsException();
		}

		transfer.getSourceAccount().debit(transfer.getAmount());
		transfer.getDestinationAccount().credit(transfer.getAmount());

		updateAccountsBalance(transfer);
		completeTransfer(transfer);
	}

	private boolean isTransferCannotBeCompleted(Transfer transfer) {
		return transfer.getSourceAccount().getBalance().compareTo(transfer.getAmount()) < 0;
	}

	private void cancelTransfer(Transfer transfer) {
		transfer.cancel();
		transferRepository.update(transfer);
	}

	private void updateAccountsBalance(Transfer transfer) {
		accountRepository.update(transfer.getSourceAccount(), transfer.getDestinationAccount());
	}

	private void completeTransfer(Transfer transfer) {
		transfer.complete();
		transferRepository.update(transfer);
	}

}
