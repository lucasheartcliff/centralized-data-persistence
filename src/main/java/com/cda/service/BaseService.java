package com.cda.service;

import com.cda.persistence.TransactionHandler;
import com.cda.repository.RepositoryFactory;

public abstract class BaseService {
    protected final RepositoryFactory repositoryFactory;
    protected final TransactionHandler transactionHandler;

    protected  BaseService(RepositoryFactory repositoryFactory, TransactionHandler transactionHandler) {
        this.repositoryFactory = repositoryFactory;
        this.transactionHandler = transactionHandler;
    }
}