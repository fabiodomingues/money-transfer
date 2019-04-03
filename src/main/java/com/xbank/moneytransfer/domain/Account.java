package com.xbank.moneytransfer.domain;

import java.math.BigDecimal;
import java.util.Date;

public class Account {

	private String accountCode;
	private BigDecimal balance;
	private Date lastUpdate;

	public Account(String accountCode, BigDecimal balance) {
		this.balance = balance;
		this.accountCode = accountCode;
	}

	public String getAccountCode() {
		return accountCode;
	}

	public BigDecimal getBalance() {
		return balance == null ? BigDecimal.ZERO : balance;
	}

	public void credit(BigDecimal credit) {
		balance = balance.add(credit);
	}

	public void debit(BigDecimal debit) {
		balance = balance.subtract(debit);
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
}
