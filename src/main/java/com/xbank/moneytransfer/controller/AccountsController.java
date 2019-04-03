package com.xbank.moneytransfer.controller;

import com.xbank.moneytransfer.infrastructure.RepositoryFactory;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller("/accounts")
public class AccountsController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AccountsController.class);

	private AccountService accountService;

	public AccountsController() {
		this.accountService = new AccountService(RepositoryFactory.getAccountRepository());
	}

	@Post
	public MutableHttpResponse<Object> createAccount(AccountDTO accountDTO) {
		LOGGER.debug("Creating account " + accountDTO.getAccountCode() + " with [balance:" + accountDTO + "]");

		accountService.createAccount(accountDTO.getAccountCode(), accountDTO.getInitialBalance());

		return HttpResponse.ok();
	}
}
