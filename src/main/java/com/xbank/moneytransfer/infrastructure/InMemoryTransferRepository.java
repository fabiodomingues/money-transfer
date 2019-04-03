package com.xbank.moneytransfer.infrastructure;

import com.xbank.moneytransfer.application.TransferRepository;
import com.xbank.moneytransfer.domain.Transfer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InMemoryTransferRepository implements TransferRepository {

	private Map<UUID, Transfer> transferByUuid;

	InMemoryTransferRepository() {
		this.transferByUuid = new HashMap<>();
	}

	@Override
	public void add(Transfer transfer) {
		this.transferByUuid.put(transfer.getId(), transfer);
	}

	@Override
	public void update(Transfer transfer) {
		this.transferByUuid.put(transfer.getId(), transfer);
	}

	@Override
	public Optional<Transfer> withId(UUID transferId) {
		return Optional.ofNullable(transferByUuid.get(transferId));
	}
}
