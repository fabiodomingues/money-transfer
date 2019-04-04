package com.xbank.moneytransfer.infrastructure;

import com.xbank.moneytransfer.application.transfer.TransferRepository;
import com.xbank.moneytransfer.domain.TransferRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InMemoryTransferRepository implements TransferRepository {

	private Map<UUID, TransferRegistry> transferByUuid;

	InMemoryTransferRepository() {
		this.transferByUuid = new HashMap<>();
	}

	@Override
	public void add(TransferRegistry transferRegistry) {
		this.transferByUuid.put(transferRegistry.getId(), transferRegistry);
	}

	@Override
	public void update(TransferRegistry transferRegistry) {
		this.transferByUuid.put(transferRegistry.getId(), transferRegistry);
	}

	@Override
	public Optional<TransferRegistry> withId(UUID transferId) {
		return Optional.ofNullable(transferByUuid.get(transferId));
	}
}
