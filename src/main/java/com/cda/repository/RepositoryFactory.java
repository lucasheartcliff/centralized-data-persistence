package com.cda.repository;

import com.cda.persistence.DatabaseContext;
import com.cda.repository.message.MessageRepository;
import com.cda.repository.message.MessageRepositoryImpl;
import com.cda.repository.user.UserRepository;
import com.cda.repository.user.UserRepositoryImpl;

public class RepositoryFactory {
    private final DatabaseContext databaseContext;

    public RepositoryFactory(DatabaseContext databaseContext) {
        this.databaseContext = databaseContext;
    }

    public UserRepository buildUserRepository(){
        return new UserRepositoryImpl(databaseContext.getEntityManager());
    }

    public MessageRepository buildMessageRepository(){
        return new MessageRepositoryImpl(databaseContext.getEntityManager());
    }
}
