package com.cda.repository;

import com.cda.persistence.DatabaseContext;

public class RepositoryFactoryImpl {
    private final DatabaseContext databaseContext;

    public RepositoryFactoryImpl(DatabaseContext databaseContext) {
        this.databaseContext = databaseContext;
    }
}
