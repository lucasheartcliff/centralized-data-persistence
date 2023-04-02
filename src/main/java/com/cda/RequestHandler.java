package com.cda;

import com.cda.persistence.DatabaseContext;
import com.cda.persistence.DatabaseContextImpl;
import com.cda.repository.RepositoryFactory;
import com.cda.service.ServiceFactory;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManagerFactory;

@Service
public class RequestHandler {

  private final EntityManagerFactory entityManagerFactory;


  public RequestHandler(EntityManagerFactory entityManagerFactory) {
    this.entityManagerFactory = entityManagerFactory;
  }

  public DatabaseContext buildDatabaseContext() {
    return new DatabaseContextImpl(entityManagerFactory);
  }

  private RepositoryFactory buildRepositoryFactory(DatabaseContext databaseContext) {
    return new RepositoryFactory(databaseContext);
  }

  public ServiceFactory buildServiceFactory(DatabaseContext databaseContext) {
    RepositoryFactory repositoryFactory = buildRepositoryFactory(databaseContext);
    return new ServiceFactory(repositoryFactory, databaseContext);
  }

}
