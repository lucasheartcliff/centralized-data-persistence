package com.cda.persistence;

import javax.persistence.EntityManagerFactory;

public interface PersistenceService {
    EntityManagerFactory buildEntityManagerFactory();
}
