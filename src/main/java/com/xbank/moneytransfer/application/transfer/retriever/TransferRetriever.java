package com.xbank.moneytransfer.application.transfer.retriever;

import com.xbank.moneytransfer.application.transfer.TransferRepository;
import com.xbank.moneytransfer.domain.TransferRegistry;

import java.util.Optional;
import java.util.UUID;

public class TransferRetriever {

	private TransferRepository transferRepository;

	public TransferRetriever(TransferRepository transferRepository) {
		this.transferRepository = transferRepository;
	}

	public TransferRegistry retrieveTransfer(String transferId) {
		Optional<TransferRegistry> transferOptional = transferRepository.withId(UUID.fromString(transferId));
		if (!transferOptional.isPresent()) {
			throw new TransferNotFoundException();
		}

		return transferOptional.get();
	}
}
