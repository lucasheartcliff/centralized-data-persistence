package com.cda.service;

import com.cda.persistence.DatabaseContext;
import com.cda.persistence.TransactionHandler;
import com.cda.repository.RepositoryFactory;
import com.cda.service.message.MessageService;
import com.cda.service.message.MessageServiceImpl;
import com.cda.service.user.UserService;
import com.cda.service.user.UserServiceImpl;

public class ServiceFactory {
    private final RepositoryFactory repositoryFactory;
    private final DatabaseContext databaseContext;

    private TransactionHandler cachedTransactionHandler;

    public ServiceFactory(RepositoryFactory repositoryFactory, DatabaseContext  databaseContext) {
        this.repositoryFactory = repositoryFactory;
        this.databaseContext = databaseContext;
    }

    public UserService buildUserService() {
        return new UserServiceImpl(repositoryFactory, getTransactionHandler());
    }

    public MessageService buildMessageService() {
        return new MessageServiceImpl(repositoryFactory, getTransactionHandler());
    }

    private TransactionHandler getTransactionHandler(){
        if(cachedTransactionHandler == null) cachedTransactionHandler = new TransactionHandler(databaseContext);
        return cachedTransactionHandler;
    }
}
