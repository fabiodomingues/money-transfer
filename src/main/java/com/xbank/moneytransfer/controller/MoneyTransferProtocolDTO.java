package com.xbank.moneytransfer.controller;

import com.xbank.moneytransfer.domain.TransferStatus;

import java.util.UUID;

public class MoneyTransferProtocolDTO {

	private UUID protocolId;
	private TransferStatus status;

	public UUID getProtocolId() {
		return protocolId;
	}

	public void setProtocolId(UUID protocolId) {
		this.protocolId = protocolId;
	}

	public TransferStatus getStatus() {
		return status;
	}

	public void setStatus(TransferStatus status) {
		this.status = status;
	}
}
