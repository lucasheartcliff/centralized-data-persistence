package com.cda.service;

import com.cda.persistence.DatabaseContext;
import com.cda.persistence.TransactionHandler;
import com.cda.repository.RepositoryFactoryImpl;

public class ServiceFactory {
    private final RepositoryFactoryImpl repositoryFactory;
    private final DatabaseContext databaseContext;

    private TransactionHandler cachedTransactionHandler;

    public ServiceFactory(RepositoryFactoryImpl repositoryFactory, DatabaseContext  databaseContext) {
        this.repositoryFactory = repositoryFactory;
        this.databaseContext = databaseContext;
    }
    private TransactionHandler getTransactionHandler(){
        if(cachedTransactionHandler == null) cachedTransactionHandler = new TransactionHandler(databaseContext);
        return cachedTransactionHandler;
    }
}
