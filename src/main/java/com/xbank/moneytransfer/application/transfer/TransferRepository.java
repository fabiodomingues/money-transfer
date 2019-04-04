package com.xbank.moneytransfer.application.transfer;

import com.xbank.moneytransfer.domain.TransferRegistry;

import java.util.Optional;
import java.util.UUID;

public interface TransferRepository {

	void add(TransferRegistry transferRegistry);

	void update(TransferRegistry capture);

	Optional<TransferRegistry> withId(UUID transferId);
}
