package com.xbank.moneytransfer.controller;


public class MoneyTransferStatusDTO {

	private String transferId;
	private String status;

	public String getTransferId() {
		return transferId;
	}

	public void setTransferId(String transferId) {
		this.transferId = transferId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
