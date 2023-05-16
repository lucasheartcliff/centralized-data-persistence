package com.cda.service;

import com.cda.persistence.TransactionHandler;
import com.cda.repository.RepositoryFactoryImpl;

public abstract class BaseService {
    protected final RepositoryFactoryImpl repositoryFactory;
    protected final TransactionHandler transactionHandler;

    protected  BaseService(RepositoryFactoryImpl repositoryFactory, TransactionHandler transactionHandler) {
        this.repositoryFactory = repositoryFactory;
        this.transactionHandler = transactionHandler;
    }
}
