package com.xbank.moneytransfer.application;

import com.xbank.moneytransfer.domain.Transfer;

import java.util.Optional;
import java.util.UUID;

public interface TransferRepository {

	void add(Transfer transfer);

	void update(Transfer capture);

	Optional<Transfer> withId(UUID transferId);
}
