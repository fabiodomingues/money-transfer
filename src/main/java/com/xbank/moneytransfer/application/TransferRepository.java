package com.xbank.moneytransfer.application;

import com.xbank.moneytransfer.domain.Transfer;

public interface TransferRepository {

	void add(Transfer transfer);

	void update(Transfer capture);
}
