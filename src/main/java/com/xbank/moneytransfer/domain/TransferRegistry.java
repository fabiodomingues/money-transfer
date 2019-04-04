package com.xbank.moneytransfer.domain;

import java.math.BigDecimal;
import java.util.UUID;

public class TransferRegistry {

	public UUID id;
	private Account sourceAccount;
	private Account destinationAccount;
	private TransferStatus status;
	private BigDecimal amount;

	public TransferRegistry(UUID id) {
		this.id = id;
		this.status = TransferStatus.PENDING;
	}

	public UUID getId() {
		return id;
	}

	public TransferStatus getStatus() {
		return status;
	}

	public void complete() {
		this.status = TransferStatus.COMPLETED;
	}

	public void cancel() {
		this.status = TransferStatus.CANCELED;
	}

	public Account getSourceAccount() {
		return sourceAccount;
	}

	public void setSourceAccount(Account sourceAccount) {
		this.sourceAccount = sourceAccount;
	}

	public Account getDestinationAccount() {
		return destinationAccount;
	}

	public void setDestinationAccount(Account destinationAccount) {
		this.destinationAccount = destinationAccount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getAmount() {
		return this.amount;
	}
}
