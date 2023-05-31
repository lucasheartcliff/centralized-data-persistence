package com.cda.service;

import org.slf4j.Logger;

import com.cda.persistence.TransactionHandler;
import com.cda.repository.RepositoryFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseService {
  protected final RepositoryFactory repositoryFactory;
  protected final TransactionHandler transactionHandler;

  protected BaseService(
      RepositoryFactory repositoryFactory, TransactionHandler transactionHandler) {
    this.repositoryFactory = repositoryFactory;
    this.transactionHandler = transactionHandler;
  }

  protected Logger getLog(){
    return log;
  }
}
