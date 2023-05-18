package com.cda.persistence;


import javax.persistence.EntityManagerFactory;

import com.cda.configuration.ApplicationProperties;

public interface PersistenceService {
    EntityManagerFactory buildEntityManagerFactory(ApplicationProperties properties);
}
